import os
import json

class Cache:
    """A class that handles storing and retrieving key-value pairs from a JSON file.
    
    This class uses a JSON file to cache data and supports both string and JSON (dict) values.
    If a value is a dictionary, it is serialized into a JSON string before being stored.
    """
    
    def __init__(self, filename='cache.json'):
        """Initializes the Cache object and loads any existing key-value pairs from the JSON file.
        
        Args:
            filename (str, optional): The name of the JSON file to use for caching. Defaults to 'cache.json'.
        """
        self.filename = filename
        self.pairs = {}

        # If the cache file exists, load the key-value pairs into memory
        if os.path.exists(self.filename):
            with open(self.filename, 'r', encoding='utf-8') as f:
                self.pairs = json.load(f)

    def add(self, key: str, value) -> None:
        """Adds a new key-value pair to the cache. If the value is a dictionary, it is converted to a JSON string.
        
        Args:
            key (str): The key to associate with the cached value.
            value (dict or str): The value to store in the cache. It can be a string or a dictionary.
        """
        # Convert dictionary values to a JSON string for storage
        if isinstance(value, dict):
            value = json.dumps(value)

        # Add the key-value pair to the cache and write to the JSON file
        self.pairs[key] = value
        with open(self.filename, 'w', encoding='utf-8') as f:
            json.dump(self.pairs, f, ensure_ascii=False, indent=4)

    def get(self, key: str):
        """Retrieves the value associated with the given key from the cache.
        
        Args:
            key (str): The key for which to retrieve the cached value.
        
        Returns:
            dict or str: The cached value. If the value is a JSON string (representing a dict or list), it is deserialized before being returned.
        """
        # Fetch the value from the cache
        value = self.pairs.get(key)

        # If the value is found, check if it is JSON and deserialize if necessary
        if value is not None:
            # Check if the value is a JSON string and deserialize if necessary
            try:
                return json.loads(value) if isinstance(value, str) else value
            except json.JSONDecodeError:
                return value

        return None  # Return None if the key is not found
