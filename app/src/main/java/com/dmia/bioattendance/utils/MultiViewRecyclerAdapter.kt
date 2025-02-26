package com.dmia.bioattendance.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dmia.bioattendance.utils.resource.WidgetViewModel

class MultiViewRecyclerAdapter<T : WidgetViewModel>(
    recyclerView: RecyclerView,
    private val list: ArrayList<T>,
    private val onBind: (item: T, binder: ViewDataBinding, position: Int) -> Unit
) : RecyclerView.Adapter<MultiViewRecyclerAdapter.ViewHolder<ViewDataBinding>>() {

    init {
        recyclerView.adapter = this
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].layoutId()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewDataBinding> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(viewType))!!)
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewDataBinding>, position: Int) {
        val item = list[position]
        onBind.invoke(item, holder.binder, position)
    }

    class ViewHolder<out V : ViewDataBinding>(val binder: V) : RecyclerView.ViewHolder(binder.root)
}

infix fun ViewGroup.inflate(@LayoutRes lyt: Int) =
    LayoutInflater.from(context).inflate(lyt, this, false)!!

fun <T : WidgetViewModel> RecyclerView.setUpMultiViewRecyclerAdapter(
    itemList: ArrayList<T>,
    onBind: ((item: T, binder: ViewDataBinding, position: Int) -> Unit)
) = MultiViewRecyclerAdapter(this, itemList, onBind)