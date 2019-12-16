package ovh.geoffrey_druelle.nantestoilettes.ui.favorites

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.core.BaseFragment
import ovh.geoffrey_druelle.nantestoilettes.databinding.FavoritesFragmentBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity.Companion.instance

class FavoritesFragment : BaseFragment<FavoritesFragmentBinding>() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private var exit: Boolean = false
    private lateinit var viewModel: FavoritesViewModel

    @LayoutRes
    override fun getLayoutResId(): Int = R.layout.favorites_fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        instance.setFragment(this)
        instance.supportActionBar?.show()
        instance.bottom_nav.visibility = View.VISIBLE

        viewModel = getViewModel()
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    quitApp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        return binding.root
    }

    private fun quitApp() {
        if (exit) instance.finish()
        else {
            toast("Press Back again to exit.")
            exit = true
            Handler().postDelayed({exit = false}, 3000)
        }
    }

}
