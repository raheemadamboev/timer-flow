package xyz.teamgravity.timerflowdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.teamgravity.timer_flow.Timer
import xyz.teamgravity.timerflowdemo.ui.theme.TimerFlowDemoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimerFlowDemoTheme {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { padding ->
                    val timer = remember {
                        Timer().apply {
                            timerDuration = 6_000L
                            start()
                        }
                    }
                    val time by timer.time.collectAsStateWithLifecycle()
                    val progress by remember { derivedStateOf { time / 6_000F } }
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        label = "progress"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 16.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Text(
                            text = time.toString()
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            gapSize = (-15).dp,
                            drawStopIndicator = {},
                            modifier = Modifier.fillMaxWidth(0.8F)
                        )
                    }
                }
            }
        }
    }
}