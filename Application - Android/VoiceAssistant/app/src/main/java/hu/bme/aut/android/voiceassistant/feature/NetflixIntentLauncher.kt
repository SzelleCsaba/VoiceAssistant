package hu.bme.aut.android.voiceassistant.feature

import android.content.Context
import android.content.Intent
import android.net.Uri

class NetflixIntentLauncher(intentData: String, onBackPressed: () -> Unit) : IntentLauncher(intentData, onBackPressed) {
    override val intent = Intent(Intent.ACTION_VIEW).apply {
        setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity")
        data = Uri.parse("http://www.netflix.com/watch/$intentData")
    }

    override fun onAppNotInstalled(context: Context) {
        // Open Netflix in the web browser if the app is not installed.
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.netflix.com/watch/$intentData"))
        if (webIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(webIntent)
        }
    }
}