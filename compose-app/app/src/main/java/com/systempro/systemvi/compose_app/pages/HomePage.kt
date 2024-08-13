package com.systempro.systemvi.compose_app.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.systempro.systemvi.compose_app.utils.AppState
import com.systempro.systemvi.compose_app.utils.Theme
import com.systempro.systemvi.compose_app.utils.navigationItems
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(appState: AppState = viewModel()) {
    val lightTheme = appState.useLightTheme
    val dynamicTheme = appState.useDynamicTheme
    val colorScheme=when{
        dynamicTheme  &&  lightTheme -> dynamicLightColorScheme(LocalContext.current)
        dynamicTheme  && !lightTheme -> dynamicDarkColorScheme(LocalContext.current)
        !dynamicTheme &&  lightTheme -> Theme.LightColorScheme
        /*!dynamic !light*/     else -> Theme.DarkColorScheme
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var pageIndex by rememberSaveable { mutableIntStateOf(0) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Theme.Typography
    ) {
        ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                    Text(text = "Navigation Drawer", style = MaterialTheme.typography.headlineMedium)
                }
                LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                    items(navigationItems.size, key = {it}){
                        val navigationItem=navigationItems[it]
                        NavigationDrawerItem(
                            label = {Text(navigationItem.name)},
                            selected = it==pageIndex,
                            onClick = {scope.launch {
                                pageIndex=it
                                drawerState.close()
                            }}
                        )
                    }
                }
            }
        }) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = { IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            content = { Icon(Icons.Rounded.Menu, contentDescription = "Menu") }
                        )},
                        actions = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                content = { Icon(Icons.Rounded.Search, contentDescription = "Menu") }
                            )
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                content = { Icon(Icons.Rounded.MoreVert, contentDescription = "Menu") }
                            )
                        },
                        title = { Text(text = "App Bar ") }
                    )
                },

                content = { innerPadding ->
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        navigationItems[pageIndex].builder()
                    }
                }
            )
        }
    }
}

@Preview(showSystemUi = true, name = "Home page test")
@Composable
fun HomePagePreview() {
    HomePage()
}