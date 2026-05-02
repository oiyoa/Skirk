package app.skirk.client

import android.net.VpnService
import android.content.Intent
import android.os.ParcelFileDescriptor

class SkirkVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connect()
        return START_STICKY
    }

    fun connect() {
        vpnInterface = Builder()
            .setSession("Skirk")
            .addAddress("10.19.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("1.1.1.1")
            .establish()
    }

    override fun onDestroy() {
        vpnInterface?.close()
        vpnInterface = null
        super.onDestroy()
    }
}
