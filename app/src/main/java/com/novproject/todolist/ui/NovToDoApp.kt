package com.novproject.todolist.ui

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.novproject.todolist.auth.ui.LoginScreen
import com.novproject.todolist.auth.ui.RegisterScreen
import com.novproject.todolist.auth.ui.ResetPasswordScreen
import com.novproject.todolist.profile.ui.ProfileScreen
import com.novproject.todolist.profile.ui.AboutScreen
import com.novproject.todolist.tasks.ui.TodayTasksScreen
import com.novproject.todolist.tasks.ui.AllTasksScreen
import com.novproject.todolist.tasks.ui.AddTaskScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ResetPassword : Screen("reset_password")
    object Today : Screen("today")
    object AllTasks : Screen("all_tasks")
    object Profile : Screen("profile")
    object About : Screen("about")
    object AddTask : Screen("add_task")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun NovToDoApp() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null) Screen.Today.route else Screen.Login.route

    val bottomItems = listOf(
        BottomNavItem(Screen.Today, "Главная", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.AllTasks, "Все задачи", Icons.Filled.List, Icons.Outlined.List),
        BottomNavItem(Screen.Profile, "Профиль", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Today.route, Screen.AllTasks.route, Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        val selected = navBackStackEntry?.destination?.hierarchy
                            ?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Today.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateReset = { navController.navigate(Screen.ResetPassword.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Screen.Today.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Today.route) {
                TodayTasksScreen(
                    onAddTask = { navController.navigate(Screen.AddTask.route) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.AllTasks.route) {
                AllTasksScreen(
                    onAddTask = { navController.navigate(Screen.AddTask.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onAbout = { navController.navigate(Screen.About.route) }
                )
            }
            composable(Screen.About.route) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.AddTask.route) {
                AddTaskScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
