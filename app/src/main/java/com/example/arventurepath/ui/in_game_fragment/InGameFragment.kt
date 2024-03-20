package com.example.arventurepath.ui.in_game_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.arventurepath.data.StepCounter
import com.example.arventurepath.databinding.FragmentInGameBinding
import com.example.arventurepath.ui.detail_arventure_fragment.DetailArventureFragmentArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType


class InGameFragment : Fragment() {

    private lateinit var binding: FragmentInGameBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private var stepCounter: StepCounter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startStepCounter()
    }

    private fun startStepCounter() {
        val account = GoogleSignIn.getAccountForExtension(requireContext(), FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build())

        stepCounter = StepCounter(requireContext(), account) { stepCount ->
            // Aquí recibes el conteo de pasos y puedes actualizar la UI, guardar los datos, etc.
            Log.d("StepCounter", "Steps: $stepCount")
        }

        stepCounter?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        stepCounter?.stop()
    }
}
