package app.skirk.client

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkirkApp()
        }
    }
}

@Composable
fun SkirkApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F7F4),
        ) {
            ConfigScreen()
        }
    }
}

@Composable
fun ConfigScreen() {
    val context = LocalContext.current
    var rawConfig by remember { mutableStateOf("") }
    var parsed by remember { mutableStateOf<SkirkConfig?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val vpnPermission = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Skirk", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Import one client config, then connect through the native VPN service.")

        OutlinedTextField(
            value = rawConfig,
            onValueChange = { rawConfig = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 8,
            label = { Text("client.json") },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                try {
                    parsed = SkirkConfig.parse(rawConfig)
                    error = null
                } catch (ex: Exception) {
                    parsed = null
                    error = ex.message
                }
            }) {
                Text("Import")
            }
            Button(onClick = {
                val intent: Intent? = VpnService.prepare(context)
                if (intent != null) {
                    vpnPermission.launch(intent)
                } else {
                    context.startService(Intent(context, SkirkVpnService::class.java))
                }
            }) {
                Text("Connect")
            }
        }

        parsed?.let { config ->
            StatusCard(config)
        }
        error?.let {
            Text(it, color = Color(0xFFB42318))
        }
    }
}

@Composable
fun StatusCard(config: SkirkConfig) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Config loaded", fontWeight = FontWeight.SemiBold)
            Text("Route: ${config.routeMode}")
            Text("Session: ${config.sessionId}")
            Text("Sheet: ${config.spreadsheetId}")
            Text("Drive folder: ${config.driveFolderId}")
        }
    }
}
