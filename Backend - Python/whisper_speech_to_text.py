import os
from openai import OpenAI

class WhisperSpeechToText(SpeechToText):
    client = OpenAI()

    @classmethod
    def recognize(cls, audio_file, lang):
        """
        Recognizes speech from the given audio file uploaded via a request using OpenAI's Whisper model.
        
        Args:
            audio_file: The audio file to be processed.
            lang: The language of the speech in the file.
        
        Returns:
            str: The recognized text or an error message.
        """

        try:
            # Save the uploaded file to the current working directory
            uploaded_filename = audio_file.filename
            upload_path = os.path.join(os.getcwd(), uploaded_filename)
            audio_file.save(upload_path)

            helpwords = ""

            # Help table for hard commands, different for each language
            if lang == 'hu':
                helpwords = "játszd le, keress rá"
            elif lang == 'en':
                helpwords = "play, search"

            # Recognize speech from the saved audio file
            
            with open(upload_path, 'rb') as file:
                # Call the OpenAI API for speech recognition
                transcription = cls.client.audio.transcriptions.create(
                    model="whisper-1",
                    file=file,
                    response_format="text"
                )
            return transcription.text

        except Exception as e:
            raise Exception(e)