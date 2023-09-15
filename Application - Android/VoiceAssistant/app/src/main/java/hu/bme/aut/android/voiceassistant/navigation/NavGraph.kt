package hu.bme.aut.android.voiceassistant.navigation

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.android.voiceassistant.feature.BluetoothOnScreen
import hu.bme.aut.android.voiceassistant.feature.MainScreen
import hu.bme.aut.android.voiceassistant.feature.RecordVideoScreen
import hu.bme.aut.android.voiceassistant.feature.SearchWebScreen
import hu.bme.aut.android.voiceassistant.feature.SetAlarmScreen
import hu.bme.aut.android.voiceassistant.feature.SetTimerScreen
import hu.bme.aut.android.voiceassistant.feature.SettingsScreen
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

        composable(Screen.SearchWeb.route) {
            SearchWebScreen(query = "android", onBackPressed = {
                navController.popBackStack()
            })
            // TODO handle the query
        }

        composable(Screen.SetAlarm.route) {
            SetAlarmScreen(message = "android", time = "20:00", onBackPressed = {
                navController.popBackStack()
            })
            // TODO handle the query
        }

        composable(Screen.SetTimer.route) {
            SetTimerScreen(message = "timer", time = "00:02:15", onBackPressed = {
                navController.popBackStack()
            })
            // TODO handle the query
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
