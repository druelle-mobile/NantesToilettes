package ovh.geoffrey_druelle.nantestoilettes.ui.toiletslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet
import ovh.geoffrey_druelle.nantestoilettes.data.repository.ToiletRepository
import ovh.geoffrey_druelle.nantestoilettes.databinding.ToiletItemBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity
import kotlin.coroutines.CoroutineContext

class ToiletsListAdapter(
    var toilets: List<Toilet> = listOf(),
    private val toiletsListViewModel: ToiletsListViewModel
) : RecyclerView.Adapter<ToiletsListAdapter.ToiletsListViewHolder>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job: Job = Job()

    private var repo = ToiletRepository(NantesToilettesApp.instance)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ToiletsListViewHolder {
        return ToiletsListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.toilet_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = toilets.size

    override fun onBindViewHolder(holder: ToiletsListViewHolder, position: Int) {
        val toilet = toilets[position]
        holder.binding.toiletItem = toilet
        holder.binding.vm = toiletsListViewModel
        holder.binding.itemSearchOnMap.setOnClickListener {
            locateOnMap(toilet)
        }

        holder.binding.itemFav.setOnClickListener {
            addOrRemoveFromFavorites(toilet, holder, position)
        }

        setFavoriteButtonIcon(toilet, holder, position)
        setMobilityAccessIcon(toilet, holder, position)
    }

    private fun setMobilityAccessIcon(
        toilet: Toilet,
        holder: ToiletsListViewHolder,
        position: Int
    ) {
        if (toilet.reduceMobility == "oui") {
            holder.binding.itemAccessibility.visibility = View.VISIBLE
        }
        else {
            holder.binding.itemAccessibility.visibility = View.GONE
        }
        MainActivity.instance.runOnUiThread { Runnable { this.notifyItemChanged(position) } }
    }

    private fun addOrRemoveFromFavorites(
        toilet: Toilet,
        holder: ToiletsListViewHolder,
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
        holder: ToiletsListViewHolder,
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

    inner class ToiletsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ToiletItemBinding = DataBindingUtil.bind(view)!!
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        job.cancel()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}