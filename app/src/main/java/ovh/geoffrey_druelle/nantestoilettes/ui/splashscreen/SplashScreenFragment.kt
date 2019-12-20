package ovh.geoffrey_druelle.nantestoilettes.ui.splashscreen

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.core.BaseFragment
import ovh.geoffrey_druelle.nantestoilettes.databinding.SplashScreenFragmentBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity

class SplashScreenFragment : BaseFragment<SplashScreenFragmentBinding>() {

    companion object {
        fun newInstance() = SplashScreenFragment()
    }

    private lateinit var viewModel: SplashScreenViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.splash_screen_fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        MainActivity.instance.supportActionBar?.hide()
        MainActivity.instance.bottom_nav.visibility = View.GONE

        viewModel = getViewModel()
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initObservers()
        initLottieAnimBehavior()

        return binding.root
    }

    private fun initLottieAnimBehavior() {
        val anim = binding.root.findViewById<LottieAnimationView>(R.id.animation)
        anim.speed = anim.speed*2
        anim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                viewModel.finishAnimation()
            }
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
    }

    private fun initObservers() {
        viewModel.navToMap.observe(this, Observer {})

        viewModel.isFinishedAnim.observe(this, Observer { finishedAnim ->
            if (finishedAnim && viewModel.navToMap.value == true) navigateToMap()
        })

        viewModel.failedRequestForDatas.observe(this, Observer { fail ->
            if (fail) makeRetryDatasSnackbar()
        })

        viewModel.isConnection.observe(this, Observer { connected ->
            if (!connected) makeNoConnectionSnackbar()
        })

        viewModel.failedRequestForHits.observe(this, Observer { fail ->
            if (fail) makeRetryHitsSnackbar()
        })
    }

    private fun makeNoConnectionSnackbar() {
        view?.let {
            Snackbar.make(it, "No internet. Continue ?", Snackbar.LENGTH_LONG)
                .setAction("Yes") { navigateToMap() }
                .show()
        }
    }

    private fun makeRetryHitsSnackbar() {
        viewModel.succeedRequestForHits()
        view?.let {
            Snackbar.make(it, "Failed to get hits", Snackbar.LENGTH_LONG)
                .setAction("Retry") { viewModel.isLocalDatas() }
                .show()
        }
    }

    private fun makeRetryDatasSnackbar() {
        viewModel.succeedRequestForDatas()
        view?.let {
            Snackbar.make(it, "Failed to get datas", Snackbar.LENGTH_LONG)
                .setAction("Retry") { viewModel.launchRequestForDatas() }
                .show()
        }
    }

    private fun navigateToMap() {
        val action = R.id.action_splashScreenFragment_to_mapFragment
        viewModel.navigatedToMap()
        viewModel.alreadyFinishedAnimation()
        NavHostFragment.findNavController(this).navigate(action)
    }
}
