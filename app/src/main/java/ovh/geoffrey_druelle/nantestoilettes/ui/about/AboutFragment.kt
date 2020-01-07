package ovh.geoffrey_druelle.nantestoilettes.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ovh.geoffrey_druelle.nantestoilettes.R
import ovh.geoffrey_druelle.nantestoilettes.core.BaseFragment
import ovh.geoffrey_druelle.nantestoilettes.databinding.AboutFragmentBinding
import ovh.geoffrey_druelle.nantestoilettes.ui.MainActivity
import ovh.geoffrey_druelle.nantestoilettes.utils.AUTHOR_WEBSITE
import ovh.geoffrey_druelle.nantestoilettes.utils.NANTES_LOGO
import ovh.geoffrey_druelle.nantestoilettes.utils.URITROITTOIR_IMG

class AboutFragment : BaseFragment<AboutFragmentBinding>() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var viewModel: AboutViewModel
    private lateinit var uritrottoirImageView: ImageView
    private lateinit var nantesImageView: ImageView

    @LayoutRes
    override fun getLayoutResId(): Int = R.layout.about_fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        MainActivity.instance.setFragment(this)
        MainActivity.instance.supportActionBar?.show()
        MainActivity.instance.bottom_nav.visibility = View.VISIBLE

        viewModel = getViewModel()
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val root = binding.root

        initImages(root)
        initObservers()

        MainActivity.instance.setFragment(this)

        return root
    }

    private fun initObservers() {
        viewModel.buttonClicked.observe(viewLifecycleOwner, Observer { buttonClicked ->
            if (buttonClicked) {
                navigateToWebsite()
                viewModel.hasNavigatedToWebsite()
            }
        })

        viewModel.connected.observe(this, Observer { connected ->
            if (connected) {
                Picasso.get().load(URITROITTOIR_IMG).into(uritrottoirImageView)
                Picasso.get().load(NANTES_LOGO).into(nantesImageView)
            } else {
                uritrottoirImageView.setImageResource(R.drawable.ic_signal_wifi_off_black_48dp)
                nantesImageView.setImageResource(R.drawable.ic_signal_wifi_off_black_48dp)
            }
        })
    }

    private fun initImages(root: View) {
        uritrottoirImageView = root.findViewById(R.id.uritrottoir)
        nantesImageView = root.findViewById(R.id.nantes)
    }

    private fun navigateToWebsite(){
        val dialog = AlertDialog.Builder(MainActivity.instance)
            .setTitle("Open link ?")
            .setMessage("By accepting, you will open your web browser and go to https://geoffrey-druelle.ovh")
            .setPositiveButton(R.string.yes){ dialogInterface, _ ->
                dialogInterface.cancel()
                openWebBrowserIntent()
            }
            .setNegativeButton(R.string.no){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun openWebBrowserIntent() {
        val uri: Uri = Uri.parse(AUTHOR_WEBSITE)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            startActivity(intent)
        }
    }
}
