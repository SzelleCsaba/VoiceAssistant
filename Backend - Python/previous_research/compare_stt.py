import logging
from datetime import datetime
from google.api_core.client_options import ClientOptions
from google.cloud.speech_v2 import SpeechClient
from google.cloud.speech_v2.types import cloud_speech
import wave
import pyaudio
import keyboard
import time
import openai

def transcribe_chirp(project_id: str, audio_file: str) -> cloud_speech.RecognizeResponse:
    client = SpeechClient(
        client_options=ClientOptions(api_endpoint="us-central1-speech.googleapis.com")
    )

    with open(audio_file, "rb") as f:
        content = f.read()

    recognizer_id = f"projects/{project_id}/locations/us-central1/recognizers/_"

    config = cloud_speech.RecognitionConfig(
        auto_decoding_config=cloud_speech.AutoDetectDecodingConfig(),
        language_codes=["en-US"],
        model="chirp",
    )

    request = cloud_speech.RecognizeRequest(
        recognizer=recognizer_id,
        config=config,
        content=content,
    )

    response = client.recognize(request=request)

    transcripts = []
    for result in response.results:
        for alternative in result.alternatives:
            transcripts.append(alternative.transcript)

    return ' '.join(transcripts)

def transcribe_whisper(file_path: str):
    with open(file_path, 'rb') as audio_file:
        result = openai.Audio.transcribe("whisper-1", audio_file, language="en")
        text = result["text"]
        text = text.replace('"', '')
        return text

def record_audio():
    CHUNK = 1024
    FORMAT = pyaudio.paInt16
    CHANNELS = 2
    RATE = 44100
    
    timestamp_str = datetime.now().strftime("%Y-%m-%d_%H_%M_%S")
    WAVE_OUTPUT_FILENAME = f"{timestamp_str}.mp3"  # Name the output file as the current timestamp

    p = pyaudio.PyAudio()

    stream = p.open(format=FORMAT,
                    channels=CHANNELS,
                    rate=RATE,
                    input=True,
                    frames_per_buffer=CHUNK)

    print("Press and hold 'x' to start recording, release to stop")

    frames = []
    num_chunks = 0
    
    while True:
        if keyboard.is_pressed('x'):  # if key 'x' is pressed 
            print('Recording...')
            data = stream.read(CHUNK)
            frames.append(data)
            num_chunks += 1
            
            # Stop recording after MAX_RECORD_SECONDS
            if num_chunks >= (44100 / CHUNK * 15):
                print("Maximum recording duration reached, stopping...")
                break

        else:
            if len(frames) > 0:
                print('Recording stopped')
                break
            else:
                continue

    print("* done recording")

    stream.stop_stream()
    stream.close()
    p.terminate()

    wf = wave.open(WAVE_OUTPUT_FILENAME, 'wb')
    wf.setnchannels(CHANNELS)
    wf.setsampwidth(p.get_sample_size(FORMAT))
    wf.setframerate(RATE)
    wf.writeframes(b''.join(frames))
    wf.close()

    return WAVE_OUTPUT_FILENAME

    
# Configure logger at the start of the script
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(message)s',
                    datefmt='%m/%d/%Y %I:%M:%S %p',
                    filename='transcript.log',
                    filemode='a')
logger = logging.getLogger() 

filename = record_audio()
transcription_whisper = transcribe_whisper(filename)
logger.info(f"Whisper transcript: {transcription_whisper}")  # Log whisper transcripts
transcription_chirp = transcribe_chirp("-----", filename)
logger.info(f"Chirp transcript: {transcription_chirp}")  # Log chirp transcripts