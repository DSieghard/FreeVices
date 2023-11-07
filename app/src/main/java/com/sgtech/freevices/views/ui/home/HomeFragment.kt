package com.sgtech.freevices.views.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentHomeBinding
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.utils.FirebaseUtils.dataHandler
import com.sgtech.freevices.utils.FirebaseUtils.hideLoadingDialog
import com.sgtech.freevices.utils.FirebaseUtils.showLoadingDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val appContext: Context by lazy { requireContext() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(appContext)
        dataHandler(appContext) { dataMap ->
            viewModel.updatePieChartData(dataMap)
            viewModel.updateTotalData(dataMap)
        }
        hideLoadingDialog()
        val menuFab = binding.menuEFab
        menuFab.setOnClickListener { showPopupMenu(it) }

        viewModel.pieChartData.observe(viewLifecycleOwner) { dataMap ->
            updatePieChart(dataMap)
        }

        viewModel.totalData.observe(viewLifecycleOwner) { dataMap ->
            totalCalculate(dataMap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updatePieChart(dataMap: Map<String, Float>) {
        val pieChart = binding.overviewGraph

        val entries = mutableListOf<PieEntry>()
        for ((category, value) in dataMap) {
            entries.add(PieEntry(value, category))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            ContextCompat.getColor(appContext, R.color.tobacco),
            ContextCompat.getColor(appContext, R.color.alcohol),
            ContextCompat.getColor(appContext, R.color.parties),
            ContextCompat.getColor(appContext, R.color.others)
        )
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.setUsePercentValues(true)
        pieChart.isDrawHoleEnabled = false
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
    }

    private fun totalCalculate(dataForWeek: Map<String, Float>) {
        val totalMonth = binding.totalMonth

        val tobaccoValue = dataForWeek[getString(R.string.tobacco)] ?: 0.0f
        val alcoholValue = dataForWeek[getString(R.string.alcohol)] ?: 0.0f
        val partiesValue = dataForWeek[getString(R.string.parties)] ?: 0.0f
        val othersValue = dataForWeek[getString(R.string.others)] ?: 0.0f

        val total = tobaccoValue + alcoholValue + partiesValue + othersValue

        binding.tobaccoValueText.text = getString(R.string.tobacco_value, tobaccoValue.toInt())
        binding.alcoholValueText.text = getString(R.string.alcohol_value, alcoholValue.toInt())
        binding.partiesValueText.text = getString(R.string.parties_value, partiesValue.toInt())
        binding.otherValueText.text = getString(R.string.others_value, othersValue.toInt())
        totalMonth.text = getString(R.string.total_value, total.toInt())
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_fab, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.alcoholMenu -> {
                    showExpenseDialog(appContext, getString(R.string.alcohol), view)
                    return@setOnMenuItemClickListener true
                }

                R.id.partiesMenu -> {
                    showExpenseDialog(appContext, getString(R.string.parties), view)
                    return@setOnMenuItemClickListener true
                }

                R.id.othersMenu -> {
                    showExpenseDialog(appContext, getString(R.string.others), view)
                    return@setOnMenuItemClickListener true
                }

                R.id.tobaccoMenu -> {
                    showExpenseDialog(appContext, getString(R.string.tobacco), view)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        popupMenu.show()
    }

    private fun showExpenseDialog(context: Context, category: String, rootView: View) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_expense, null)

        val editText = dialogView.findViewById<TextInputEditText>(R.id.amount_edit_text)
        editText.hint = "$"

        builder.setTitle(context.getString(R.string.how_many))
        builder.setView(dialogView)
        builder.setPositiveButton(context.getString(R.string.add)) { _, _ ->
            val expenseAmount = editText.text.toString()
            val expenseValue = expenseAmount.toIntOrNull() ?: 0

            showLoadingDialog(context)
            FirebaseUtils.addDataToCategory(context, category, expenseValue, rootView, onSuccess = {
                dataHandler(context) { dataMap ->
                    viewModel.updatePieChartData(dataMap)
                    viewModel.updateTotalData(dataMap)
                    hideLoadingDialog()
                }
            })
        }

        builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
            Snackbar.make(rootView, context.getString(R.string.cancelled), Snackbar.LENGTH_SHORT).show()
        }
        builder.show()
    }

}
