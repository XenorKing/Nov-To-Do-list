package com.novaroject.novtodolist.profile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // Top bar
        Box(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0818))
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF8888AA))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // App icon
            Box(
                modifier = Modifier.size(88.dp).clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0E0C1C)),
                contentAlignment = Alignment.Center
            ) {
                Text("N", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan)
            }

            Spacer(Modifier.height(14.dp))
            Text("novTo-Do List", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A0E2E)).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("v1.0.0", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))

            // О ПРИЛОЖЕНИИ section
            AboutSection("О ПРИЛОЖЕНИИ") {
                AboutInfoRow(Icons.Default.Tag,      Color(0xFFA855F7), "Версия",      "1.0.0")
                AboutInfoRow(Icons.Default.Business, Color(0xFF00B4FF), "Компания",    "Nova Project")
                AboutInfoRow(Icons.Default.Code,     Color(0xFF00E5FF), "Разработчик", "XenorKing")
                AboutInfoRow(Icons.Default.Brush,    Color(0xFF9D00FF), "Дизайнер UI", "Mitoka",      last = true)
            }

            Spacer(Modifier.height(20.dp))

            // ОБРАТНАЯ СВЯЗЬ section
            AboutSection("ОБРАТНАЯ СВЯЗЬ") {
                AboutLinkRow(
                    icon      = Icons.Default.Email,
                    iconBg    = Color(0xFFFF5722),
                    title     = "Email",
                    subtitle  = "novaprojecthelp@mail.ru",
                    onClick   = { ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:novaprojecthelp@mail.ru"))) }
                )
                AboutLinkRow(
                    icon      = Icons.Default.Send,
                    iconBg    = Color(0xFF0088CC),
                    title     = "Telegram",
                    subtitle  = "@NovaProjectNews",
                    onClick   = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/NovaProjectNews"))) }
                )
                AboutLinkRow(
                    icon      = Icons.Default.Public,
                    iconBg    = Color(0xFF4680C2),
                    title     = "ВКонтакте",
                    subtitle  = "Nova Project VK",
                    onClick   = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/novaproject"))) },
                    last      = true
                )
            }

            Spacer(Modifier.height(32.dp))
            Text("© 2025 Nova Project. Все права защищены.",
                color = Color(0xFF4A4A7A), fontSize = 11.sp)
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AboutSection(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, color = Color(0xFF6666AA), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            color = Color(0xFF100D20)) { content() }
    }
}

@Composable
private fun AboutInfoRow(
    icon: ImageVector, iconBg: Color, label: String, value: String, last: Boolean = false
) {
    ListItem(
        headlineContent = { Text(label, fontWeight = FontWeight.Medium, color = Color.White) },
        trailingContent = { Text(value, color = Color(0xFF8888AA), fontSize = 14.sp) },
        leadingContent  = {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconBg, modifier = Modifier.size(18.dp))
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    if (!last) HorizontalDivider(color = Color(0xFF1A1730), modifier = Modifier.padding(start = 64.dp))
}

@Composable
private fun AboutLinkRow(
    icon: ImageVector, iconBg: Color, title: String, subtitle: String, onClick: () -> Unit, last: Boolean = false
) {
    Surface(onClick = onClick, color = Color.Transparent) {
        ListItem(
            headlineContent  = { Text(title, fontWeight = FontWeight.SemiBold, color = Color(0xFF9999BB), fontSize = 12.sp) },
            supportingContent = { Text(subtitle, color = Color.White, fontSize = 14.sp) },
            leadingContent   = {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                    contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            },
            trailingContent  = {
                Icon(Icons.Default.OpenInNew, null, tint = Color(0xFF5555AA), modifier = Modifier.size(16.dp))
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
    if (!last) HorizontalDivider(color = Color(0xFF1A1730), modifier = Modifier.padding(start = 72.dp))
}
