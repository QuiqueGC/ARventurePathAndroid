package com.example.arventurepath.ui.score_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.databinding.FragmentScoreBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.sql.Time

class ScoreFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentScoreBinding
    private lateinit var map: GoogleMap
    private val args: ScoreFragmentArgs by navArgs()
    private val viewModel = ScoreFragmentViewModel()
    private var stops = listOf<Stop>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFragment()

        observedViewModel()
        viewModel.getArventureScore(args.idArventure)

        binding.buttonAccept.setOnClickListener {
            findNavController().navigate(
                ScoreFragmentDirections.actionScoreFragment2ToListArventureFragment(
                    idUser = args.idUser
                )
            )
        }
    }

    private fun observedViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.arventureFinal.collect {
                binding.arventureTitle.text = it.name.uppercase()
                binding.distanceArventure.text = it.distance
                binding.estimateTimeArventure.text = it.estimateTime
                binding.stepsArventure.text = args.steps.toString()
                binding.timeArventure.text = getFormattedTime(args.totalSeconds)
                //binding.timeArventure.text = args.totalSeconds.toString()
                binding.storyNameArventure.text = it.storyName
                stops = it.stops
                setAchievements()

                Glide.with(requireContext())
                    .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgStory/" + it.img)
                    .error(R.drawable.aventura2)
                    .apply(RequestOptions().centerCrop())
                    .into(binding.imgArventure)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                if (!isLoading) {
                    binding.progressBar.visibility = View.GONE
                    binding.linearTitle.visibility = View.VISIBLE
                    binding.linearImg.visibility = View.VISIBLE
                    binding.linearMap.visibility = View.VISIBLE
                    binding.linearAchievement.visibility = View.VISIBLE
                    createFragment()
                }
            }
        }
    }

    private fun setAchievements() {
        val marginInPixels = resources.getDimensionPixelOffset(R.dimen.smallMargin)
        val marginLayoutParams = ViewGroup.MarginLayoutParams(
            resources.getDimensionPixelOffset(R.dimen.achievementWidth),
            resources.getDimensionPixelOffset(R.dimen.achievementHeight)
        )
        marginLayoutParams.setMargins(
            marginInPixels,
            marginInPixels,
            marginInPixels,
            marginInPixels
        )

        args.achievements.achievements.forEach { achievement ->
            Log.i(">", "Nombre del logro -> " + achievement.name)
            val imageView = ImageView(context).apply {
                layoutParams = marginLayoutParams
                Glide.with(requireContext())
                    .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgAchievement/" + achievement.img)
                    .error(R.drawable.aventura2)
                    .apply(RequestOptions().centerCrop())
                    .into(this)
                setImageResource(R.drawable.logro)
            }
            binding.linearImgAchievement.addView(imageView)
        }
    }

    private fun createFragment() {
        binding.frgmntMap.getFragment<SupportMapFragment>().getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    private fun createMarker() {

        for (stop in stops){
            val coordinates = LatLng(stop.latitude, stop.longitude)
            val marker : MarkerOptions = MarkerOptions().position(coordinates).title(stop.name)
            map.addMarker(marker)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 11.8f),4000, null
            )
        }
    }

    private fun getFormattedTime(timeInSeconds: Int): String {
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds % 3600) / 60
        val seconds = timeInSeconds % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        //binding.timeArventure.text = timeString

        return timeString
    }

}