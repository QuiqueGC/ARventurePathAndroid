package com.example.arventurepath.ui.in_game_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
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

        setMap()
        viewModel.getArventureDetail(args.idArventure)
        viewModel.getListAchievements()
        observeViewModel()
        //startTimer()
        binding.buttonStart.setOnClickListener {
            // TODO: inicializar el contador de pasos
            isFirstStop = false
            //Contador de pasos
            sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            startTimer()
            viewModel.getStoryFragment()


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
            destinyMarker.remove()
            viewModel.removeStop()
            viewModel.getStop()
            it.visibility = View.GONE
            viewModel.removeStoryFragment()
        }
        startCheckMarkerTimer()

        binding.textHappening.setOnClickListener {
            binding.textHappening.visibility = View.GONE
        }
        binding.imgHappening.setOnClickListener{
            binding.imgHappening.visibility = View.GONE
        }
    }



    override fun onSensorChanged(event: SensorEvent?) {

        if (running) {
                                            // i = 2
            totalSteps = event!!.values[0] // 11
            val steps = event.values[0] + i // 13

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = steps.toInt() - totalSteps.toInt() //  13 - 11 = 2

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
            Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
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
                // Ejecutar este Runnable nuevamente después de 1 segundo
                handlerTime.postDelayed(this, 1000)
            }
        })

        handlerHappening.post(object : Runnable {
            override fun run() {
                if (randomSecondHappening == 0){
                    randomSecondHappening = Random.nextInt(120, 301)
                }
                randomHappening()
                secondsHappening++
                handlerHappening.postDelayed(this, 1000)
            }
        })
    }

    private fun randomHappening() {
        if (secondsHappening >= randomSecondHappening) {
            val happenings = viewModel.arventureDetail.value.happenings.toMutableList()

            if (happenings.isNotEmpty()) {
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
                }

                // Remover el happening seleccionado para que no se vuelva a utilizar
                happenings.removeAt(randomHappeningNum)
            }
            if (secondsHappening - randomSecondHappening >= 20){
                binding.textHappening.visibility = View.GONE
                binding.imgHappening.visibility = View.GONE
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
            viewModel.win.collect {

                findNavController().navigate(
                    InGameFragmentDirections.actionInGameFragmentToScoreFragment2(
                        totalSteps,
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
                        if (isFirstStop) {
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
    }
}

