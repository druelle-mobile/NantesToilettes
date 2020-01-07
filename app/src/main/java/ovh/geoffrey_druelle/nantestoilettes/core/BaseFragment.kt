package ovh.geoffrey_druelle.nantestoilettes.core

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import org.jetbrains.anko.support.v4.toast
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity

abstract class BaseFragment<T: ViewDataBinding> : Fragment(){

    @LayoutRes
    abstract fun getLayoutResId(): Int

    protected lateinit var binding: T
        private set

    private var exit: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            getLayoutResId(),
            container,
            false
        )

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
        if (exit) MainActivity.instance.finish()
        else {
            toast("Press Back again to exit.")
            exit = true
            Handler().postDelayed({ exit = false }, 3000)
        }
    }
}
