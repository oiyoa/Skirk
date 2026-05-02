package app.skirk.client

import org.json.JSONObject

data class SkirkConfig(
    val sessionId: String,
    val routeMode: String,
    val spreadsheetId: String,
    val driveFolderId: String,
) {
    companion object {
        fun parse(raw: String): SkirkConfig {
            val root = JSONObject(raw)
            val route = root.optJSONObject("route") ?: JSONObject()
            val sheets = root.optJSONObject("sheets") ?: JSONObject()
            val drive = root.optJSONObject("drive") ?: JSONObject()
            return SkirkConfig(
                sessionId = root.optString("session_id"),
                routeMode = route.optString("mode", "direct"),
                spreadsheetId = sheets.optString("spreadsheet_id"),
                driveFolderId = drive.optString("folder_id"),
            )
        }
    }
}
