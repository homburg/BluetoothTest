package dk.homburg.bluetoothtest.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.homburg.bluetoothtest.R

abstract class RecyclerViewFragment<in A : Activity, T, Y : TheAdapterViewHolder<T>> : Fragment() {

    abstract fun adapterFromActivity(activity: A): TheAdapter<T, Y>

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

    private var mainActivity: A? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mainActivity = context as A?
    }
}
