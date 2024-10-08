package hu.bme.aut.android.voiceassistant.feature.screens

import android.app.SearchManager
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.voiceassistant.R

@Composable
fun SearchWebScreen(query: String, onBackPressed: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
        onBackPressed()
    }

    Text(
        text = stringResource(R.string.launching_web_search_for, query),
        modifier = Modifier.padding(16.dp)
    )
}
