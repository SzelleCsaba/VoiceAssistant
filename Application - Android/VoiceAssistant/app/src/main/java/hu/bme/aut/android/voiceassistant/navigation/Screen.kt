package hu.bme.aut.android.voiceassistant.navigation

sealed class Screen(val route: String) {
    object Main: Screen("main")
    object TakePicture: Screen("take_picture")
    object RecordVideo: Screen("record_video")

    object SearchWeb: Screen("search_web")

    object SetAlarm: Screen("set_alarm")
    object SetTimer: Screen("set_timer")

    object BluetoothOn: Screen("bluetooth_on")

    object Settings: Screen("settings")
    object AirplaneSettings: Screen("airplane_settings")
    object WirelessSettings: Screen("wireless_settings")
    object WifiSettings: Screen("wifi_settings")
    object ApnSettings: Screen("apn_settings")
    object BluetoothSettings: Screen("bluetooth_settings")
    object DateSettings: Screen("date_settings")
    object LocaleSettings: Screen("locale_settings")
    object InputSettings: Screen("input_settings")
    object DisplaySettings: Screen("display_settings")
    object SecuritySettings: Screen("security_settings")
    object LocationSettings: Screen("location_settings")
    object InternalStorageSettings: Screen("internalstorage_settings")
    object MemoryCardSettings: Screen("memorycard_settings")

    object CreateNote: Screen("create_note")

    object SendText: Screen("send_text")
    object StartCall: Screen("start_call")

    object ShowAnswer: Screen("show_answer")


}
