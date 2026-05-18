package hejulian.ai.myapplication.ui.AIPage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import hejulian.ai.myapplication.core.logging.AppLogger
import hejulian.ai.myapplication.core.logging.InMemoryAppLogger
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    logger: AppLogger
){
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxSize(),
                drawerContainerColor = Color(0xFFF7F7F7),
                drawerContentColor =  Color.Black
            ) {
                DrawerMenuContent()
            }
        }
    ) {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            logger = logger,
            onMenuClick = {
                logger.info("AI drawer opened")
                scope.launch {
                    drawerState.open()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeRouteView(){
    HomeRoute(logger = InMemoryAppLogger())
}

