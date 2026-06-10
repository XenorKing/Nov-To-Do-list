package com.novaroject.novtodolist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

sealed class Screen(val route: String) {
    object Login        : Screen("login")
    object Register     : Screen("register")
    object ResetPass    : Screen("reset_pass")
    object Today        : Screen("today")
    object AllTasks     : Screen("all_tasks")
    object Profile      : Screen("profile")
    object About        : Screen("about")
    object AddTask      : Screen("add_task")
}

@Composable
fun NovToDoApp() {
    val nav     = rememberNavController()
    val entry   by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route

    val startDest = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Today.route else Screen.Login.route

    val mainRoutes = setOf(Screen.Today.route, Screen.AllTasks.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            if (current in mainRoutes) {
                NavigationBar {
                    listOf(
                        Triple(Screen.Today,    "Главная",    Icons.Default.Home    to Icons.Outlined.Home),
                        Triple(Screen.AllTasks, "Все задачи", Icons.Default.List    to Icons.Outlined.List),
                        Triple(Screen.Profile,  "Профиль",    Icons.Default.CheckCircle to Icons.Outlined.CheckCircle),
                    ).forEach { (screen, label, icons) ->
                        val selected = current == screen.route
                        NavigationBarItem(
                            icon  = { Icon(if (selected) icons.first else icons.second, label) },
                            label = { Text(label) },
                            selected  = selected,
                            onClick   = {
                                nav.navigate(screen.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true; restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = startDest, modifier = Modifier.padding(pad)) {
            composable(Screen.Login.route)     { LoginScreen(
                onLoginSuccess     = { nav.navigate(Screen.Today.route) { popUpTo(0) { inclusive = true } } },
                onNavigateRegister = { nav.navigate(Screen.Register.route) },
                onNavigateReset    = { nav.navigate(Screen.ResetPass.route) }
            ) }
            composable(Screen.Register.route)  { RegisterScreen(
                onRegistered = { nav.navigate(Screen.Today.route) { popUpTo(0) { inclusive = true } } },
                onBack       = { nav.popBackStack() }
            ) }
            composable(Screen.ResetPass.route) { ResetPasswordScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.Today.route)     { TodayTasksScreen(
                onAddTask    = { nav.navigate(Screen.AddTask.route) },
                onOpenProfile= { nav.navigate(Screen.Profile.route) }
            ) }
            composable(Screen.AllTasks.route)  { AllTasksScreen(onAddTask = { nav.navigate(Screen.AddTask.route) }) }
            composable(Screen.Profile.route)   { ProfileScreen(
                onLogout = { nav.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                onAbout  = { nav.navigate(Screen.About.route) }
            ) }
            composable(Screen.About.route)     { AboutScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.AddTask.route)   { AddTaskScreen(onBack = { nav.popBackStack() }) }
        }
    }
}
