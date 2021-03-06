# Birdwatcher
Keep track of bird observations

[![Build Status](https://app.bitrise.io/app/c2780485f7d3438f/status.svg?token=VdZoib3KvUAL-JrokQI1yQ&branch=master)](https://app.bitrise.io/app/c2780485f7d3438f)

# Features
- Store bird observations locally, observation information includes:
  - Bird species name
  - Rarity of the bird
  - Timestamp for when the observation was stored
  - Optional user location at the time of storing
  - Optional picture from device's gallery
  - Optional notes
- View stored observations in a list
  - Can be ordered by timestamp or species names
- View a single stored observation in more detail

# Implementation notes
- Uses AndroidX and Jetpack libraries heavily: LiveData, Navigation, Room, ViewModel (with saved state)
  - I wanted to test using these, as Room was the only one I had previously used
- Asynchronity handled with Kotlin Coroutines (including Flows)
  - Again, some tech I hadn't used in earlier projects
- Dependency injection with Dagger
  - Dagger graph includes e.g. UseCases & their dependencies, ViewModelProviders

# Things that could be improved
- The DI setup for VMs is annoying and needs quite a lot of hand-written code
  - To support SavedStateHandles and giving params from Fragment's arguments to VM's constructor,
    basically a factory of factories is injected to the Fragment (from a FragmentFactory, yo dawg).
    Some code repetition has been cut by adding factories that take lambdas in to create the final VM.
    Something like AssistedInject could help but I don't like how using it would move extraction of arguments to the Fragment itself. 
- UI tests, there are some but the DI setup for them just feels wrong and requires lot of manual config
- Better form error handling: currently errors in filling the add observation form are reported 1 at a time
- Better feedback when saving the observation is in progress, getting user's location with GPS can take a while
  - Maybe add a cancelation option too?
- Better i18n, viewmodels can currently just send hardcoded strings to fragments
- Remove all other Android dependencies from business layer (for example Android Uris are used in ViewModels)
- Dark mode toggle and overall some theme adjustments from default

# Building and running
Project uses gradle and doesn't require anything out of your regular Android app building pipeline.

Easiest way to build and run is through Android Studio

Building from command line can be done with commands like ``./gradlew assembleDebug``

To install a debug build to a connected device through command line, run ``./gradlew installDebug``, and then you can start the app from the device

# Running tests
You can run tests either through Android Studio or from the command line

Unit tests from command line
1. Run ``./gradlew test``

Android tests from command line (currently has Room tests, UI tests to be added)
1. Make sure that an Android emulator is running (or a real device with USB debug setup is connected)
2. Run ``./gradlew connectedAndroidTest``

# License
The app is open sourced with MIT license
