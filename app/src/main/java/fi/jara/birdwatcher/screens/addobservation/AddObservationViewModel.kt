package fi.jara.birdwatcher.screens.addobservation

import android.net.Uri
import androidx.lifecycle.*
import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObservationRarity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddObservationViewModel(
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver
) : ViewModel() {

    private var hasLocationPermission: Boolean = false
    private var imageResolvingJob: Job? = null

    private val _displayMessages = LiveEvent<String>()
    val displayMessages: LiveData<String>
        get() = _displayMessages

    private val _requestLocationPermission = LiveEvent<Unit>()
    val requestLocationPermission: LiveData<Unit>
        get() = _requestLocationPermission

    private val _requestImageUri = LiveEvent<Unit>()
    val requestImageUri: LiveData<Unit>
        get() = _requestImageUri

    private val _gotoListScreen = LiveEvent<Unit>()
    val gotoListScreen: LiveData<Unit>
        get() = _gotoListScreen

    private val _addLocationToObservation = MutableLiveData<Boolean>().apply { value = false }
    val addLocationToObservation: LiveData<Boolean>
        get() = _addLocationToObservation

    private val _userImageUri = MutableLiveData<Uri?>().apply { value = null }
    val userImageUri: LiveData<Uri?>
        get() = _userImageUri

    private val isSavingObservation = MutableLiveData<Boolean>().apply { value = false }

    val saveButtonEnabled: LiveData<Boolean>

    init {
        saveButtonEnabled = MediatorLiveData<Boolean>().apply {
            fun resolve() {
                val saving = isSavingObservation.value ?: true
                value = !saving
            }

            addSource(isSavingObservation) {
                resolve()
            }
        }
    }

    fun onAddLocationToObservationToggled(value: Boolean) {
        if (value && !hasLocationPermission) {
            _requestLocationPermission.postValue(Unit)
        }

        _addLocationToObservation.value = value
    }

    fun onLocationPermissionRequestFinished(hasPermission: Boolean) {
        hasLocationPermission = hasPermission
        if (!hasPermission) {
            _addLocationToObservation.value = false
        }
    }

    fun onAttachImageToggled(value: Boolean) {
        if (value) {
            _requestImageUri.postValue(Unit)
        } else {
            _userImageUri.postValue(null)
        }
    }

    fun onImageUriRequestFinished(uri: Uri?) {
        _userImageUri.postValue(uri)
    }

    fun onSaveObservationClicked(
        observationName: String,
        observationRarity: ObservationRarity?,
        observationDescription: String
    ) {
        viewModelScope.launch {
            isSavingObservation.value = true

            val addLocation =
                addLocationToObservation.value!! //addLocation LiveData was initialized with nonnull
            val imageBytes = userImageUri.value?.let {
                val bitmap = bitmapResolver.getBitmap(it)
                bitmapResolver.getBytes(bitmap)
            }

            try {
                val result = insertNewObservationUseCase(
                    observationName,
                    addLocation,
                    observationRarity,
                    observationDescription,
                    imageBytes
                )

                when (result) {
                    is Either.Left -> saveObservationSuccess()
                    is Either.Right -> saveObservationFailed(result.value)
                }

            } catch (e: Exception) {
                saveObservationFailed(e.message ?: "")
            }
        }
    }


    private fun saveObservationSuccess() {
        _displayMessages.value = "Successfully saved observation"
        viewModelScope.launch {
            delay(1000)
            _gotoListScreen.postValue(Unit)
        }
    }

    private fun saveObservationFailed(errorMessage: String) {
        _displayMessages.value = errorMessage
        isSavingObservation.value = false
    }
}