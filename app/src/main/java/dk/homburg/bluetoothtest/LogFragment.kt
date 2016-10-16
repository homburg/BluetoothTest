package dk.homburg.bluetoothtest

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A placeholder fragment containing a simple view.
 */
class LogFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        this.recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
    }

    override fun onPause() {
        super.onPause()

        recyclerView!!.adapter = null
    }


    override fun onResume() {
        super.onResume();

        recyclerView!!.adapter = this.mainActivity!!.logAdapter
    }

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity) {
            this.mainActivity = context
        }
    }
}
