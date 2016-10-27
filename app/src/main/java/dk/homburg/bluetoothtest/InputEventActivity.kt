package dk.homburg.bluetoothtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import dk.homburg.bluetoothtest.ui.TheAdapter
import timber.log.Timber

class InputEventActivity : AppCompatActivity() {
    data class InputEvent(val date: String, val event: android.view.InputEvent)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_event)

        val actionBar = supportActionBar!!
        actionBar.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment, InputEventFragment())
                .commit()
    }

    val  inputEventAdapter = TheAdapter(R.layout.list_item, ::InputEventViewHolder)

    override fun onBackPressed() {
        Timber.d("Back pressed...")
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    private var inputEvents: List<InputEvent>
        get() = inputEventAdapter.items
        set(value) {
            Timber.d("%d", value.count())
            this.inputEventAdapter.items = value
        }

    private fun captureEvent(event: android.view.InputEvent?): Boolean {
        event?.let {
            this.inputEvents += InputEvent(MainActivity.currentDateStr(), it)
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean = this.captureEvent(event)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?) = captureEvent(event)

    override fun onTrackballEvent(event: MotionEvent?) = captureEvent(event)

    // override fun onKeyUp(keyCode: Int, event: KeyEvent?) = captureEvent(event)
}
