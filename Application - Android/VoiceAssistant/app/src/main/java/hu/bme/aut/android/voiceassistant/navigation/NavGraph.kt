package hu.bme.aut.android.voiceassistant.navigation

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hu.bme.aut.android.voiceassistant.feature.BluetoothOnScreen
import hu.bme.aut.android.voiceassistant.feature.CreateNoteScreen
import hu.bme.aut.android.voiceassistant.feature.MainScreen
import hu.bme.aut.android.voiceassistant.feature.RecordVideoScreen
import hu.bme.aut.android.voiceassistant.feature.SearchWebScreen
import hu.bme.aut.android.voiceassistant.feature.SendTextScreen
import hu.bme.aut.android.voiceassistant.feature.SetAlarmScreen
import hu.bme.aut.android.voiceassistant.feature.SetTimerScreen
import hu.bme.aut.android.voiceassistant.feature.SettingsScreen
import hu.bme.aut.android.voiceassistant.feature.StartCallScreen
import hu.bme.aut.android.voiceassistant.feature.TakePictureScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen { route ->
                navController.navigate(route)
            }
        }

        composable(Screen.TakePicture.route) {
            TakePictureScreen(onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.RecordVideo.route) {
            RecordVideoScreen(onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(
            Screen.SearchWeb.route + "?query={query}",
            arguments = listOf(
                navArgument("query") { defaultValue = "android"}
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")

            SearchWebScreen(query = query ?: "android",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.SetAlarm.route + "?message={message}&time={time}",
            arguments = listOf(
                navArgument("message") { defaultValue = "alarm" },
                navArgument("time") { defaultValue = "12:00" }
            )
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")
            val time = backStackEntry.arguments?.getString("time")

            SetAlarmScreen(
                message = message ?: "alarm",
                time = time ?: "12:00",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.SetTimer.route + "?message={message}&time={time}",
            arguments = listOf(
                navArgument("message") { defaultValue = "timer" },
                navArgument("time") { defaultValue = "00:05:00" }
            )
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")
            val time = backStackEntry.arguments?.getString("time")

            SetTimerScreen(
                message = message ?: "timer",
                time = time ?: "00:05:00",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BluetoothOn.route) {
            BluetoothOnScreen(onBackPressed = {
                navController.popBackStack()
            })
        }


        composable(Screen.Settings.route) {
            SettingsScreen(Settings.ACTION_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.WirelessSettings.route) {
            SettingsScreen(Settings.ACTION_WIRELESS_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.AirplaneSettings.route) {
            SettingsScreen(Settings.ACTION_AIRPLANE_MODE_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.WifiSettings.route) {
            SettingsScreen(Settings.ACTION_WIFI_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.ApnSettings.route) {
            SettingsScreen(Settings.ACTION_APN_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.BluetoothSettings.route) {
            SettingsScreen(Settings.ACTION_BLUETOOTH_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.DateSettings.route) {
            SettingsScreen(Settings.ACTION_DATE_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(
            Screen.CreateNote.route + "?text={text}&subject={subject}",
            arguments = listOf(
                navArgument("text") { defaultValue = "empty note" },
                navArgument("subject") { defaultValue = "Note" }
            )
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text")
            val subject = backStackEntry.arguments?.getString("subject")

            CreateNoteScreen(
                subject = subject ?: "Note",
                text = text ?: "",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.SendText.route + "?name={name}&message={message}",
            arguments = listOf(
                navArgument("name") { defaultValue = "name" },
                navArgument("message") { defaultValue = "message" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val message = backStackEntry.arguments?.getString("message")

            SendTextScreen(
                name = name ?: "",
                message = message ?: "",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.StartCall.route + "?name={name}",
            arguments = listOf(
                navArgument("name") { defaultValue = "name" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            StartCallScreen(
                name = name ?: "",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }



        /* TODO ezeket megcsinálni, mint a többit
    ACTION_LOCALE_SETTINGS
    ACTION_INPUT_METHOD_SETTINGS
    ACTION_DISPLAY_SETTINGS
    ACTION_SECURITY_SETTINGS
    ACTION_LOCATION_SOURCE_SETTINGS
    ACTION_INTERNAL_STORAGE_SETTINGS
    ACTION_MEMORY_CARD_SETTINGS
         */
    }
}
