package com.sgtech.freevices.views.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.textview.MaterialTextView
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentHomeBinding
import com.sgtech.freevices.utils.FirebaseUtils.dataHandler
import com.sgtech.freevices.utils.FirebaseUtils.hideLoadingDialog
import com.sgtech.freevices.utils.FirebaseUtils.showLoadingDialog

class HomeFragment : Fragment(), DataUpdateListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var pieChart: PieChart
    private lateinit var tobaccoDataText: MaterialTextView
    private lateinit var alcoholDataText: MaterialTextView
    private lateinit var partiesDataText: MaterialTextView
    private lateinit var othersDataText: MaterialTextView
    private lateinit var totalMonth: MaterialTextView
    private lateinit var viewModel: HomeViewModel



    private val binding get() = _binding!!

    override fun reloadData() {
        showLoadingDialog(requireContext())
        dataHandler(requireContext()) { dataMap ->
            viewModel.updatePieChartData(dataMap)
            viewModel.updateTotalData(dataMap)
        }
        hideLoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getPieChartData().observe(viewLifecycleOwner, Observer { newData ->
            updatePieChart(newData)
        })
        viewModel.getTotalData().observe(viewLifecycleOwner, Observer { newData ->
            totalCalculate(newData)
        })
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tobaccoDataText = root.findViewById(R.id.tobaccoValueText)
        alcoholDataText = root.findViewById(R.id.alcoholValueText)
        partiesDataText = root.findViewById(R.id.partiesValueText)
        othersDataText = root.findViewById(R.id.otherValueText)
        pieChart = root.findViewById(R.id.overviewGraph)
        totalMonth = root.findViewById(R.id.total_month)
        Log.d("HomeFragment", "onViewCreated")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(requireContext())
        dataHandler(requireContext()) { dataMap ->
            updatePieChart(dataMap)
            totalCalculate(dataForWeek = dataMap)
        }
        hideLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updatePieChart(dataMap: Map<String, Float>) {
        // Configurar el gráfico de pastel (PieChart) y asignar los datos
        val pieChart = binding.root.findViewById<PieChart>(R.id.overviewGraph) // Asegúrate de tener la referencia correcta al gráfico

        val entries = mutableListOf<PieEntry>()
        for ((category, value) in dataMap) {
            entries.add(PieEntry(value, category))
        }

        val dataSet = PieDataSet(entries, "Categorías")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.tobacco),
            ContextCompat.getColor(requireContext(), R.color.alcohol),
            ContextCompat.getColor(requireContext(), R.color.parties),
            ContextCompat.getColor(requireContext(), R.color.others)
        )
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        // Configurar opciones de visualización
        pieChart.setUsePercentValues(true)
        pieChart.centerText = "Categorías"
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
    }


    private fun totalCalculate(dataForWeek: Map<String, Float>) {
        val totalMonth = requireView().findViewById<MaterialTextView>(R.id.total_month)

        // Calcula el total y muestra los valores
        val tobaccoValue = dataForWeek[getString(R.string.tobacco)] ?: 0.0f
        val alcoholValue = dataForWeek[getString(R.string.alcohol)] ?: 0.0f
        val partiesValue = dataForWeek[getString(R.string.parties)] ?: 0.0f
        val othersValue = dataForWeek[getString(R.string.others)] ?: 0.0f

        val total = tobaccoValue + alcoholValue + partiesValue + othersValue

        tobaccoDataText.text = getString(R.string.tobacco_value, tobaccoValue.toInt())
        alcoholDataText.text = getString(R.string.alcohol_value, alcoholValue.toInt())
        partiesDataText.text = getString(R.string.parties_value, partiesValue.toInt())
        othersDataText.text = getString(R.string.others_value, othersValue.toInt())
        totalMonth.text = getString(R.string.total_value, total.toInt())
    }
}
