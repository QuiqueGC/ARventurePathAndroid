package com.example.arventurepath.ui.in_game_fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.arventurepath.data.StepCounter
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.databinding.FragmentInGameBinding
import com.example.arventurepath.ui.detail_arventure_fragment.DetailArventureFragmentArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch


class InGameFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentInGameBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private var stepCounter: StepCounter? = null
    private var totalSeconds: Int = 0
    private lateinit var handler: Handler

    private val viewModel = InGameViewModel()

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())

        startTimer()

        viewModel.getArventureDetail(args.idArventure)
        observeViewModel()
        setMap()
    }

    private fun startTimer(){
        // Ejecutar un Runnable cada segundo para contar los segundos
        handler.post(object : Runnable {
            override fun run() {
                // Incrementar los segundos
                totalSeconds++
                // Actualizar la UI con los segundos transcurridos en formato de horas, minutos y segundos
                updateUI()
                // Ejecutar este Runnable nuevamente después de 1 segundo
                handler.postDelayed(this, 1000)
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
        lifecycleScope.launch {
            viewModel.arventureDetail.collect {
                if (it.route.stops.isNotEmpty()) {
                    binding.nextStopValueText.text = it.route.stops[0].name
                }
                binding.timeValueText.text = "0"
                binding.stepsValueText.text = "0"
            }
        }

        lifecycleScope.launch {
            viewModel.stop.collect {
                createMarker(it)
            }
        }

    }

    private fun setMap() {
        binding.mapInGame.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //createMarkers()
    }

    private fun createMarker(stop: Stop) {

        val coordinates = LatLng(stop.latitude, stop.longitude)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title(stop.name)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 11.8f), 4000, null
        )

    }
}

