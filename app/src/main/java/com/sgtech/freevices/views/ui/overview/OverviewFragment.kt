package com.sgtech.freevices.views.ui.overview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sgtech.freevices.R
import com.sgtech.freevices.databinding.FragmentOverviewBinding
import com.sgtech.freevices.utils.FirebaseUtils.dataHandlerCurrentMonth
import com.sgtech.freevices.utils.FirebaseUtils.hideLoadingDialog
import com.sgtech.freevices.utils.FirebaseUtils.showLoadingDialog
import com.sgtech.freevices.views.ui.AlcoholCard
import com.sgtech.freevices.views.ui.OtherCard
import com.sgtech.freevices.views.ui.PartiesCard
import com.sgtech.freevices.views.ui.TobaccoCard
import com.sgtech.freevices.views.ui.TotalCardForMonth
import com.sgtech.freevices.views.ui.overview.ui.theme.FreeVicesTheme

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!
    private val appContext: Context by lazy { requireContext() }
    private val viewModel: OverviewViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.overviewComposeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FreeVicesTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.menu_overview),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(120.dp))
                                    },
                                navigationIcon = {
                                    IconButton(onClick = { requireActivity().onBackPressed() }) {
                                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                                    }
                                },
                                actions = {
                                    // Get methods from MainActivity Top App Bar
                                }
                            )
                        },
                        content = { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TobaccoCard(0)
                                Spacer(modifier = Modifier.padding(16.dp))
                                AlcoholCard(0)
                                Spacer(modifier = Modifier.padding(16.dp))
                                PartiesCard(0)
                                Spacer(modifier = Modifier.padding(16.dp))
                                OtherCard(0)
                                Spacer(modifier = Modifier.padding(32.dp))
                                TotalCardForMonth(0)
                            }
                        }
                    )
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialog(appContext)
        dataHandlerCurrentMonth(appContext) { dataMap ->
            Log.d("OverviewFragment:dataMap", dataMap.toString())
            viewModel.updateTotalData(dataMap)
        }
        hideLoadingDialog()

        viewModel.totalData.observe(viewLifecycleOwner) { dataMap ->
            updateViews(dataMap)
        }
    }

    private fun updateViews(data : Map<String, Float>) {
        val tobaccoValue = data[getString(R.string.tobacco)] ?: 0.0f
        val alcoholValue = data[getString(R.string.alcohol)] ?: 0.0f
        val partiesValue = data[getString(R.string.parties)] ?: 0.0f
        val othersValue = data[getString(R.string.others)] ?: 0.0f
        val total = tobaccoValue + alcoholValue + partiesValue + othersValue

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}