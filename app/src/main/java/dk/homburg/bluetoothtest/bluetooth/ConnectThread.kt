package dk.homburg.bluetoothtest.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import timber.log.Timber
import java.io.IOException
import java.util.*

abstract class ConnectThread(private val mBluetoothAdapter: BluetoothAdapter, private val mmDevice: BluetoothDevice) : Thread() {
    private val mmSocket: BluetoothSocket
    private val uuid: UUID = UUID.fromString("8ec51273-c07a-43a2-9aa5-31b8bed2cc85")

    abstract fun manageConnectedSocket(socket: BluetoothSocket)

    init {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        var tmp: BluetoothSocket? = null

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // uuid is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid)
        } catch (e: IOException) {
            Timber.e(e)
        }

        Timber.d("Created rfcomm socket %s", tmp)
        mmSocket = tmp!!
    }

    override fun run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery()

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            Timber.d("Connecting")
            mmSocket.connect()
            Timber.d("Connected")
        } catch (connectException: IOException) {
            // Unable to connect; close the socket and get out
            Timber.e(connectException)
            try {
                mmSocket.close()
            } catch (closeException: IOException) {
                Timber.e(closeException)
            }

            return
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket)
    }

    /** Will cancel an in-progress connection, and close the socket  */
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            Timber.e(e)
        }

    }
}
