package com.novaroject.novtodolist.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit, onAbout: () -> Unit) {
    val user     = FirebaseAuth.getInstance().currentUser
    val name     = user?.displayName ?: "Пользователь"
    val email    = user?.email ?: ""
    val initial  = name.firstOrNull()?.uppercase() ?: "N"
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выйти из аккаунта?") },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                }) { Text("Выйти", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Профиль", fontWeight = FontWeight.Bold) }) }
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar
            Box(
                Modifier
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initial, style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(Modifier.height(16.dp))
            Text(name,  style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(32.dp))

            // Options card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text("Уведомления", fontWeight = FontWeight.Medium) },
                    leadingContent = {
                        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingContent = {
                        var enabled by remember { mutableStateOf(true) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ListItem(
                    headlineContent = { Text("Синхронизация", fontWeight = FontWeight.Medium) },
                    supportingContent = { Text("Изменения видны на всех устройствах") },
                    leadingContent = {
                        Icon(Icons.Default.Sync, null, tint = MaterialTheme.colorScheme.secondary)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text("О приложении", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.then(Modifier.padding(0.dp)),
                    colors = ListItemDefaults.colors(),
                )
            }

            // Make it clickable — wrap in Surface
            Spacer(Modifier.height(0.dp))
            Surface(
                onClick = onAbout,
                modifier = Modifier.fillMaxWidth().offset(y = (-16).dp),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 0.dp
            ) {
                ListItem(
                    headlineContent = { Text("О приложении", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    contentColor   = MaterialTheme.colorScheme.error
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Logout, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Выйти из аккаунта", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
