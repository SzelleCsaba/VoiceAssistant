package hu.bme.aut.android.voiceassistant.feature

import android.Manifest
import android.app.SearchManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.content.Context
import android.database.Cursor
import com.google.android.gms.actions.NoteIntents
import java.util.Random
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult

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

        fun callPerson(name: String) {
            val phoneNumber: String = getPhoneNumberByName(this, name)
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        fun sendText(name: String, message: String) {
            val phoneNumber: String = getPhoneNumberByName(this, name)
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("sms:$phoneNumber")
                    putExtra("sms_body", message)
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        private fun getPhoneNumberByName(context: Context, name: String): String {
            // Initialize a ContentResolver to query the contacts
            val contentResolver: ContentResolver = context.contentResolver

            // Define the columns you want to retrieve
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Create a selection clause to filter contacts by name
            val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(name)

            // Query the contacts database
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    // Retrieve the phone number
                    val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    return it.getString(phoneNumberIndex)
                }
            }

            // If the contact with the given name is not found, return an empty string
            return ""
        }


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


*/
        // TODO  kérdezz rá
        fun toggleWiFi(enable: Boolean) {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = enable
        }





}