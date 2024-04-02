package com.example.arventurepath.ui.in_game_fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.data.models.Happening
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.databinding.FragmentInGameBinding
import com.example.arventurepath.ui.detail_arventure_fragment.DetailArventureFragmentArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random


class InGameFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private lateinit var binding: FragmentInGameBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private var totalSeconds: Int = 0
    private lateinit var handlerTime: Handler
    private lateinit var handlerHappening: Handler
    private var secondsHappening: Int = 0

    private val viewModel = InGameViewModel()

    private lateinit var map: GoogleMap
    private lateinit var nextStop: Stop
    private lateinit var destinyMarker: Marker

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var i = 1
    private var isFirstStop = true
    private var isOnStop = false
    private var randomSecondHappening = 0
    private var happenings: MutableList<Happening> = arrayListOf()
    private var showingHappening = false
    private lateinit var mediaPlayer: MediaPlayer
    private var audioURL = ""
    private var currentSteps = 0
    private var startButtonPressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Timer tiempo
        handlerTime = Handler(Looper.getMainLooper())
        handlerHappening = Handler(Looper.getMainLooper())

        //Reproductor Audio
        mediaPlayer = MediaPlayer()

        setMap()
        viewModel.getArventureDetail(args.idArventure)
        viewModel.getListAchievements()
        observeViewModel()
        //startTimer()
        binding.buttonStart.setOnClickListener {
            // TODO: inicializar el contador de pasos
            //isFirstStop = false
            //Contador de pasos
            sensorManager =
                requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepSensor == null) {
                // This will give a toast message to the user if there is no sensor in the device
                Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
            }else{
                sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

            }
            startTimer()
            viewModel.getStoryFragment()

            binding.llAchievement.setOnClickListener {
                it.visibility = View.GONE
            }

            with(binding) {
                nextStopText.visibility = View.VISIBLE
                nextStopValueText.visibility = View.VISIBLE
                timeText.visibility = View.VISIBLE
                timeValueText.visibility = View.VISIBLE
                stepsText.visibility = View.VISIBLE
                stepsValueText.visibility = View.VISIBLE
                buttonStart.visibility = View.GONE
                tvGoToStart.visibility = View.GONE
            }
        }

        binding.tvTxtInGame.setOnClickListener {
            if (!isFirstStop) {
                launchExternalApk("com.DefaultCompany.Intento1")
            } else {
                destinyMarker.remove()
                viewModel.removeStop()
                viewModel.getStop()
                it.visibility = View.GONE
                viewModel.removeStoryFragment()
            }
            isFirstStop = false
            /*destinyMarker.remove()
            viewModel.removeStop()
            viewModel.getStop()
            it.visibility = View.GONE
            viewModel.removeStoryFragment()*/
        }
        startCheckMarkerTimer()

        binding.textHappening.setOnClickListener {
            restartHappening()
        }
        binding.imgHappening.setOnClickListener{
            restartHappening()
        }
    }


    private fun restartHappening(){
        binding.imgHappening.visibility = View.GONE
        binding.textHappening.visibility = View.GONE
        showingHappening = false
        randomSecondHappening = 0
        secondsHappening = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (running) {
            // i = 2
            totalSteps = event!!.values[0] // 11
            val steps = event.values[0] + i // 13

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            currentSteps = steps.toInt() - totalSteps.toInt() //  13 - 11 = 2

            if (currentSteps == 100) {
                viewModel.earn100StepsAchievement()
            }
            if (currentSteps == 500) {
                viewModel.earn500StepsAchievement()
            }
            // It will show the current steps to the user
            binding.stepsValueText.text = ("$currentSteps")
            i++
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            //Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun startTimer() {
        // Ejecutar un Runnable cada segundo para contar los segundos
        handlerTime.post(object : Runnable {
            override fun run() {
                // Incrementar los segundos
                totalSeconds++
                // Actualizar la UI con los segundos transcurridos en formato de horas, minutos y segundos
                updateUI()

                if (totalSeconds == 300) {
                    viewModel.earnTimeAchievement()
                }
                // Ejecutar este Runnable nuevamente después de 1 segundo
                handlerTime.postDelayed(this, 1000)
            }
        })

        handlerHappening.post(object : Runnable {
            override fun run() {
                if (randomSecondHappening == 0){
                    randomSecondHappening = Random.nextInt(60, 181)
                }
                if (!showingHappening){
                    randomHappening()
                }

                if (secondsHappening - randomSecondHappening >= 11){
                    restartHappening()
                }
                secondsHappening+=5

                handlerHappening.postDelayed(this, 5000)
            }
        })
    }

    private fun randomHappening() {
        if(!isOnStop){
            if (secondsHappening >= randomSecondHappening) {
                if (happenings.isNotEmpty()) {
                    showingHappening = true
                    val randomHappeningNum = Random.nextInt(happenings.size)
                    val selectedHappening = happenings[randomHappeningNum]

                    when (selectedHappening.type) {
                        "text" -> {
                            binding.textHappening.visibility = View.VISIBLE
                            binding.textHappening.text = selectedHappening.text
                        }
                        "image" -> {
                            binding.imgHappening.visibility = View.VISIBLE
                            Glide.with(requireContext())
                                .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgHappening/${selectedHappening.img}")
                                .error(R.drawable.aventura2)
                                .apply(RequestOptions().centerCrop())
                                .into(binding.imgHappening)
                        }
                        "audio" -> {
                            audioURL = "http://abp-politecnics.com/2024/DAM01/filesToServer/audioHappening/${selectedHappening.img}"
                            try {
                                mediaPlayer.setDataSource(audioURL)
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Ha habido un problema al intentar reproducir el audio", Toast.LENGTH_LONG).show()
                            }
                            Toast.makeText(requireContext(), "Audio en reproducción", Toast.LENGTH_LONG).show()

                        }
                    }

                    // Remover el happening seleccionado para que no se vuelva a utilizar
                    happenings.removeAt(randomHappeningNum)
                }

            }
        }

    }

    private fun updateUI() {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        binding.timeValueText.text = timeString
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.arventureDetail.collect {
                binding.timeValueText.text = "0"
                binding.stepsValueText.text = "0"
                binding.arventureTitle.text = it.name.uppercase()
                happenings = viewModel.arventureDetail.value.happenings.toMutableList()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stop.collect {
                binding.nextStopValueText.text = it.name
                nextStop = it
                createNextStopMarker()

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.storyFragment.collect {
                binding.tvTxtInGame.visibility = View.VISIBLE
                binding.tvTxtInGame.text = it.content
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.achievementToShow.collect {
                // TODO: mostrar aquí el logro
                binding.llAchievement.visibility = View.VISIBLE
                binding.tvAchievementContent.text = it.name
                Glide.with(requireContext())
                    .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgAchievement/${it.img}")
                    .error(R.drawable.aventura2)
                    .apply(RequestOptions().centerCrop())
                    .into(binding.imgAchievement)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.win.collect {
                it.achievements.forEach {
                    Log.i(">", "Nombre del logro -> " + it.name)
                }
                findNavController().navigate(
                    InGameFragmentDirections.actionInGameFragmentToScoreFragment2(
                        currentSteps,
                        it,
                        args.idUser,
                        args.idArventure,
                        totalSeconds
                    )
                )
            }
        }
    }

    private fun setMap() {
        // Inicializar el cliente de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.mapInGame.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.getStop()

        // Habilitar el botón "Mi ubicación" en el mapa
        map.isMyLocationEnabled = true
    }


    private fun startCheckMarkerTimer() {
        // Ejecutar un Runnable cada segundo para contar los segundos
        handlerTime.post(object : Runnable {
            override fun run() {
                // Incrementar los segundos
                checkIfComeToMark()
                // Actualizar la UI con los segundos transcurridos en formato de horas, minutos y segundos
                updateUI()
                // Ejecutar este Runnable nuevamente después de 1 segundo
                handlerTime.postDelayed(this, 5000)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun checkIfComeToMark() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                if (::destinyMarker.isInitialized) {
                    if (checkIfIsNear(it.latitude, destinyMarker.position.latitude) &&
                        checkIfIsNear(it.longitude, destinyMarker.position.longitude)
                    ) {
                        if (isFirstStop && !startButtonPressed) {
                            startButtonPressed = true
                            binding.buttonStart.visibility = View.VISIBLE
                            binding.tvGoToStart.text = "Pulsa en empezar y... Allá vamos!"
                        } else {
                            /*destinyMarker.remove()
                            viewModel.removeStop()
                            viewModel.getStop()*/
                            viewModel.getStoryFragment()
                        }
                    }
                }
            }
        }
    }

    private fun checkIfIsNear(myPosition: Double, destinyPosition: Double): Boolean {
        return myPosition > destinyPosition - 0.0002010551 && myPosition < destinyPosition + 0.0002010551
    }

    private fun createNextStopMarker() {
        val coordinates = LatLng(nextStop.latitude, nextStop.longitude)
        val markerOptions = MarkerOptions().position(coordinates).title(nextStop.name)
        destinyMarker = map.addMarker(markerOptions)!!
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18.75f), 1, null
        )
        randomSecondHappening = 0
        secondsHappening = 0
    }


    //RA--------------------------------------------------------------------------------------------
    private val contractRA: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                destinyMarker.remove()
                viewModel.removeStop()
                viewModel.getStop()
                binding.tvTxtInGame.visibility = View.GONE
                viewModel.removeStoryFragment()
            } else {
                destinyMarker.remove()
                viewModel.removeStop()
                viewModel.getStop()
                binding.tvTxtInGame.visibility = View.GONE
                viewModel.removeStoryFragment()
            }
        }

    private fun launchExternalApk(packageName: String) {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            contractRA.launch(it)
        } ?: run {
            // Manejar el caso en el que no se pueda lanzar la actividad
            Toast.makeText(requireContext(), "No se pudo iniciar la aplicación", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun tryingApk() {
        val apkInputStream = resources.openRawResource(R.raw.apk_ra)
        val apkFile = File(requireContext().filesDir, "apk_ra")
        apkInputStream.copyTo(apkFile.outputStream())
        val apkUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.arventurepath.ui.login_fragment.LoginFragment.FileProvider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}

