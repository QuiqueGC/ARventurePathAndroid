package com.example.arventurepath.ui.list_arventure_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.arventurepath.R
import com.example.arventurepath.databinding.FragmentListArventureBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ListArventureFragment : Fragment(), ArventurePagerAdapter.ArventureListener {

    private lateinit var binding: FragmentListArventureBinding
    private lateinit var adapter: ArventurePagerAdapter
    private val viewModel = ListArventureViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListArventureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeViewModel()
        viewModel.getListArventures()

        binding.btnGetList.setOnClickListener {
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.listArventures.collect { arventuresList ->
                adapter.updateList(arventuresList)
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

    private fun setupAdapter() {
        adapter = ArventurePagerAdapter(requireContext(), this)

        binding.viewPager.adapter = adapter

        setupCardsVisionConfig(binding.viewPager)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
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

    override fun onArventureClick(id: Int) {
        // TODO: implementar la navegación
    }
}