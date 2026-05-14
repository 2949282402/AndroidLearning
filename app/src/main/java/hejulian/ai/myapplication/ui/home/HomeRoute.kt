package hejulian.ai.myapplication.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import hejulian.ai.myapplication.ui.components.InfoCard
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(){
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
            onMenuClick = {
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
    HomeRoute()
}

