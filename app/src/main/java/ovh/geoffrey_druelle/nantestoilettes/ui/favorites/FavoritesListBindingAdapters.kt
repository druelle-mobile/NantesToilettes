package ovh.geoffrey_druelle.nantestoilettes.ui.favorites

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet

@BindingAdapter(value = ["favoritesList","favoritesViewModel"], requireAll = false)
fun setRecyclerViewSource(
    recyclerView: RecyclerView,
    list: List<Toilet>,
    viewModel: FavoritesViewModel
) {
    recyclerView.adapter?.run {
        if (this is FavoritesListAdapter) {
            this.toilets = list
            this.notifyDataSetChanged()
        }
    } ?: run {
        FavoritesListAdapter(list, viewModel).apply {
            recyclerView.adapter = this
        }
    }
}