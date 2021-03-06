package ovh.geoffrey_druelle.nantestoilettes.ui.toiletslist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet

@BindingAdapter(value = ["toiletsList","toiletsViewModel"], requireAll = false)
fun setRecyclerViewSource(
    recyclerView: RecyclerView,
    list: List<Toilet>,
    viewModel: ToiletsListViewModel
) {
    recyclerView.adapter?.run {
        if (this is ToiletsListAdapter) {
            this.toilets = list
            this.notifyDataSetChanged()
        }
    } ?: run {
        ToiletsListAdapter(list, viewModel).apply {
            recyclerView.adapter = this
        }
    }
}