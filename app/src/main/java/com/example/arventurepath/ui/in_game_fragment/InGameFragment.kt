package com.example.arventurepath.ui.in_game_fragment

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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
        setMap()
        viewModel.getArventureDetail(args.idArventure)
        observeViewModel()
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
                createNextStopMarker(it)
            }
        }
        lifecycleScope.launch {
            viewModel.myLocation.collect {
                setMyLocation(it)
            }
        }

    }

    private fun setMap() {
        binding.mapInGame.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.getStop()
        viewModel.getMyLocation(requireContext())
    }

    private fun createNextStopMarker(stop: Stop) {

        val coordinates = LatLng(stop.latitude, stop.longitude)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title(stop.name)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18.75f), 1, null
        )
    }

    private fun setMyLocation(myLocation: Location) {
        val coordinates = LatLng(myLocation.latitude, myLocation.longitude)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title("Este soy yo")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18.75f), 4000, null
        )
    }
}

