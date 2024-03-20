package com.example.arventurepath.ui.in_game_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.arventurepath.databinding.FragmentInGameBinding
import com.example.arventurepath.ui.detail_arventure_fragment.DetailArventureFragmentArgs
import kotlinx.coroutines.launch


class InGameFragment : Fragment() {

    private lateinit var binding: FragmentInGameBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private val viewModel = InGameViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }
}