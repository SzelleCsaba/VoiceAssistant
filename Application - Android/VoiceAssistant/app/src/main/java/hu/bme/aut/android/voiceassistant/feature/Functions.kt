package hu.bme.aut.android.voiceassistant.feature

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager


class Functions : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        // This service does not provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    /*
        fun createNote(subject: String = "note", text: String) {
            val intent = Intent(NoteIntents.ACTION_CREATE_NOTE).apply {
                putExtra(NoteIntents.EXTRA_NAME, subject)
                putExtra(NoteIntents.EXTRA_TEXT, text)
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        fun flipCoin() {
            val random = Random()
            val result = if (random.nextBoolean()) "Heads" else "Tails"
            // TODO visualize the result
        }

*/
        // TODO nem fog működni, kérdezz rá
        fun startAppByName(context: Context, appName: String) {
            val packageManager: PackageManager = context.packageManager
            val packages = packageManager.getInstalledPackages(PackageManager.MATCH_ALL)

            for (packageInfo in packages) {
                val appLabel = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
                if (appLabel.equals(appName, ignoreCase = true)) {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName)
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                        return
                    }
                }
            }

            // If the app was not found, you can handle the error here.
            // Example: Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
        }

    //----------------------------------first line
}