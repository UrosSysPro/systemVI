package com.systempro.systemvi.compose_app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

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
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Theme.Typography,
        content = {
            Scaffold(
                modifier = Modifier,
                topBar = {TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {Text(text="App Bar")}
                )},

                content = {innerPadding-> Column(modifier = Modifier.padding(innerPadding)) {
                    for(index in 0 until 10){
                        Text(text = "Item $index")
                    }
                }}
            )
        }
    )
}

@Preview(showSystemUi = true, name = "Home page test")
@Composable
fun HomePagePreview() {
    HomePage(dynamicColor = false, darkTheme = true)
}