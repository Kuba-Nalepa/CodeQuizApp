package com.jakubn.codequizapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.jakubn.codequizapp.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jakubn.codequizapp.domain.model.User

@Composable
fun UserProfileEditScreen(user: User, navController: NavController) {

    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(R.drawable.generic_avatar),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier
                    .offset(x = (-4).dp, y = (-4).dp)
                    .background(Color(0xFF007BFF), CircleShape)
                    .padding(6.dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name & phone below avatar
        user.name?.let {
            Text(text = it, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Editable fields
        user.name?.let { EditableField(label = "YOUR NAME", value = it, onValueChange = { newName = it }) }
        user.email?.let { EditableField(label = "YOUR EMAIL", value = it, onValueChange = { newEmail = it }) }

        Spacer(modifier = Modifier.height(24.dp))

        // Update button
        Button(
            onClick = { /* Save changes here */ },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("UPDATE", color = Color.White)
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, color = Color(0xFF007BFF), fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}