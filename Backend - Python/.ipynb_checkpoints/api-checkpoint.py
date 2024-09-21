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

import command_resolver
import decision_agent
import answer_agent
import cache


# set it in case you want a Cache
CACHE_ENABLED = False
    

app = Flask(__name__)
log_dir = os.path.join(os.path.expanduser("~"), "RestAPI_logs")
os.makedirs(log_dir, exist_ok=True)

log = logging.getLogger('werkzeug')
#log.setLevel(logging.ERROR)
logging.basicConfig(
                filename=os.path.join(log_dir, "RestAPI.log"),
                format="%(message)s",
                filemode="a",
                encoding="UTF-8")


cr = command_resolver.CommandResolver()
da = decision_agent.DecisionAgent()
aa = answer_agent.AnswerAgent()
cache = cache.Cache()

async def interpret_prompt(text: str) -> dict:
    """Interpets the user prompt from the point it is a text

    Args:
        text (str): The piece of text to interpret.

    Returns:
        dict: The result for the prompt
    """

    if CACHE_ENABLED:
        cached_response = cache.get(text)
        if cached_response:
            return cached_response

    try:
        if (await da.decide(text)):
            res = await cr.select(text)
            if CACHE_ENABLED:
                cache.add(text, res)
        else:
            answer = await aa.answer(text)
    
            res = {
                "name": "show_answer",
                "arguments": "{\n\"answer\": \"" + answer + "\"\n}"
            }
    except e:
        logger.error(["e", str(e)])
        
        data = {
            "name": "show_answer",
            "arguments": "{\n\"answer\": \"None\"\n}"
        }
    return res


@app.route('/text', methods=['POST'])
async def interpret_text():
    text = request.json.get('text')
    
    res = await interpret_prompt(text)
    
    return jsonify(res)

@app.route('/voice', methods=['POST'])
def interpret_voice():
    # TODO https://learn.microsoft.com/en-us/azure/ai-services/openai/whisper-quickstart?tabs=command-line%2Cpython-new%2Cjavascript&pivots=programming-language-python
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

