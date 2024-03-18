package com.example.arventurepath.ui.detail_arventure_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentDetailArventureBinding
import com.example.arventurepath.ui.login_fragment.LoginFragmentDirections
import kotlinx.coroutines.launch

class DetailArventureFragment : Fragment() {

    private lateinit var binding: FragmentDetailArventureBinding
    private val args: DetailArventureFragmentArgs by navArgs()
    private val viewModel = DetailArventureViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailArventureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        viewModel.getArventureDetail(args.idArventure)
        binding.button.setOnClickListener{
            findNavController().navigate(
                DetailArventureFragmentDirections.actionDetailArventureFragmentToInGameFragment(
                    idUser = args.idUser,
                    idArventure = args.idArventure)
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.arventureDetail.collect {
                binding.arventureTitle.text = it.name
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

        lifecycleScope.launch {
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
}