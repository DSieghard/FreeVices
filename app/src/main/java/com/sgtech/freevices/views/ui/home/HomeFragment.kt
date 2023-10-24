package com.sgtech.freevices.views.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var pieChart: PieChart

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pieChart = root.findViewById(R.id.mainGraph)
        pieChartHandler()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pieChartHandler() {
        val entries = listOf(
            PieEntry(30f, "Tabaco"),
            PieEntry(35f, "Alcohol"),
            PieEntry(40f, "Fiestas"),
            PieEntry(20f, "Otros")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val pieData = PieData(dataSet)

        val backgroundColor = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                ContextCompat.getColor(requireContext(), R.color.pieChartBackgroundColorDark)
            }
            else -> {
                ContextCompat.getColor(requireContext(), R.color.pieChartBackgroundColorLight)
            }
        }


        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.setBackgroundColor(backgroundColor)
        pieChart.setDrawEntryLabels(false)
        pieChart.invalidate()
    }
}
