package com.example.arventurepath.ui.in_game_fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private var totalSeconds: Int = 0
    private lateinit var handler: Handler


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
        startStepCounter()
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
    private fun startStepCounter() {
        val account = GoogleSignIn.getAccountForExtension(requireContext(), FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build())

        stepCounter = StepCounter(requireContext(), account) { stepCount ->
            // Aquí recibes el conteo de pasos y puedes actualizar la UI, guardar los datos, etc.
            binding.stepsValueText.text = stepCount.toString()
            Log.e("PASOS", stepCount.toString())
        }

        stepCounter?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        stepCounter?.stop()
    }
}
