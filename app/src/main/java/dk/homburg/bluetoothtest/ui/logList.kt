package dk.homburg.bluetoothtest.ui

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

class TheAdapter<T>(@LayoutRes val itemLayout: Int, val binder: (T, View) -> Unit) : RecyclerView.Adapter<TheAdapter<T>.ViewHolder>(), AutoUpdatableAdapter {

    var items: List<T> by Delegates.observable(emptyList()) {
        prop, old, new ->
        autoNotify(old, new) { o, n -> o == n }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(itemLayout, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(content: T) = binder(content, itemView)
    }
}

interface AutoUpdatableAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotify(old: List<T>, new: List<T>, compare: (T, T) -> Boolean) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(old[oldItemPosition], new[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition] == new[newItemPosition]
            }

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size
        })

        diff.dispatchUpdatesTo(this)
    }
}