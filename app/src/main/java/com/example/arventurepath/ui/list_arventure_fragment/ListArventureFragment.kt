package com.example.arventurepath.ui.list_arventure_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.arventurepath.R
import com.example.arventurepath.data.ItemArventure
import com.example.arventurepath.databinding.FragmentListArventureBinding
import com.google.android.material.tabs.TabLayoutMediator

class ListArventureFragment : Fragment(), ArventurePagerAdapter.ArventureListener {

    private lateinit var binding: FragmentListArventureBinding
    private lateinit var mAdapter: ArventurePagerAdapter
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

    }

    private fun setupAdapter() {
        mAdapter = ArventurePagerAdapter(requireContext(), this, hardcodedList())

        binding.viewPager.adapter = mAdapter

        setupCardsVisionConfig(binding.viewPager)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()


    }

    private fun hardcodedList(): MutableList<ItemArventure> {
        return mutableListOf(
            ItemArventure(
                1,
                "Este es un título largo",
                "En un pequeño pueblo, los cuervos son vistos como mensajeros de la muerte. Una joven descubre que su presencia en la noche está relacionada con un secreto oscuro que amenaza con destruir todo lo que ama. ",
                "3.45",
                9.4,
                "http://abp-politecnics.com/2024/dam01/filesToServer/imgAchievement/Busca%20de%20redenci%C3%B3n%20.png"
            ),
            ItemArventure(
                2,
                "Otro título de prueba",
                "asdasd asdasd asdas dasd ",
                "3.45",
                9.4,
                "http://abp-politecnics.com/2024/dam01/filesToServer/imgAchievement/Android.JPG"
            ),
            ItemArventure(
                3,
                "Un título más",
                "asdasd asdasd asdas dasd ",
                "3.45",
                9.4,
                "http://abp-politecnics.com/2024/dam01/filesToServer/imgAchievement/Cuervos%20en%20la%20oscuridad.png"
            ),
            ItemArventure(
                4,
                "Títulos everywhere",
                "asdasd asdasd asdas dasd ",
                "3.45",
                9.4,
                "http://abp-politecnics.com/2024/dam01/filesToServer/imgStory/El%20Pacto%20con%20el%20Djinn.png"
            ),
            ItemArventure(
                5,
                "Este es el título más largo y sin sentido del mundo",
                "asdasd asdasd asdas dasd ",
                "3.45",
                9.4,
                "data:image/jpeg;base64,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"
            ),
        )
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