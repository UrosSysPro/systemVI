package com.systempro.systemvi.compose_app.pages

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewTest() {
    val mUrl = "https://www.google.com"

    val scope = rememberCoroutineScope()

    var webView by remember { mutableStateOf<WebView?>(null) }
    var url by remember { mutableStateOf("http://localhost:3000") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebView") },
                actions = {
                    TextButton(onClick = {scope.launch {
                        webView?.reload()
                    }}) {
                        Text("refresh")
                    }
                    TextButton(onClick = {scope.launch {
                        url="https://www.firefox.com"
                    }}) {
                        Text("firefox.com")
                    }
                    TextButton(onClick = {scope.launch {
                        url="https://www.google.com"
                    }}) {
                        Text("google.com")
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = {context->
                webView=WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                webView!!
            }, update = {
                it.loadUrl(url)
            })
    }
}