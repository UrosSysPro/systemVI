package com.systempro.systemvi.compose_app.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.systempro.systemvi.compose_app.composables.AboutView
import com.systempro.systemvi.compose_app.composables.SettingView

data class NavigationLocation(
    val name:String,
    val builder:@Composable ()->Unit,
    val icon: ImageVector
)

val navigationItems:List<NavigationLocation> = listOf(
    NavigationLocation("Settings",{ SettingView() }, Icons.Rounded.Settings),
    NavigationLocation("About",{ AboutView() }, Icons.Rounded.Settings),
)