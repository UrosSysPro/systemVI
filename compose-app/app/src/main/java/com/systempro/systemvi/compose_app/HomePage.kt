package com.systempro.systemvi.compose_app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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
    val scope= rememberCoroutineScope()
    val (state,setState) = remember {mutableStateOf("text")}
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Theme.Typography
    ) {
        ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                Text(text = "eee")
                Text(text = "eee")
                Text(text = "eee")
                Text(text = "eee")
                NavigationDrawerItem(label = { Text(text = "eee") }, selected = false, onClick = { print("eee") })
            }
        }) {
            Scaffold(
                modifier = Modifier.padding(10.dp),
                topBar = {
                    TopAppBar(
                        actions = {
                            Button(onClick = { scope.launch{drawerState.open()} }) {
                                Text(text = "ee")
                            }
                        },

                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = "App Bar aaa") }
                    )
                },

                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (index in 0 until 10) {
                            Text(text = "Item $index")
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
    HomePage(dynamicColor = false, darkTheme = true)
}