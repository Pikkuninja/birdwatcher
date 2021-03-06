package fi.jara.birdwatcher.screens.addobservation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.observations.ObservationRarity
import kotlinx.android.synthetic.main.add_observation_fragment.*
import javax.inject.Inject

class AddObservationFragment @Inject constructor(
    private val viewModelFactoryCreator: AddObservationViewModelFactoryCreator

) : Fragment() {
    private val viewModel: AddObservationViewModel by viewModels { viewModelFactoryCreator(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.add_observation_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewModel()
        addViewListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeViewListeners()
    }

    private fun subscribeToViewModel() {
        viewModel.addLocationToObservation.observe(viewLifecycleOwner, Observer {
            add_observation_location_toggle.isChecked = it
        })

        viewModel.userImageUri.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                add_observation_image_preview.setImageURI(it)
                add_observation_image_preview.contentDescription =
                    resources.getString(R.string.user_image_attached)
                add_observation_image_toggle.isChecked = true
            } else {
                add_observation_image_preview.setImageResource(R.drawable.ic_imageplaceholder_black_24dp)
                add_observation_image_preview.contentDescription =
                    resources.getString(R.string.no_image_attached)
                add_observation_image_toggle.isChecked = false
            }
        })

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner, Observer {
            add_observation_save_button.isEnabled = it
        })

        viewModel.displayMessages.observe(viewLifecycleOwner, Observer { message ->
            view?.let { v ->
                Snackbar.make(v, message, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer {
            requestLocationPermission()
        })

        viewModel.gotoListScreen.observe(viewLifecycleOwner, Observer {
            gotoListScreen()
        })

        viewModel.requestImageUri.observe(viewLifecycleOwner, Observer {
            requestImageFromGallery()
        })
    }

    private fun addViewListeners() {
        add_observation_location_toggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAddLocationToObservationToggled(isChecked)
        }

        add_observation_image_toggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAttachImageToggled(isChecked)
        }

        add_observation_save_button.setOnClickListener {
            val name = add_observation_species.text?.toString() ?: ""
            val rarity = when (add_observation_rarity_radiogroup.checkedRadioButtonId) {
                R.id.add_observation_rarity_common -> ObservationRarity.Common
                R.id.add_observation_rarity_rare -> ObservationRarity.Rare
                R.id.add_observation_rarity_extremely_rare -> ObservationRarity.ExtremelyRare
                else -> null
            }
            val description = add_observation_description.text?.toString() ?: ""

            viewModel.onSaveObservationClicked(name, rarity, description)
        }

        add_observation_species.setOnEditorActionListener { _, _, _ ->
            add_observation_species.clearFocus()
            false
        }

        add_observation_description.setOnEditorActionListener { _, _, _ ->
            add_observation_description.clearFocus()
            false
        }
    }

    private fun removeViewListeners() {
        add_observation_location_toggle.setOnClickListener(null)
        add_observation_image_toggle.setOnClickListener(null)
        add_observation_save_button.setOnClickListener(null)

        add_observation_species.setOnEditorActionListener(null)
        add_observation_description.setOnEditorActionListener(null)
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, resources.getString(R.string.select_image)),
            IMAGE_FROM_GALLERY_REQUEST_CODE
        )
    }

    private fun gotoListScreen() {
        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val granted = grantResults.getOrNull(0) ?: PackageManager.PERMISSION_DENIED
            viewModel.onLocationPermissionRequestFinished(granted == PackageManager.PERMISSION_GRANTED)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_FROM_GALLERY_REQUEST_CODE) {
            val uri = data?.data
            viewModel.onImageUriRequestFinished(uri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 4321
        private const val IMAGE_FROM_GALLERY_REQUEST_CODE = 4322
    }
}
