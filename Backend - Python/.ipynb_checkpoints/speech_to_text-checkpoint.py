from abc import ABC, abstractmethod

class SpeechToText(ABC):
    
    @classmethod
    @abstractmethod
    def recognize(cls, audio_file, lang):
        """
        Recognizes speech from the given audio file.
        
        Args:
            audio_file: The audio file to be processed.
            lang: The language of the speech in the file.
        
        Returns:
            str: The recognized text.
        """
        pass