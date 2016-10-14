package dk.homburg.bluetoothtest

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class DevicesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        // recyclerView.adapter = mainActivity!!.devicesAdapter
    }

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mainActivity = context as MainActivity
    }

}