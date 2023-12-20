package com.example.navgraphapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.navgraphapplication.databinding.FragmentSecondBinding
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SecondFragment : Fragment() {

    private val dataSource = MutableLiveData<Data>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val random = Random(System.nanoTime())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                while (true) {
                    dataSource.value = Data(
                        pages = listOf(
                            Page(
                                dataPoints = listOf(
                                    DataPoint(x = 1, y = random.nextInt().mod(10)),
                                    DataPoint(x = 2, y = random.nextInt().mod(10)),
                                    DataPoint(x = 3, y = random.nextInt().mod(10)),
                                ),
                            ),
                            Page(
                                dataPoints = listOf(
                                    DataPoint(x = 1, y = random.nextInt().mod(10)),
                                    DataPoint(x = 2, y = random.nextInt().mod(10)),
                                    DataPoint(x = 3, y = random.nextInt().mod(10)),
                                ),
                            ),
                            Page(
                                dataPoints = listOf(
                                    DataPoint(x = 1, y = random.nextInt().mod(10)),
                                    DataPoint(x = 2, y = random.nextInt().mod(10)),
                                    DataPoint(x = 3, y = random.nextInt().mod(10)),
                                ),
                            ),
                        ),
                    )

                    delay(2000)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false).apply {
            FragmentSecondBinding.bind(this).composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val data by dataSource.observeAsState()

                    data?.let {
                        Content(data = it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(data: Data) {
    val pagerState = rememberPagerState(initialPage = data.currentScreen) {
        data.pages.size
    }

    MaterialTheme {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            state = pagerState,
        ) { pageIndex ->
            data.pages.getOrNull(pageIndex)?.let { page ->
                Page(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.LightGray),
                    index = pageIndex,
                    page = page,
                )
            }
        }
    }
}

@Composable
private fun Page(
    index: Int,
    page: Page,
    modifier: Modifier,
) {
    val entryProducer = remember {
        ChartEntryModelProducer()
    }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.wrapContentHeight(),
            text = "Page $index"
        )

        val dataPoints = page.dataPoints.map { it.x to it.y }.toTypedArray()
        val chartEntryModel = entryModelOf(*dataPoints)

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(chartEntryModel) {
            coroutineScope.launch {
                delay(500)
                entryProducer.setEntriesSuspending(chartEntryModel.entries).await()
            }
        }

        Chart(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp)
                .background(Color.White),
            chart = columnChart(),
            chartModelProducer = entryProducer,
        )
    }
}

@Preview
@Composable
private fun PagePreview() {
    Content(
        data = Data(
            pages = listOf(
                Page(
                    dataPoints = listOf(
                        DataPoint(x = 1, y = 1),
                        DataPoint(x = 2, y = 2),
                        DataPoint(x = 3, y = 3),
                    ),
                ),
                Page(
                    dataPoints = listOf(
                        DataPoint(x = 1, y = 7),
                        DataPoint(x = 2, y = 6),
                        DataPoint(x = 3, y = 5),
                    ),
                ),
                Page(
                    dataPoints = listOf(
                        DataPoint(x = 1, y = 3),
                        DataPoint(x = 2, y = 4),
                        DataPoint(x = 3, y = 3),
                    ),
                ),
            ),
        )
    )
}

private data class Data(
    val pages: List<Page>,
    val currentScreen: Int = 0,
)

private data class Page(
    val dataPoints: List<DataPoint>,
)

private data class DataPoint(
    val x: Int,
    val y: Int,
)