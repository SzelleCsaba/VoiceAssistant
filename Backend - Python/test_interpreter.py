'''
pip install pandas
pip install scikit-learn
pip install matplotlib
'''


import unittest
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay
import numpy as np
from api import TextProcessor, VectorDb

class TestFilterText(unittest.TestCase):
    def test_filter_text(self):
        # Test cases to verify the filter_text function
        test_cases = [
            # {"input": "play till i collapse from eminem", "expected": "play_music"},
            # {"input": "search the term segregation on the web", "expected": "search_web"},
            # {"input": "how many months are in a year?", "expected": "show_answer"},
            # {"input": "play pump up the jam", "expected": "play_music"},
            # {"input": "Call mother.", "expected": "start_call"},
            # {"input": "Send a text to Anna to dont forget to buy groceries.", "expected": "send_text"},
            # {"input": "open the settings", "expected": "settings"},
            # {"input": "open the wifi settings", "expected": "wifi_settings"},
            # {"input": "open the camera", "expected": "take_picture"},
            # {"input": "open the locale settings", "expected": "locale_settings"},
            # {"input": "open the internal storage settings", "expected": "internalstorage_settings"},
            # {"input": "turn on bluetooth", "expected": "bluetooth_on"},
            # {"input": "take a picture", "expected": "take_picture"},
            # {"input": "record a video", "expected": "record_video"},
            # {"input": "create a note to go to the hairdresser", "expected": "create_note"},
            # {"input": "add an event to my calendar on the 5th of august to take the trash out", "expected": "add_event"},
            # {"input": "remind me to buy groceries", "expected": "create_note"},
            # {"input": "play the music named bohemian rhapsody", "expected": "play_music"},
            # {"input": "search for medieval england on the internet", "expected": "search_web"},
            # {"input": "open the date settings", "expected": "date_settings"},
            # {"input": "search for something online", "expected": "search_web"},
            # {"input": "open display settings", "expected": "display_settings"},
            # {"input": "record a video", "expected": "record_video"},
            # {"input": "open security settings", "expected": "security_settings"},
            # {"input": "search for a term online", "expected": "search_web"},
            # {"input": "play the track titled Bohemian Rhapsody", "expected": "play_music"},
            # {"input": "turn on the bluetooth", "expected": "bluetooth_on"},
            # {"input": "set a timer for 10 minutes", "expected": "set_timer"},
            # {"input": "open camera", "expected": "take_picture"},
            # {"input": "open wifi settings", "expected": "wifi_settings"},
            # {"input": "open wireless settings", "expected": "wireless_settings"},
            # {"input": "open airplane mode settings", "expected": "airplane_settings"},
            # {"input": "open apn settings", "expected": "apn_settings"},
            # {"input": "open bluetooth settings", "expected": "bluetooth_settings"},
            # {"input": "open date settings", "expected": "date_settings"},
            # {"input": "open locale settings", "expected": "locale_settings"},
            # {"input": "open input settings", "expected": "input_settings"},
            # {"input": "open display settings", "expected": "display_settings"},
            # {"input": "open security settings", "expected": "security_settings"},
            # {"input": "open location settings", "expected": "location_settings"},
            # {"input": "open internal storage settings", "expected": "internalstorage_settings"},
            # {"input": "open memory card settings", "expected": "memorycard_settings"},
            # {"input": "Tell me a joke.", "expected": "show_answer"},
            # {"input": "Translate 'hello' to French.", "expected": "show_answer"},
            # {"input": "Find a recipe for lasagna.", "expected": "show_answer"},
            # {"input": "How do I tie a tie?", "expected": "show_answer"},
            # {"input": "Read me a bedtime story.", "expected": "show_answer"},
            # {"input": "What's the elevation of Mount Everest?", "expected": "show_answer"},
            # {"input": "What's the population of Tokyo?", "expected": "show_answer"},
            # {"input": "What's the distance to the moon?", "expected": "show_answer"},
            # {"input": "What's the capital of France?", "expected": "show_answer"},
            # {"input": "Tell me a fun fact.", "expected": "show_answer"},
            # {"input": "Tell me a historical fact.", "expected": "show_answer"},
            # {"input": "What's the chemical formula for water?", "expected": "show_answer"},
            # {"input": "Solve the equation: 2x + 3 = 7.", "expected": "show_answer"},
            # {"input": "Recommend a book to read.", "expected": "show_answer"},
            # {"input": "What's your favorite movie?", "expected": "show_answer"},
            # {"input": "Define 'onomatopoeia.'", "expected": "show_answer"},
            # {"input": "How many calories in a banana?", "expected": "show_answer"},
            # Add more test cases as needed
            {"input": "Játszd le a várnék című számot", "expected": "play_music"},
            {"input": "keress rá a szegregáció kifejezésre az interneten", "expected": "search_web"},
            {"input": "hány hónap van egy évben?", "expected": "show_answer"},
            {"input": "Hívd fel anyát.", "expected": "start_call"},
            {"input": "Küldj sms-t Annának, hogy ne felejtsen el boltba menni", "expected": "send_text"},
            {"input": "nyisd meg a beállításokat", "expected": "settings"},
            {"input": "nyissa meg a wifi beállításokat", "expected": "wifi_settings"},
            {"input": "nyissa meg a kamerát", "expected": "take_picture"},
            {"input": "nyisd mega helyi beállításokat", "expected": "locale_settings"},
            {"input": "nyissa meg a belső tárhely beállításokat", "expected": "internalstorage_settings"},
            {"input": "kapcsold be a bluetooth-ot", "expected": "bluetooth_on"},
            {"input": "készíts egy képet", "expected": "take_picture"},
            {"input": "rögzíts egy videót", "expected": "record_video"},
            {"input": "készíts egy jegyzetet, hogy menjek el fodrászhoz", "expected": "create_note"},
            {"input": "adj hozzá egy eseményt a naptáramhoz augusztus 5-ére, hogy vigyem ki a szemetet", "expected": "add_event"},
            {"input": "emlékeztess arra, hogy vásároljak élelmiszert", "expected": "create_note"},
            {"input": "játsszd le a Bohemian Rhapsody nevű zenét", "expected": "play_music"},
            {"input": "keress rá a középkori Angliára az interneten", "expected": "search_web"},
            {"input": "nyisd meg a dátum beállításokat", "expected": "date_settings"},
            {"input": "nyisd meg a kijelző beállításokat", "expected": "display_settings"},
            {"input": "rögzíts egy videót", "expected": "record_video"},
            {"input": "nyisd meg a biztonsági beállításokat", "expected": "security_settings"},
            {"input": "kapcsold be a bluetooth-ot", "expected": "bluetooth_on"},
            {"input": "állíts be egy időzítőt 10 percre", "expected": "set_timer"},
            {"input": "nyisd meg a kamerát", "expected": "take_picture"},
            {"input": "nyissa meg a wifi beállításokat", "expected": "wifi_settings"},
            {"input": "nyissa meg a vezeték nélküli beállításokat", "expected": "wireless_settings"},
            {"input": "nyisd meg a repülőgép üzemmód beállításokat", "expected": "airplane_settings"},
            {"input": "nyissa meg az APN beállításokat", "expected": "apn_settings"},
            {"input": "nyissa meg a bluetooth beállításokat", "expected": "bluetooth_settings"},
            {"input": "nyissa meg a dátum beállításokat", "expected": "date_settings"},
            {"input": "nyissa meg a helyi beállításokat", "expected": "locale_settings"},
            {"input": "nyissa meg a bemeneti beállításokat", "expected": "input_settings"},
            {"input": "nyissa meg a kijelző beállításokat", "expected": "display_settings"},
            {"input": "nyissa meg a biztonsági beállításokat", "expected": "security_settings"},
            {"input": "nyissa meg a helymeghatározási beállításokat", "expected": "location_settings"},
            {"input": "nyissa meg a belső tárhely beállításokat", "expected": "internalstorage_settings"},
            {"input": "nyissa meg a memóriakártya beállításokat", "expected": "memorycard_settings"},
            {"input": "Mondd egy viccet.", "expected": "show_answer"},
            {"input": "Fordítsd le a 'szia'-t francia nyelvre.", "expected": "show_answer"},
            {"input": "Keress egy receptet a lasagna-hoz.", "expected": "show_answer"},
            {"input": "Hogyan kössünk nyakkendőt?", "expected": "show_answer"},
            {"input": "Mondj egy esti mesét nekem.", "expected": "show_answer"},
            {"input": "Mennyi a Mount Everest magassága?", "expected": "show_answer"},
            {"input": "Mennyi a tokiói népesség?", "expected": "show_answer"},
            {"input": "Mennyi a távolság a Holdig?", "expected": "show_answer"},
            {"input": "Mi a francia főváros?", "expected": "show_answer"},
            {"input": "Mondj egy szórakoztató tényt.", "expected": "show_answer"},
            {"input": "Mondj egy történelmi tényt.", "expected": "show_answer"},
            {"input": "Mi a víz kémiai képlete?", "expected": "show_answer"},
            {"input": "Oldd meg az egyenletet: 2x + 3 = 7.", "expected": "show_answer"},
            {"input": "Ajánlj egy könyvet olvasásra.", "expected": "show_answer"},
            {"input": "Mi a kedvenc filmed?", "expected": "show_answer"},
            {"input": "Mi a szegregáció definíciója", "expected": "show_answer"},
            {"input": "Hány kalória van egy banánban?", "expected": "show_answer"}
        ]

        # GitHub Desktop default folder szcsa is the Windows username
        app_folder = r"C:\Users\szcsa\Documents\GitHub\SzakDoga\Backend - Python\misc" 
        vdb = VectorDb(app_folder)
        tp = TextProcessor(vdb)

        predicted_labels = []  # Store the predicted labels
        actual_labels = []  # Store the actual labels

        for test_case in test_cases:
            with self.subTest(msg=f"Testing '{test_case['input']}'"):
                tp = TextProcessor(vdb)  # Initialize TextProcessor with the appropriate VectorDb
                result = tp.filter_text(test_case['input'])
                predicted_labels.append(result['name'])
                actual_labels.append(test_case['expected'])

                self.assertEqual(result['name'], test_case['expected'])

        # Count the number of passed and failed test cases
        passed_count = sum(1 for pred, actual in zip(predicted_labels, actual_labels) if pred == actual)
        failed_count = len(actual_labels) - passed_count

        # Data for the bar chart
        labels = ['Passed', 'Failed']
        counts = [passed_count, failed_count]

        # Create the bar chart
        plt.bar(labels, counts, color=['green', 'red'])
        plt.xlabel('Test Results')
        plt.ylabel('Number of Test Cases')
        plt.title('Test Case Results')

        # Display the bar chart
        plt.show()


        # Convert labels to binary (0 for show_answer, 1 for others)
        actual_labels_binary = np.array([0 if label == "show_answer" else 1 for label in actual_labels])
        predicted_labels_binary = np.array([0 if label == "show_answer" else 1 for label in predicted_labels])


        # Create a confusion matrix
        cm = confusion_matrix(actual_labels_binary, predicted_labels_binary, labels=[0, 1])

        classes = ["Generating answer", "Executing"]
        display = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=classes)
        display.plot(cmap=plt.cm.Blues)  # Display percentages with 2 decimal places
        plt.xlabel('Predicted')
        plt.ylabel('Actual')
        plt.title('Confusion Matrix')
        plt.show()


        cm = cm / cm.sum(axis=-1)[:, np.newaxis]

        # Visualize the confusion matrix with percentages
        classes = ["Generating answer", "Executing"]
        display = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=classes)
        display.plot(cmap=plt.cm.Blues, values_format=".2f")  # Display percentages with 2 decimal places
        plt.xlabel('Predicted')
        plt.ylabel('Actual')
        plt.title('Confusion Matrix (Percentages)')
        plt.show()


if __name__ == '__main__':
    unittest.main()
