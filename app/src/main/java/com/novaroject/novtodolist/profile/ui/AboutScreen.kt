package com.novaroject.novtodolist.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О приложении") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(
                Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("N", style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(Modifier.height(12.dp))
            Text("novTo-Do List", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("v1.0.0", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                    .padding(horizontal = 10.dp, vertical = 4.dp))

            Spacer(Modifier.height(28.dp))

            // App info
            AboutSection("О ПРИЛОЖЕНИИ") {
                AboutRow(Icons.Default.Tag,       "Версия",       "1.0.0")
                AboutRow(Icons.Default.Business,  "Компания",     "Nova Project")
                AboutRow(Icons.Default.Code,      "Разработчик",  "XenorKing")
            }

            Spacer(Modifier.height(16.dp))

            // Contacts
            AboutSection("ОБРАТНАЯ СВЯЗЬ") {
                AboutRow(Icons.Default.Email,    "Email",    "novaprojecthelp@mail.ru")
                AboutRow(Icons.Default.Send,     "Telegram", "@NovaProjectNews")
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "© 2025 Nova Project. Все права защищены.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutSection(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) { content() }
    }
}

@Composable
private fun AboutRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    ListItem(
        headlineContent = { Text(label, fontWeight = FontWeight.Medium) },
        trailingContent = { Text(value, color = MaterialTheme.colorScheme.onSurface) },
        leadingContent  = {
            Box(
                Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant)
}
