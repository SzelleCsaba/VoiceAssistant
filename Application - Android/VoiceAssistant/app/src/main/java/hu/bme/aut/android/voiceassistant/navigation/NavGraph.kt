package hu.bme.aut.android.voiceassistant.navigation

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hu.bme.aut.android.voiceassistant.R
import hu.bme.aut.android.voiceassistant.feature.TextToSpeech
import hu.bme.aut.android.voiceassistant.feature.screens.AddEventScreen
import hu.bme.aut.android.voiceassistant.feature.screens.BluetoothOnScreen
import hu.bme.aut.android.voiceassistant.feature.screens.CreateNoteScreen
import hu.bme.aut.android.voiceassistant.feature.screens.MainScreen
import hu.bme.aut.android.voiceassistant.feature.screens.PlayMusicScreen
import hu.bme.aut.android.voiceassistant.feature.screens.RecordVideoScreen
import hu.bme.aut.android.voiceassistant.feature.screens.SearchWebScreen
import hu.bme.aut.android.voiceassistant.feature.screens.SendTextScreen
import hu.bme.aut.android.voiceassistant.feature.screens.SetAlarmScreen
import hu.bme.aut.android.voiceassistant.feature.screens.SetTimerScreen
import hu.bme.aut.android.voiceassistant.feature.screens.SettingsScreen
import hu.bme.aut.android.voiceassistant.feature.screens.ShowAnswerScreen
import hu.bme.aut.android.voiceassistant.feature.screens.StartCallScreen
import hu.bme.aut.android.voiceassistant.feature.screens.TakePictureScreen



@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val langCode = stringResource(R.string.language_code)
    val countryCode = stringResource(R.string.country_code)
    val context = LocalContext.current
    lateinit var tts: TextToSpeech

    LaunchedEffect(Unit){
        tts = TextToSpeech(context, langCode, countryCode)
    }

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

        composable(Screen.LocaleSettings.route) {
            SettingsScreen(Settings.ACTION_LOCALE_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.InputSettings.route) {
            SettingsScreen(Settings.ACTION_INPUT_METHOD_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.DisplaySettings.route) {
            SettingsScreen(Settings.ACTION_DISPLAY_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.SecuritySettings.route) {
            SettingsScreen(Settings.ACTION_SECURITY_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.LocationSettings.route) {
            SettingsScreen(Settings.ACTION_LOCATION_SOURCE_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.InternalStorageSettings.route) {
            SettingsScreen(Settings.ACTION_INTERNAL_STORAGE_SETTINGS ,onBackPressed = {
                navController.popBackStack()
            })
        }

        composable(Screen.MemoryCardSettings.route) {
            SettingsScreen(Settings.ACTION_MEMORY_CARD_SETTINGS ,onBackPressed = {
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

        composable(Screen.PlayMusic.route + "?query={query}",
            arguments = listOf(
                navArgument("query") { defaultValue = "query" }
            )
        ) {backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")
            PlayMusicScreen(
                query ?: "",
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }

        composable(Screen.ShowAnswer.route + "?answer={answer}",
            arguments = listOf(
                navArgument("answer") { defaultValue = "answer" }
            )
        ) {backStackEntry ->
            val answer = backStackEntry.arguments?.getString("answer")
            ShowAnswerScreen(
                answer ?: "",
                onBackPressed = {
                    navController.popBackStack()
                },
                tts = tts
            )
        }

        composable(
            Screen.AddEvent.route + "?title={title}&date={date}",
            arguments = listOf(
                navArgument("title") { defaultValue = "title" },
                navArgument("date") { defaultValue = "date" }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            val date = backStackEntry.arguments?.getString("date")

            AddEventScreen(
                title = title ?: "",
                date = date ?: "",
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

    }
}
