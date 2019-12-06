package ovh.geoffrey_druelle.nantestoilettes.ui.toiletslist

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ovh.geoffrey_druelle.nantestoilettes.R

class ToiletsListFragment : Fragment() {

    companion object {
        fun newInstance() = ToiletsListFragment()
    }

    private lateinit var viewModel: ToiletsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.toilets_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ToiletsListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
