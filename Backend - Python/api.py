# -*- coding: utf-8 -*-

def install_dependencies() -> None:
    import subprocess
    import pkg_resources

    required_dependencies = [
        "faiss-cpu",
        "openai",
        "numpy",
        "pandas",
        "langdetect",
        "flask",
        "sentence-transformers",
    ]

    installed_packages = {pkg.key for pkg in pkg_resources.working_set}
    missing_dependencies = [dep for dep in required_dependencies if dep not in installed_packages]

    if missing_dependencies:
        for dependency in missing_dependencies:
            subprocess.check_call(['pip', 'install', dependency])

install_dependencies()

import re
import csv
import os
import logging
from typing import Union, Dict
from dataclasses import dataclass
import json

import faiss
import openai
import numpy as np
import pandas as pd
from langdetect import detect
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer


config = {
    "hugging_face_model": "sentence-transformers/all-MiniLM-L6-v2",
    "command_data_path": "commands",
    "index_column_name": "Example",
    "enable_translation": True,
    "enable_cache": False
}


class Cache:
    def __init__(self, filename='cache.csv'):
        self.filename = filename
        self.pairs = {}
        self.enabled = config.get("enable_cache")

        if os.path.exists(self.filename):
            with open(self.filename, 'r', encoding='utf-8') as f:
                reader = csv.reader(f)
                self.pairs = {rows[0]: rows[1] for rows in reader if len(rows) == 2}

    def add(self, key: str, value) -> None:
        if self.enabled:
            if isinstance(value, dict):
                value = json.dumps(value)

            self.pairs[key] = value
            with open(self.filename, 'w', newline='', encoding='utf-8') as f:
                writer = csv.writer(f)
                for key, value in self.pairs.items():
                    writer.writerow([key, value])

    def get(self, key: str):
        if self.enabled:
            value = self.pairs.get(key)
            if value is not None:
                if value.startswith('{') or value.startswith('['):
                    return json.loads(value)
                else:
                    return value

@dataclass
class Parameter:
    name: str = None
    type: str = None
    required: int = None
    description: str = None

@dataclass
class Match:
    confidence: float
    example: str
    name: str
    description: str
    param1: Parameter = Parameter()
    param2: Parameter = Parameter()

class VectorDb:
    def __init__(self, app_folder) -> None:
        self.config = config

        self.model = SentenceTransformer(self.config.get("hugging_face_model"))

        data_path = f"{os.path.join(app_folder,self.config.get('command_data_path'))}.csv"
        self.data = pd.read_csv(data_path, header=0, sep=';')

        self.index_path = f"{os.path.join(app_folder,self.config.get('command_data_path'))}.index"
        if not os.path.exists(self.index_path):
            self._build_index()
        self.index = faiss.read_index(self.index_path)

    def _preprocess(self, text: str) -> str:
        text = re.sub(r"[^\w\s]", "", text)
        return text.lower()

    def _build_index(self):
        df = self.data

        df[self.config.get("index_column_name")] = df[self.config.get(
            "index_column_name")].apply(self._preprocess)

        df["Embedding"] = list(self.model.encode(
            df[self.config.get("index_column_name")].to_numpy()))

        # 'd' is the dimensionality of the vectors
        d = len(df["Embedding"][0])

        # Euclidean distance - faiss.IndexFlatL2(d)
        # Euclidean cosine - faiss.IndexFlatIP(d) + normalization
        index = faiss.IndexFlatIP(d)

        vectors = np.array(list(df["Embedding"].to_numpy()), dtype=np.float32)
        vectors /= np.linalg.norm(vectors, axis=1, keepdims=True)
        index.add(vectors)

        faiss.write_index(index, self.index_path)

    def _text_to_embedding(self, text: str) -> np.ndarray:
        text = self._preprocess(text)
        embedding = self.model.encode(text)
        embedding /= np.linalg.norm(embedding, axis=0, keepdims=True)
        return embedding

    def get_top_k_match(self, text: str, k: int = 10) -> list[Match]:
        embedding = self._text_to_embedding(text)
        distances, indices = self.index.search(
            np.expand_dims(embedding, axis=0), k)
        distances = distances[0]
        indices = indices[0]
        ret = []
        for dist, i in zip(distances, indices):

            
            if not pd.isnull(self.data["Param2"][i]) and self.data["Param2"][i] != "": # 2 params
                param1 = Parameter(self.data["Param1"][i], self.data["Param1Type"][i], self.data["Param1Required"][i], self.data["Param1Description"][i])
                param2 = Parameter(self.data["Param2"][i], self.data["Param2Type"][i], self.data["Param2Required"][i], self.data["Param2Description"][i])
                ret.append(Match(dist, self.data["Example"][i],  self.data["Name"][i], self.data["Description"][i], param1, param2))
            
            elif not pd.isnull(self.data["Param1"][i]) and self.data["Param1"][i] != "": # 1 param
                param1 = Parameter(self.data["Param1"][i], self.data["Param1Type"][i], self.data["Param1Required"][i], self.data["Param1Description"][i])
                ret.append(Match(dist, self.data["Example"][i],  self.data["Name"][i], self.data["Description"][i], param1))
            
            else:   # no params
                ret.append(Match(dist, self.data["Example"][i],  self.data["Name"][i], self.data["Description"][i]))
        
        return ret



class TextProcessor:
    def __init__(self, vector_db: VectorDb) -> None:
        self.translation_enabled = config.get("enable_translation")
        self.db = vector_db
        self.gpt_run_cache = Cache('gpt_run_cache.csv')

        openai.api_key = os.getenv("OPENAI_API_KEY")

    def filter_text(self, text: str) -> Union[str, None, Dict]:
        original_text = text
        if len(original_text) < 4:
            return None
        
        if self.translation_enabled:
            text = self._translate_gpt(text)

        k = 2
        results = self.db.get_top_k_match(text, k)
        max_score = results[0].confidence

        if (max_score < 0.33 and results[0].name != "search_web" and results[0].name != "play_music"  and results[0].name != "send_text"):
            answer = self._ask_gpt(original_text)
            data = {
                "name": "show_answer",
                "arguments": "{\n\"answer\": \"" + answer + "\"\n}"
            }
            return data
        else:
            res = self._process_text_gpt(text, results, original_text)

            if res:
                return res

            else:
                data = {
                    "name": "show_answer",
                    "arguments": "{\n\"answer\": \"None\"\n}"
                }
                return data

    def _process_text_gpt(self, prompt: str, matches: list[Match], original_prompt: str) -> Union[str, None]:
        if prompt == "":
            return None
        
        # Check the cache first
        cached_response = self.gpt_run_cache.get(original_prompt)
        if cached_response:
            return cached_response

        functions = []

        for match in matches:
            function = {
                "name": match.name,
                "description": match.description,
                "parameters": {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
            }

            # Add parameters to the function
            if match.param1.name:
                function["parameters"]["properties"][match.param1.name] = {
                    "type": match.param1.type,
                    "description": match.param1.description
                }
                if match.param1.required == 1:
                    function["parameters"]["required"].append(match.param1.name)

            if match.param2.name:
                function["parameters"]["properties"][match.param2.name] = {
                    "type": match.param2.type,
                    "description": match.param2.description
                }
                if match.param2.required == 1:
                    function["parameters"]["required"].append(match.param2.name)

            functions.append(function)

        messages = [
            {
                "role": "user",
                "content": original_prompt,
            }
        ]
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=messages,
            functions=functions,
            function_call="auto",
            temperature=0,
        )
        response_message = response["choices"][0]["message"]

        if response_message.get("function_call"):
            function = response_message["function_call"]

            self.gpt_run_cache.add(original_prompt, function)

            return function

        return None

    def _translate_gpt(self, text: str) -> str:
        if detect(text) != 'en':
            prompt = (
                "Translate "
                "\"{} \""
                "to english as a voice command given to a voice assistant!\n"
                "Just answer with the translation!"
            ).format(text)           
            messages = [
                {
                    "role": "user",
                    "content": prompt
                }
            ]
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                temperature=0,
                messages=messages
            )
            translation = response['choices'][0]['message']['content']
            translation = translation.replace('"', '')

            return translation
        else:
            return text
        
    def _ask_gpt(self, text: str) -> str:
        system_prompt = ("You are a helpful voice assistant, answer on the language that on the prompt is!\n"
            "Answer in less than 3 sentences, if you can not, then say you cant do it, without refering to languages.\n")          
        messages = [
            {
                "role": "system",
                "content": system_prompt
            },
            {
                "role": "user",
                "content": text
            }
        ]
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            temperature=0.8,
            messages=messages
        )
        answer = response['choices'][0]['message']['content']
        answer = answer.replace('"', '')

        return answer
    

app = Flask(__name__)
log_dir = os.path.join(os.path.expanduser("~"), "RestAPI_logs")
os.makedirs(log_dir, exist_ok=True)

log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)
logging.basicConfig(
                filename=os.path.join(log_dir, "RestAPI.log"),
                format="%(message)s",
                filemode="a",
                encoding="UTF-8")
logger = logging.getLogger()
logger.setLevel(logging.ERROR)

# GitHub Desktop default folder szcsa is the Windows username
app_folder = r"C:\Users\szcsa\Documents\GitHub\SzakDoga\Backend - Python\misc" 
vdb = VectorDb(app_folder)
tp = TextProcessor(vdb)
    

@app.route('/text', methods=['POST'])
def interpret_text():
    text = request.json.get('text')
    res = tp.filter_text(text)
    #logger.critical(["t", text, res])
    return jsonify(res)

@app.route('/voice', methods=['POST'])
def interpret_voice():
    try:
        # Check if the request has the 'audio' field with an uploaded file
        if 'audio' not in request.files:
            return "No audio file provided", 400

        audio_file = request.files['audio']

        # Check if the file has a valid extension
        if audio_file and audio_file.filename.endswith(('.wav', '.mp3', ".mp4")):
            # Save the uploaded file to the current working directory
            uploaded_filename = audio_file.filename
            upload_path = os.path.join(os.getcwd(), uploaded_filename)
            audio_file.save(upload_path)


            audio_file = open(upload_path, "rb")
            # Process the audio data here
            lang = request.form.get('lang', 'en')  # Get the language parameter

            helpwords = ""
            # Help table for hard commands, different for each language
            if lang == 'hu':
                helpwords = "játszd le, keress rá"
            if lang == 'en':
                helpwords = "play, search"

            result = openai.Audio.transcribe("whisper-1", audio_file, language=lang, prompt = helpwords)
            text = result["text"]
            text = text.replace('"', '')
            res = tp.filter_text(text)
            
            #logger.critical(["t", text, res])
            return jsonify(res)
        else:
            return "Invalid audio file format", 400
    except Exception as e:
        logger.error(["e", str(e)])
        return str(e), 500

@app.errorhandler(Exception)
def handle_exception(e):
    logger.error(["e", str(e)])
    return str(e), 500

