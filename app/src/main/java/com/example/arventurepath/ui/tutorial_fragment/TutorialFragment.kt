package com.example.arventurepath.ui.tutorial_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentTutorialBinding
import com.example.arventurepath.ui.list_arventure_fragment.ListArventureFragmentArgs
import com.example.arventurepath.ui.login_fragment.LoginFragmentDirections

class TutorialFragment : Fragment() {

    private lateinit var binding: FragmentTutorialBinding
    private val args: TutorialFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSkip.setOnClickListener{
            findNavController().navigate(
                TutorialFragmentDirections.actionTutorialFragmentToListArventureFragment3(idUser = args.idUser)
            )
        }
    }
}