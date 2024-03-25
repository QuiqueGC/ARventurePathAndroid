package com.example.arventurepath.ui.detail_arventure_fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentDetailArventureBinding
import com.example.arventurepath.utils.Constants.REQUEST_PERMISSION
import kotlinx.coroutines.launch

class DetailArventureFragment : Fragment() {

    private lateinit var binding: FragmentDetailArventureBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private val viewModel = DetailArventureViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailArventureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        viewModel.getArventureDetail(args.idArventure)
        binding.button.setOnClickListener{
            checkPermissions()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.arventureDetail.collect {
                binding.arventureTitle.text = it.name.uppercase()
                binding.summaryArventure.text = it.summary
                binding.distanceArventure.text = it.distance
                binding.minutesArventure.text = it.time
                binding.originArventure.text = it.origin
                binding.storyNameArventure.text = it.nameStory

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
                    binding.linearImg.visibility = View.VISIBLE
                    binding.linearSummary.visibility = View.VISIBLE
                    binding.linearTitle.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkPermissions() {
        val fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val cameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val activityRecognitionPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                // TODO: Revisar esto, que puse un true por poner algo
                true
            }

        if (!fineLocationPermissionGranted ||
            !coarseLocationPermissionGranted ||
            !cameraPermissionGranted ||
            !activityRecognitionPermissionGranted
        ) {
            // Al menos uno de los permisos no está concedido
            requestPermissions()
        } else {
            // Ambos permisos están concedidos, puedes acceder a lo que sea
            findNavController().navigate(
                DetailArventureFragmentDirections.actionDetailArventureFragmentToInGameFragment(
                    idUser = args.idUser,
                    idArventure = args.idArventure
                )
            )
        }
    }

    private fun requestPermissions() {
        val fineLocationPermissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        )

        val storagePermissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val cameraPermissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.CAMERA
        )

        val activityRecognitionPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION
                )
            } else {
                // TODO: Revisar esto, que puse un true por poner algo
                true
            }

        if (fineLocationPermissionGranted ||
            storagePermissionGranted ||
            cameraPermissionGranted ||
            activityRecognitionPermissionGranted
        ) {
            // El usuario ya ha rechazado los permisos
            Toast.makeText(
                requireContext(),
                "Ya has rechazado los permisos. Debes cambiarlos desde los ajustes.",
                Toast.LENGTH_LONG
            ).show()
        } else {

            // Pedir permisos
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Ambos permisos concedidos, puedes acceder a lo que sea
                findNavController().navigate(
                    DetailArventureFragmentDirections.actionDetailArventureFragmentToInGameFragment(
                        idUser = args.idUser,
                        idArventure = args.idArventure
                    )
                )

            } else {
                Toast.makeText(
                    requireContext(),
                    "Permisos rechazados por primera vez",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}