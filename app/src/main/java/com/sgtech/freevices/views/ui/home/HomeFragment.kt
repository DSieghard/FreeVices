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
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentHomeBinding
import com.sgtech.freevices.utils.FirebaseUtils.getDataFromFirestore

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

        pieChart = root.findViewById(R.id.tobaccoGraph)
        pieChartHandler()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pieChartHandler() {
        // Llamar a la función para recuperar los datos de Firestore
        getDataFromFirestore(
            onSuccess = { data ->
                // Crear una lista de PieEntry a partir de los datos recuperados
                val entries = data.map { (name, value) ->
                    PieEntry(value, name)
                }

                // Crear el conjunto de datos y asignar colores
                val dataSet = PieDataSet(entries, "")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                // Crear el objeto PieData y establecerlo en tu PieChart
                val pieData = PieData(dataSet)
                pieChart.data = pieData

                // Configurar colores de fondo según el tema
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

                // Configurar colores de fondo en el gráfico
                pieChart.setBackgroundColor(backgroundColor)

                // Invalidar el gráfico para que se muestren los cambios
                pieChart.invalidate()
            },
            onFailure = { exception ->
                // Manejar errores, por ejemplo, mostrar un mensaje de error
                Log.e("TAG", "Error al recuperar datos: $exception")
            }
        )
    }
}
