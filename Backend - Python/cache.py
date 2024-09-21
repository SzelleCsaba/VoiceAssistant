import os
import csv
import json

class Cache:
    """A class that handles storing and retrieving key-value pairs from a CSV file.
    
    This class uses a CSV file to cache data and supports both string and JSON (dict) values.
    If a value is a dictionary, it is serialized into a JSON string before being stored.
    """
    
    def __init__(self, filename='cache.csv'):
        """Initializes the Cache object and loads any existing key-value pairs from the CSV file.
        
        Args:
            filename (str, optional): The name of the CSV file to use for caching. Defaults to 'cache.csv'.
        """
        self.filename = filename
        self.pairs = {}

        # If the cache file exists, load the key-value pairs into memory
        if os.path.exists(self.filename):
            with open(self.filename, 'r', encoding='utf-8') as f:
                reader = csv.reader(f)
                self.pairs = {rows[0]: rows[1] for rows in reader if len(rows) == 2}

    def add(self, key: str, value: dict) -> None:
        """Adds a new key-value pair to the cache. If the value is a dictionary, it is converted to a JSON string.
        
        Args:
            key (str): The key to associate with the cached value.
            value (dict): The value to store in the cache. It can be a string or a dictionary.
        """
        # Convert dictionary values to a JSON string for storage
        if isinstance(value, dict):
            value = json.dumps(value)

        # Add the key-value pair to the cache and write to the CSV file
        self.pairs[key] = value
        with open(self.filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            for key, value in self.pairs.items():
                writer.writerow([key, value])

    def get(self, key: str) -> dict:
        """Retrieves the value associated with the given key from the cache.
        
        Args:
            key (str): The key for which to retrieve the cached value.
        
        Returns:
            dict: The cached value. If the value is a JSON string (representing a dict or list), it is deserialized before being returned.
        """
        # Fetch the value from the cache
        value = self.pairs.get(key)

        # If the value is found, check if it is JSON and deserialize if necessary
        if value is not None:
            if value.startswith('{') or value.startswith('['):
                return json.loads(value)
            else:
                return value