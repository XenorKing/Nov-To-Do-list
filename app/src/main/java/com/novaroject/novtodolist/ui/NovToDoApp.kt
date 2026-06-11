package com.novaroject.novtodolist.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.novaroject.novtodolist.auth.ui.LoginScreen
import com.novaroject.novtodolist.auth.ui.RegisterScreen
import com.novaroject.novtodolist.auth.ui.ResetPasswordScreen
import com.novaroject.novtodolist.profile.ui.AboutScreen
import com.novaroject.novtodolist.profile.ui.ProfileScreen
import com.novaroject.novtodolist.tasks.ui.AddTaskScreen
import com.novaroject.novtodolist.tasks.ui.AllTasksScreen
import com.novaroject.novtodolist.tasks.ui.TodayTasksScreen
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Register  : Screen("register")
    object ResetPass : Screen("reset_pass")
    object Today     : Screen("today")
    object AllTasks  : Screen("all_tasks")
    object Profile   : Screen("profile")
    object About     : Screen("about")
    object AddTask   : Screen("add_task")
}

@Composable
fun NovToDoApp() {
    val nav     = rememberNavController()
    val entry   by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route

    val startDest = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Today.route else Screen.Login.route

    val mainRoutes = setOf(Screen.Today.route, Screen.AllTasks.route)

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            if (current in mainRoutes) {
                CyberNavBar(
                    current    = current ?: "",
                    onToday    = {
                        nav.navigate(Screen.Today.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onAddTask  = { nav.navigate(Screen.AddTask.route) },
                    onAllTasks = {
                        nav.navigate(Screen.AllTasks.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    }
                )
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = startDest, modifier = Modifier.padding(pad)) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess     = { nav.navigate(Screen.Today.route) { popUpTo(0) { inclusive = true } } },
                    onNavigateRegister = { nav.navigate(Screen.Register.route) },
                    onNavigateReset    = { nav.navigate(Screen.ResetPass.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegistered = { nav.navigate(Screen.Today.route) { popUpTo(0) { inclusive = true } } },
                    onBack       = { nav.popBackStack() }
                )
            }
            composable(Screen.ResetPass.route) { ResetPasswordScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.Today.route) {
                TodayTasksScreen(
                    onAddTask     = { nav.navigate(Screen.AddTask.route) },
                    onOpenProfile = { nav.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.AllTasks.route) { AllTasksScreen(onAddTask = { nav.navigate(Screen.AddTask.route) }) }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = { nav.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                    onAbout  = { nav.navigate(Screen.About.route) },
                    onBack   = { nav.popBackStack() }
                )
            }
            composable(Screen.About.route) { AboutScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.AddTask.route) { AddTaskScreen(onBack = { nav.popBackStack() }) }
        }
    }
}

@Composable
private fun CyberNavBar(
    current: String,
    onToday: () -> Unit,
    onAddTask: () -> Unit,
    onAllTasks: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(270.dp)
                .height(66.dp)
                .shadow(
                    elevation  = 20.dp,
                    shape      = RoundedCornerShape(33.dp),
                    ambientColor = NeonPurple.copy(alpha = 0.5f),
                    spotColor    = NeonCyan.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(33.dp))
                .background(Color(0xFF0D0B20)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavPillItem(Icons.Default.Home, "Главная", current == Screen.Today.route, onToday)

                // Center + FAB
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(12.dp, CircleShape, ambientColor = NeonCyan.copy(0.8f), spotColor = NeonCyan.copy(0.8f))
                        .clip(CircleShape)
                        .background(NeonCyan),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onAddTask, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Default.Add, "Добавить", tint = Color.Black, modifier = Modifier.size(28.dp))
                    }
                }

                NavPillItem(Icons.Default.List, "Задачи", current == Screen.AllTasks.route, onAllTasks)
            }
        }
    }
}

@Composable
private fun NavPillItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(
        if (selected) NeonCyan else Color(0xFF5A5A8A),
        animationSpec = tween(200), label = "navColor"
    )
    IconButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, label, tint = color, modifier = Modifier.size(22.dp))
            Text(label, color = color, fontSize = 9.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        }
    }
}
