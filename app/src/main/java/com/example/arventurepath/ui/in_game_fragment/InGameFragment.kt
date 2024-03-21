package com.example.arventurepath.ui.in_game_fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var stepCounter: StepCounter? = null
    private var totalSeconds: Int = 0
    private lateinit var handlerTime: Handler

    private val viewModel = InGameViewModel()

    private lateinit var map: GoogleMap
    private lateinit var nextStop: Stop
    private lateinit var destinyMarker: Marker

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var stepCount = 0


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

        //Contador de pasos
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        startStepCounter()

        setMap()
        viewModel.getArventureDetail(args.idArventure)
        observeViewModel()
        startTimer()
    }

    private fun startStepCounter() {
        stepSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        Log.e("ON SENSOR CHANGED" , " antes del IF y del event.let")
        event?.let {
            Log.e("ON SENSOR CHANGED" , " antes del IF y DENTRO event.let")
            if (it.sensor == stepSensor) {
                stepCount = it.values[0].toInt()
                Log.e("ON SENSOR CHANGED" , " ENTRÓ EN EL IF$stepCount")
                updateStepCount()
            }
        }    }

    private fun updateStepCount() {
        Log.e("updateStepCount" , "ANTES DE ENTRAR EN RUNONUITHREAD")
        requireActivity().runOnUiThread {
            Log.e("updateStepCount" , "DESPÚES DE ENTRAR EN RUNONUITHREAD")
            binding.stepsValueText.text = "$stepCount"
        }
    }

    override fun onResume() {
        super.onResume()
        startStepCounter()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
    private fun startTimer(){
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

    private fun updateUI(){
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        binding.timeValueText.text = timeString
    }

    private fun observeViewModel() {
        /*lifecycleScope.launch {
            viewModel.arventureDetail.collect {
                if (it.route.stops.isNotEmpty()) {

                }
                binding.timeValueText.text = "0"
                binding.stepsValueText.text = "0"
            }
        }*/

        lifecycleScope.launch {
            viewModel.stop.collect {
                binding.nextStopValueText.text = it.name
                nextStop = it
                createNextStopMarker()
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
        //viewModel.getMyLocation(requireContext())

        // Habilitar el botón "Mi ubicación" en el mapa
        map.isMyLocationEnabled = true

        // Obtener la última ubicación conocida del dispositivo y mover la cámara al marcador
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                //userLocation = it
                checkIfComeToMark(it)
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.75f))
            }
        }
    }

    private fun checkIfComeToMark(userLocation: Location) {
        if (checkIfIsNear(userLocation.latitude, destinyMarker.position.latitude) &&
            checkIfIsNear(userLocation.longitude, destinyMarker.position.longitude)
        ) {
            destinyMarker.remove()
            viewModel.removeStop()
            viewModel.getStop()
        }
    }

    private fun checkIfIsNear(myPosition: Double, destinyPosition: Double): Boolean {
        return myPosition > destinyPosition - 0.001 && myPosition < destinyPosition + 0.001
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

