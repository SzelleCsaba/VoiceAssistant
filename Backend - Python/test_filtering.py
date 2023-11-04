import unittest
from api import VectorDb

class TestGetTopKMatch(unittest.TestCase):
    def test_get_topk_match(self):
        # Test cases to verify the get_topk_match function
        test_cases = [
            {"input": "play till i collapse from eminem", "k": 5, "expected": "play_music"},
            {"input": "search the term segregation on the web", "k": 3, "expected": "search_web"},
            {"input": "play pump up the jam", "k": 2, "expected": "play_music"},
            {"input": "Call mother.", "k": 2, "expected": "start_call"},
            {"input": "Send a text to Anna to dont forget to buy groceries.", "k": 2, "expected": "send_text"},
            {"input": "open the settings", "k": 2, "expected": "settings"},
            {"input": "open the wifi settings", "k": 2, "expected": "wifi_settings"},
            {"input": "open the camera", "k": 2, "expected": "take_picture"},
            {"input": "open the locale settings", "k": 2, "expected": "locale_settings"},
            {"input": "open the internal storage settings", "k": 2, "expected": "internalstorage_settings"},
            {"input": "turn on bluetooth", "k": 2, "expected": "bluetooth_on"},
            {"input": "take a picture", "k": 3, "expected": "take_picture"},
            {"input": "record a video", "k": 2, "expected": "record_video"},
            {"input": "create a note to go to the hairdresser", "k": 2, "expected": "create_note"},
            {"input": "add an event to my calendar on the 5th of august to take the trash out", "k": 2, "expected": "add_event"},
            {"input": "remind me to buy groceries", "k": 2, "expected": "create_note"},
            {"input": "play the music named bohemian rhapsody", "k": 2, "expected": "play_music"},
            {"input": "search for medieval england on the internet", "k": 2, "expected": "search_web"},
            {"input": "open the date settings", "k": 2, "expected": "date_settings"},
            {"input": "search for something online", "k": 2, "expected": "search_web"},
            {"input": "open display settings", "k": 2, "expected": "display_settings"},
            {"input": "record a video", "k": 2, "expected": "record_video"},
            {"input": "open security settings", "k": 2, "expected": "security_settings"},
            {"input": "search for a term online", "k": 2, "expected": "search_web"},
            {"input": "play the track titled Bohemian Rhapsody", "k": 2, "expected": "play_music"},
            {"input": "turn on the bluetooth", "k": 2, "expected": "bluetooth_on"},
            {"input": "set a timer for 10 minutes", "k": 2, "expected": "set_timer"},
            {"input": "open camera", "k": 2, "expected": "take_picture"},
            {"input": "open wifi settings", "k": 2, "expected": "wifi_settings"},
            {"input": "open wireless settings", "k": 2, "expected": "wireless_settings"},
            {"input": "open airplane mode settings", "k": 2, "expected": "airplane_settings"},
            {"input": "open apn settings", "k": 2, "expected": "apn_settings"},
            {"input": "open bluetooth settings", "k": 2, "expected": "bluetooth_settings"},
            {"input": "open date settings", "k": 2, "expected": "date_settings"},
            {"input": "open locale settings", "k": 2, "expected": "locale_settings"},
            {"input": "open input settings", "k": 2, "expected": "input_settings"},
            {"input": "open display settings", "k": 2, "expected": "display_settings"},
            {"input": "open security settings", "k": 2, "expected": "security_settings"},
            {"input": "open location settings", "k": 2, "expected": "location_settings"},
            {"input": "open internal storage settings", "k": 2, "expected": "internalstorage_settings"},
            {"input": "open memory card settings", "k": 2, "expected": "memorycard_settings"},
        ]

        # GitHub Desktop default folder
        app_folder = r"C:\Users\szcsa\Documents\GitHub\SzakDoga\Backend - Python\misc" 
        vdb = VectorDb(app_folder)

        for test_case in test_cases:
            with self.subTest(msg=f"Testing '{test_case['input']}'"):
                result = vdb.get_top_k_match(test_case['input'], k = 2)
                match_names = [match.name for match in result]

                # Check if the expected result is in the list of match names
                self.assertIn(test_case['expected'], match_names)

if __name__ == '__main__':
    unittest.main()
