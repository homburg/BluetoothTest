package dk.homburg.bluetoothtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_btdevice.*

class BTDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btdevice)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val device: BTDevice? = intent.getParcelableExtra<BTDevice>("device")

        if (device == null) {
            finish();
        } else {
            name.text = device.name
            address.text = device.address
            description.text = device.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return true
    }
}
