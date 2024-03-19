package com.example.arventurepath.ui.list_arventure_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentListArventureBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ListArventureFragment : Fragment(), ArventureListListener {

    private lateinit var binding: FragmentListArventureBinding
    private lateinit var pagerAdapter: ArventurePagerAdapter
    private lateinit var listAdapter: ArventureListAdapter
    private val viewModel = ListArventureViewModel()
    private val args: ListArventureFragmentArgs by navArgs()
    private var isListScreen = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListArventureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPagerAdapter()
        setupListAdapter()
        observeViewModel()
        viewModel.getListArventures()

        binding.btnGetList.setOnClickListener {
            if (!isListScreen) {
                binding.constraintRecycler.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                binding.btnGetList.text = getString(R.string.back)
            } else {
                binding.constraintRecycler.visibility = View.GONE
                binding.viewPager.visibility = View.VISIBLE
                binding.btnGetList.text = getString(R.string.seeMoreArventures)
            }
            isListScreen = !isListScreen
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.listArventures.collect { arventuresList ->
                if (arventuresList.isNotEmpty()) {
                    pagerAdapter.updateList(arventuresList)
                    listAdapter.updateList(arventuresList)
                    binding.constraintRecycler.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                if (!isLoading) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPagerAdapter() {
        pagerAdapter = ArventurePagerAdapter(requireContext(), this)

        binding.viewPager.adapter = pagerAdapter

        setupCardsVisionConfig(binding.viewPager)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupListAdapter() {
        listAdapter = ArventureListAdapter(requireContext(), this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = listAdapter
    }


    private fun setupCardsVisionConfig(viewPager: ViewPager2) {
        viewPager.offscreenPageLimit = 1

        //Para que muestre las tarjetas laterales
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position

            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))

            // If you want a fading effect uncomment the next line:
            //page.alpha = 0.25f + (1 - abs(position))
        }

        viewPager.setPageTransformer(pageTransformer)

        //Para que no se superpongan las vistas
        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_current_item_horizontal_margin
        )

        viewPager.addItemDecoration(itemDecoration)
    }

    override fun onArventureClick(idArventure: Int) {

        findNavController().navigate(
            ListArventureFragmentDirections
                .actionListArventureFragment3ToDetailArventureFragment(
                    idUser = args.idUser,
                    idArventure = idArventure
                )
        )
    }
}