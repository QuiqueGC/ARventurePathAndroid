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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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


class InGameFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private lateinit var binding: FragmentInGameBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private var totalSeconds: Int = 0
    private lateinit var handlerTime: Handler

    private val viewModel = InGameViewModel()

    private lateinit var map: GoogleMap
    private lateinit var nextStop: Stop
    private lateinit var destinyMarker: Marker

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var isFirstStop = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(">", "AAAAAAAAAAAAAAAAAAAAAAAA")
        binding = FragmentInGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Timer tiempo
        handlerTime = Handler(Looper.getMainLooper())

        //Contador de pasos
        resetSteps()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager


        setMap()
        viewModel.getArventureDetail(args.idArventure)
        viewModel.getListAchievements()
        observeViewModel()
        //startTimer()
        binding.buttonStart.setOnClickListener {
            // TODO: inicializar el contador de pasos
            isFirstStop = false
            startTimer()
            destinyMarker.remove()
            viewModel.removeStop()
            viewModel.getStop()

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
        startCheckMarkerTimer()
    }

    private fun resetSteps() {

        totalSteps = 0f
        previousTotalSteps = 0f
        binding.stepsValueText.text = 0.toString()

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
            viewModel.win.collect {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "HAS GANADO LA PARTIDA, DESGRACIADO!",
                        Toast.LENGTH_LONG
                    ).show()
                }
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
                handlerTime.postDelayed(this, 1000)
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
                            destinyMarker.remove()
                            viewModel.removeStop()
                            viewModel.getStop()
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

    override fun onSensorChanged(event: SensorEvent?) {

        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user
            binding.stepsValueText.text = ("$currentSteps")
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
            Toast.makeText(
                requireContext(),
                "No sensor detected on this device",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

}

