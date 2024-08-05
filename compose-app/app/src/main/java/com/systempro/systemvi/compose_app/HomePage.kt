package com.systempro.systemvi.compose_app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.request.*
import kotlinx.coroutines.launch

data class State(var a:Int,var b:String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false
) {
    val colorScheme = if(darkTheme){
        if(dynamicColor) dynamicDarkColorScheme(LocalContext.current) else Theme.DarkColorScheme
    }else{
        if(dynamicColor) dynamicLightColorScheme(LocalContext.current) else Theme.LightColorScheme
    }
    val drawerState= rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var clicks by rememberSaveable{ mutableIntStateOf(0) }
    var customState by remember { mutableStateOf(State(0,"hello")) }
    val appState=viewModel<AppState>()
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Theme.Typography
    ) {
        ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                Box(Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                    Text(text = "Navigation Drawer",)
                }
                LazyColumn {
                    items(10, key = {it}){
                        NavigationDrawerItem(
                            label = { Text(text = "eee") },
                            selected = false,
                            onClick = { print("eee") }
                        )
                    }
                }
            }
        }) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = { Button(onClick ={ scope.launch { drawerState.open() }}) {
                            Text(text = "open")
                        }},
                        actions = {
                            Button(onClick = { scope.launch{drawerState.open()} }) {
                                Text(text = "ee")
                            }
                        },

                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = "App Bar") }
                    )
                },

                content = { innerPadding ->
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "clicks:$clicks")
                        Button(onClick = {clicks++}) {
                            Text(text = "add")
                        }
                        Text(text = "text: ${customState.b} counter: ${customState.a}")
                        Button(onClick = {
                            customState=State(customState.a+1,"hello")
                            println("customState: ${customState.a}")
                        }) {
                            Text(text = "add to custom state")
                        }
                        Text(text = "appState.counter: ${appState.counter()}")
                        Button(onClick = {
                            appState.add()
                        }) {
                            Text(text = "add to app state counter")
                        }
                    }
                }
            )
        }
    }
}

@Preview(showSystemUi = true, name = "Home page test")
@Composable
fun HomePagePreview() {
    HomePage(dynamicColor = true, darkTheme = true)
}