package com.sgtech.freevices.views.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.textview.MaterialTextView
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentHomeBinding
import com.sgtech.freevices.utils.FirebaseUtils.getDataFromFirestore
import com.sgtech.freevices.utils.FirebaseUtils.hideLoadingDialog
import com.sgtech.freevices.utils.FirebaseUtils.showLoadingDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var pieChart: PieChart
    private lateinit var tobaccoDataText: MaterialTextView
    private lateinit var alcoholDataText: MaterialTextView
    private lateinit var partiesDataText: MaterialTextView
    private lateinit var othersDataText: MaterialTextView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tobaccoDataText = root.findViewById(R.id.tobaccoValueText)
        alcoholDataText = root.findViewById(R.id.alcoholValueText)
        partiesDataText = root.findViewById(R.id.partiesValueText)
        othersDataText = root.findViewById(R.id.otherValueText)
        pieChart = root.findViewById(R.id.tobaccoGraph)
        showLoadingDialog(root.context)
        pieChartHandler()
        setTextFromDatabase()
        hideLoadingDialog()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pieChartHandler() {
        getDataFromFirestore(
            onSuccess = { data ->
                val entries = data.map { (name, value) ->
                    PieEntry(value, name)
                }

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                val pieData = PieData(dataSet)
                pieChart.data = pieData

                val backgroundColor =
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pieChartBackgroundColorDark
                            )
                        }

                        else -> {
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pieChartBackgroundColorLight
                            )
                        }
                    }

                pieChart.setBackgroundColor(backgroundColor)
                pieChart.invalidate()
            },
            onFailure = { exception ->
                Log.e("TAG", "Error retrieving data: $exception")
            }
        )
    }

    private fun setTextFromDatabase() {
        getDataFromFirestore(
            onSuccess = { dataList ->
                val tobaccoValue = dataList.find { it.first == "tobacco" }?.second?.toInt() ?: 0
                val alcoholValue = dataList.find { it.first == "alcohol" }?.second?.toInt() ?: 0
                val partiesValue = dataList.find { it.first == "parties" }?.second?.toInt() ?: 0
                val othersValue = dataList.find { it.first == "others" }?.second?.toInt() ?: 0

                val tobaccoText = getString(R.string.tobacco_value).format(tobaccoValue)
                val alcoholText = getString(R.string.alcohol_value).format(alcoholValue)
                val partiesText = getString(R.string.parties_value).format(partiesValue)
                val othersText = getString(R.string.others_value).format(othersValue)

                tobaccoDataText.text = tobaccoText
                alcoholDataText.text = alcoholText
                partiesDataText.text = partiesText
                othersDataText.text = othersText

                Log.d("HomeFragment", "Data list size: ${dataList.size}")
                Log.d("HomeFragment", "Data list: $dataList")
            },
            onFailure = { exception ->
                Log.e("HomeFragment", "Failed to retrieve data: $exception")
            }
        )

    }
}
