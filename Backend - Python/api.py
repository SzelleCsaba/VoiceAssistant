import os
import logging

from flask import Flask, request, jsonify
from flasgger import Swagger

import command_resolver
import decision_agent
import answer_agent
import cache
import whisper_speech_to_text

import os

# TODO: Install the flasgger package
# !pip install flasgger

# set it in case you want a Cache
CACHE_ENABLED = True
    
app = Flask(__name__)
swagger = Swagger(app)
log_dir = os.path.join(os.path.expanduser("~"), "RestAPI_logs")
os.makedirs(log_dir, exist_ok=True)

logger = logging.getLogger('werkzeug')
logging.basicConfig(
                filename=os.path.join(log_dir, "RestAPI.log"),
                format="%(message)s",
                filemode="a",
                encoding="UTF-8")


cr = command_resolver.CommandResolver()
da = decision_agent.DecisionAgent()
aa = answer_agent.AnswerAgent()
cache = cache.Cache()
stt = whisper_speech_to_text.WhisperSpeechToText()

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
    
            res = [
                {
                    "type": "simple_answer"
                },
                answer
            ]
    except Exception as e:
        logger.error(["e", str(e)])
        
        data = [
            {
                "type": "error"
            },
            None
        ]
        return data
    
    return res


@app.route('/health', methods=['GET'])
async def health_check():
    """
    Health check endpoint
    ---
    responses:
        200:
            description: OK
    """
    return "OK", 200


@app.route('/text', methods=['POST'])
async def interpret_text():
    """
    Interprets a user prompt from a text
    ---
    parameters:
      - name: text
        in: body
        required: true
        schema:
          type: object
          properties:
            text:
              type: string
              example: "What is the weather like today?"

    responses:
        200:
            description: The result for the prompt
            schema:
                type: object
                properties:
                    type:
                        type: string
                        example: "simple_answer"
                    answer:
                        type: string
                        example: "The weather is sunny today."
        400:
            description: No text provided
    """

    if 'text' not in request.json:
        return "No text provided", 400
        
    text = request.json.get('text')
    
    res = await interpret_prompt(text)
    
    return jsonify(res)

@app.route('/voice', methods=['POST'])
async def interpret_voice():
    """
    Interprets a user prompt from an audio file
    ---
    parameters:
        - name: audio
            in: formData
            type: file
            required: true
            description: The audio file to interpret
        - name: lang
            in: formData
            type: string
            required: false
            description: The language of the audio file (default: en)
    responses:
        200:
            description: The result for the prompt
            schema:
                type: object
                properties:
                    type:
                        type: string
                        example: "simple_answer"
                    answer:
                        type: string
                        example: "The weather is sunny today."
        400:
            description: No audio file provided
    """
    if 'audio' not in request.files:
        return "No audio file provided", 400

    audio_file = request.files['audio']
    lang = request.form.get('lang', 'en')

    try:
        text = stt.recognize(audio_file, lang)

    except Exception as e:
        logger.error(["e", str(e)])
        text = ""

    res = await interpret_prompt(text)

    return jsonify(res)


@app.errorhandler(Exception)
def handle_exception(e):
    logger.error(["e", str(e)])
    return str(e), 500

