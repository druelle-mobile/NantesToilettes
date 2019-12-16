package ovh.geoffrey_druelle.nantestoilettes.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import ovh.geoffrey_druelle.nantestoilettes.databinding.FavoritesItemBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity
import ovh.geoffrey_druelle.nantestoilettes.ui.favorites.FavoritesViewModel
import kotlin.coroutines.CoroutineContext

class FavoritesListAdapter(
    var toilets: List<Toilet> = listOf(),
    private val favoritesViewModel: FavoritesViewModel
) : RecyclerView.Adapter<FavoritesListAdapter.FavoritesListViewHolder>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job: Job = Job()

    private var repo = ToiletRepository(NantesToilettesApp.instance)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesListViewHolder {
        return FavoritesListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favorites_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = toilets.size

    override fun onBindViewHolder(holder: FavoritesListViewHolder, position: Int) {
        val toilet = toilets[position]
        holder.binding.favoriteItem = toilet
        holder.binding.vm = favoritesViewModel
        holder.itemView.findViewById<ImageButton>(R.id.item_search_on_map).setOnClickListener {
            locateOnMap(toilet)
        }

        holder.binding.itemFav.setOnClickListener {
            addOrRemoveFromFavorites(toilet, holder, position)
        }

        setFavoriteButtonIcon(toilet, holder, position)
    }

    private fun addOrRemoveFromFavorites(
        toilet: Toilet,
        holder: FavoritesListViewHolder,
        position: Int
    ) {
        val isInFav = runBlocking { repo.isToiletInFavorites(toilet.id) }
        val updatedToilet: Toilet = runBlocking {
            repo.updateFavoriteField(toilet.id, !isInFav)
            repo.getToiletById(toilet.id)
        }

        setFavoriteButtonIcon(updatedToilet, holder, position)
    }

    private fun setFavoriteButtonIcon(
        toilet: Toilet,
        holder: FavoritesListViewHolder,
        position: Int
    ) {
        if (toilet.favorite) {
            holder.binding.itemFav.setImageResource(R.drawable.ic_star_yellow_500_24dp)
        }
        else {
            holder.binding.itemFav.setImageResource(R.drawable.ic_star_border_grey_600_24dp)
        }

        MainActivity.instance.runOnUiThread { Runnable { this.notifyItemChanged(position) } }
    }

    private fun locateOnMap(toilet: Toilet) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class FavoritesListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: FavoritesItemBinding = DataBindingUtil.bind(view)!!
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        job.cancel()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}