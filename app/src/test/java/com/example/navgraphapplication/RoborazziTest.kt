package com.example.navgraphapplication

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

// Tips: You can use Robolectric while using AndroidJUnit4
@RunWith(AndroidJUnit4::class)
// Enable Robolectric Native Graphics (RNG)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoborazziTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<AppCompatActivity>()

    @Test
    fun captureRoboImageSample() {
        composeTestRule.activityRule.scenario.onActivity {activity ->
            val fragment = SecondFragment()
            fragment.dataSource.value = Data(
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

            activity.setContentView(fragment.onCreateView(LayoutInflater.from(activity), null, null))

            composeTestRule.mainClock.autoAdvance = false
            composeTestRule.mainClock.advanceTimeBy(1000)

            // Capture screen
            onView(ViewMatchers.isRoot())
                // If you don't specify a screenshot file name, Roborazzi will automatically use the method name as the file name for you.
                // The format of the file name will be as follows:
                // build/outputs/roborazzi/com_..._ManualTest_captureRoboImageSample.png
                .captureRoboImage()
        }
    }
}