package hejulian.ai.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hejulian.ai.myapplication.ui.componets.InfoCard
import hejulian.ai.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoCard(
                            title = "hejulian.cn",
                            subTitle = "运行中",
                            icon = Icons.Default.Storage,
                            modifier = Modifier.padding(12.dp),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "点击了窗体",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onDetailsClick = {
                                Toast.makeText(
                                    context,
                                    "点击了更多",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        val context = LocalContext.current
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Greeting(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoCard(
                    title = "hejulian.cn",
                    subTitle = "运行中",
                    icon = Icons.Default.Storage,
                    modifier = Modifier.padding(12.dp),
                    onDetailsClick = {
                        Toast.makeText(
                            context,
                            "点击了更多",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}