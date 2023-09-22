package hu.bme.aut.android.voiceassistant.feature

import android.util.Log
import hu.bme.aut.android.voiceassistant.domain.api.ApiFunctions
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = routeText,
            onValueChange = { routeText = it },
            label = { Text("Enter Route") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = apiFunctions.interpretText(routeText)
                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            // Do something with the response
                            Log.i("res", response)

                            //safety net
                            if (response.length > 10){
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
                                    onSendClick(name)
                                }
                                else {
                                    val concatenatedArguments = arguments.map { "${it.key}=${it.value}" }.joinToString("&")
                                    onSendClick("$name?$concatenatedArguments")
                                    //Toast.makeText(context, "$name?$concatenatedArguments", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                Toast.makeText(context, "There is no function to do it!", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                //TODO megcsinálni a Screenekben, hogy miután végeztek a dolgukkal, térjenek vissza a mainre.

                //onSendClick(routeText)
                },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Send")
        }
    }
}
