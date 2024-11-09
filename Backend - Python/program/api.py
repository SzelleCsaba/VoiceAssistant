import logging
import sys

from flask import Flask, request, jsonify
from flasgger import Swagger

import command_resolver
import decision_agent
import answer_agent
import cache
import whisper_speech_to_text


# set it in case you want a Cache
CACHE_ENABLED = True
    
app = Flask(__name__)
swagger = Swagger(app, template={
    "info": {
        "title": "Personal Assistant API",
        "description": "API documentation for a Personal Assistant powered by state-of-the-art AI solutions",
        "version": "1.0.0",
        "contact": {
            "name": "Csaba Szelle",
            "email": "sz.csaba.2002@gmail.com"
        },
    },
    "tags": [
        {
            "name": "Assistant",
            "description": "General operations related to the personal assistant."
        },
    ]
})

logger = logging.getLogger('werkzeug')
logging.basicConfig(
    stream=sys.stdout,
    format="%(message)s",
    level=logging.INFO,
)


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
    tags:
      - Assistant
    responses:
        200:
            description: OK - the Rest API is running
            schema:
                type: string
                example: "OK"
    """
    return "OK", 200


@app.route('/text', methods=['POST'])
async def interpret_text():
    """
    Interprets a user prompt from a text
    ---
    tags:
      - Assistant
    parameters:
      - in: body
        name: body
        required: true
        schema:
          type: object
          properties:
            text:
              type: string
          example:
            text: "Set an alarm for 16:00"

    responses:
      200:
        description: The result for the prompt
        schema:
              type: array
              items:
                oneOf:
                  - type: object
                    properties:
                      args:
                        type: object
                        properties:
                          time:
                            type: string
                            example: "16:00"
                      id:
                        type: string
                        example: "call_hgsfhdgx"
                      name:
                        type: string
                        example: "set_alarm"
                      type:
                        type: string
                        example: "tool_call"
                  - type: string
                    example: "Sure, I will set an alarm for 16:00."
      400:
        description: Bad Request - No text provided
        schema:
            type: string
            example: "No text provided"
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
    tags:
      - Assistant
    parameters:
        - in: formData
          name: audio
          type: file
          required: true
          description: The audio file to interpret
        - in: formData
          name: lang
          type: string
          required: false
          description: The language of the audio file (default is en)

    responses:
      200:
        description: The result for the prompt
        schema:
              type: array
              items:
                oneOf:
                  - type: object
                    properties:
                      args:
                        type: object
                        properties:
                          time:
                            type: string
                            example: "16:00"
                      id:
                        type: string
                        example: "call_hgsfhdgx"
                      name:
                        type: string
                        example: "set_alarm"
                      type:
                        type: string
                        example: "tool_call"
                  - type: string
                    example: "Sure, I will set an alarm for 16:00."
      400:
        description: Bad Request - No audio file provided
        schema:
            type: string
            example: "No text provided"
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

