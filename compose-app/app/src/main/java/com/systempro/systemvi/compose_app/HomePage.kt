package com.systempro.systemvi.compose_app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

data class NavigationLocation(
    val id:Int,
    val name:String,
    val builder:@Composable ()->Unit,
    val icon:ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(appState:AppState = viewModel()) {
    val lightTheme=appState.useLightTheme
    val dynamicTheme=appState.useDynamicTheme
    val colorScheme=when{
        dynamicTheme  &&  lightTheme -> dynamicLightColorScheme(LocalContext.current)
        dynamicTheme  && !lightTheme -> dynamicDarkColorScheme(LocalContext.current)
        !dynamicTheme &&  lightTheme -> Theme.LightColorScheme
        /*!dynamic !light*/     else -> Theme.DarkColorScheme
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigationItems:List<NavigationLocation> = listOf(
        NavigationLocation(0,"Theme",{ Box(modifier = Modifier){Text("settings page")} },Icons.Rounded.Settings),
        NavigationLocation(1,"Theme",{ Box(modifier = Modifier)},Icons.Rounded.Settings),
        NavigationLocation(2,"Theme",{ Box(modifier = Modifier)},Icons.Rounded.Settings),
        NavigationLocation(3,"Theme",{ Box(modifier = Modifier)},Icons.Rounded.Settings),
    )

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
                    items(navigationItems.size, key = {navigationItems[it].id}){
                        val navigationItem=navigationItems[it]
                        NavigationDrawerItem(
                            label = {Text(navigationItem.name)},
                            selected = navigationItem.id==pageIndex,
                            onClick = {pageIndex=it}
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
//                        colors = TopAppBarDefaults.topAppBarColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
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
                        Text("dynamic mode")
                        Switch(
                            checked = appState.useDynamicTheme,
                            onCheckedChange = {value->appState.useDynamicTheme = value}
                        )
                        Text("light mode")
                        Switch(
                            checked = appState.useLightTheme,
                            onCheckedChange = {value->appState.useLightTheme = value}
                        )
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