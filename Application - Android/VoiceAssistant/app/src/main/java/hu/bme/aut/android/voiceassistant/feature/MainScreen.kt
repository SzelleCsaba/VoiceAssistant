package hu.bme.aut.android.voiceassistant.feature

import android.content.Context
import android.util.Log
import hu.bme.aut.android.voiceassistant.domain.api.ApiFunctions
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.android.voiceassistant.domain.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onSendClick: (String) -> Unit) {
    var routeText by remember { mutableStateOf("") }
    val apiService = ApiClient.instance
    val apiFunctions = ApiFunctions(apiService)
    val context = LocalContext.current

    val isProcessing = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                Modifier.size(width = 360.dp, height = 200.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TextField(
                        value = routeText,
                        onValueChange = { routeText = it },
                        label = { Text("Enter your command") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            isProcessing.value = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = apiFunctions.interpretText(routeText)
                                withContext(Dispatchers.Main) {
                                    isProcessing.value = false
                                    handleRoute(response, context, onSendClick)
                                }
                            }
                        },
                    ) {
                        Text("Run")
                    }
                }
            }
            Spacer(modifier = Modifier.height(200.dp))

            Button(
                modifier = Modifier.size(200.dp), // Set the size of the button
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 15.dp,
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = {}                             //TODO hangfelvétel
            ) {
                Text(
                    text = "Voice",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (isProcessing.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

private fun handleRoute(response: String?, context: Context, onSendClick: (String) -> Unit) {
    if (response != null) {
        // Do something with the response
        Log.i("res", response)

        // Safety net
        if (response.length > 10) {
            val jsonObject = JSONObject(response)
            val name = jsonObject.getString("name")

            val argumentsString = jsonObject.getString("arguments")
            val argumentsJsonObject = JSONObject(argumentsString)
            val arguments = mutableMapOf<String, String>()
            val keys = argumentsJsonObject.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val value = argumentsJsonObject.getString(key)
                arguments[key] = value
            }

            if (arguments.isEmpty()) {
                onSendClick(name) // Call the onSendClick function with the name parameter
            } else {
                val concatenatedArguments =
                    arguments.map { "${it.key}=${it.value}" }
                        .joinToString("&")
                onSendClick("$name?$concatenatedArguments") // Call the onSendClick function with the concatenated arguments
            }
        } else {
            Toast.makeText(
                context,
                "There is no function to do it!",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(
            context,
            "Something went wrong",
            Toast.LENGTH_SHORT
        ).show()
    }
}

