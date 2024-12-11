package com.jakubn.codequizapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel(), logOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(horizontal = 16.dp)
    ) {
        Button(onClick = {
            viewModel.signOut()
            logOut()
        }) {
            Text("LogOut")
        }
    }
}

