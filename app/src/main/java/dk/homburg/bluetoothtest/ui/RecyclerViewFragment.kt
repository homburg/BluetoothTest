package dk.homburg.bluetoothtest.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.homburg.bluetoothtest.MainActivity
import dk.homburg.bluetoothtest.R

abstract class RecyclerViewFragment<T> : Fragment() {

    abstract fun adapterFromActivity(activity: MainActivity): TheAdapter<T>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.findViewById(R.id.recyclerView) as RecyclerView).let {
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            it.adapter = adapterFromActivity(this.mainActivity!!)
        }
    }

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity) {
            this.mainActivity = context
        }
    }
}
