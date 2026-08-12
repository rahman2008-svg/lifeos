package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AlarmRingDialog
import com.example.ui.components.GlobalSearchModal
import com.example.ui.components.QuickActionBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.LifeOSTheme
import com.example.viewmodel.LifeOSViewModel
import kotlinx.coroutines.launch

data class NavigationModuleItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOSApp(viewModel: LifeOSViewModel) {
    val prefs by viewModel.preferences.collectAsState()
    val ringingAlarm by viewModel.ringingAlarm.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val unreadNotifCount = notifications.count { !it.isRead }

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showQuickActionSheet by remember { mutableStateOf(false) }
    var showSearchModal by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "dashboard"

    LifeOSTheme(themeMode = prefs.themeMode) {
        if (!prefs.isOnboardingCompleted) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    // Navigate to dashboard after onboarding
                }
            )
        } else {
            // Ringing Alarm Dialog overlay if active
            ringingAlarm?.let { alarm ->
                AlarmRingDialog(
                    alarm = alarm,
                    onSnooze = { viewModel.snoozeRingingAlarm() },
                    onDismiss = { viewModel.dismissRingingAlarm() }
                )
            }

            // Global Search Modal overlay
            if (showSearchModal) {
                GlobalSearchModal(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    results = searchResults,
                    onSelectResult = { route ->
                        navController.navigate(route)
                    },
                    onDismiss = { showSearchModal = false }
                )
            }

            // Quick Action BottomSheet overlay
            if (showQuickActionSheet) {
                QuickActionBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { showQuickActionSheet = false }
                )
            }

            val navModules = listOf(
                NavigationModuleItem("dashboard", "Dashboard", Icons.Default.Dashboard),
                NavigationModuleItem("today", "Daily Planner", Icons.Default.Today),
                NavigationModuleItem("week", "Weekly Planner", Icons.Default.CalendarViewWeek),
                NavigationModuleItem("habits", "Habit Tracker", Icons.Default.Loop),
                NavigationModuleItem("goals", "Goals", Icons.Default.Flag),
                NavigationModuleItem("study", "Study OS", Icons.Default.Book),
                NavigationModuleItem("focus", "Focus Mode", Icons.Default.Timer),
                NavigationModuleItem("notes", "Notes", Icons.Default.Description),
                NavigationModuleItem("books", "Book Library", Icons.Default.MenuBook),
                NavigationModuleItem("ideas", "Idea Vault", Icons.Default.Lightbulb),
                NavigationModuleItem("finance", "Finance", Icons.Default.AttachMoney),
                NavigationModuleItem("notifications", "Notifications", Icons.Default.Notifications),
                NavigationModuleItem("alarms", "Alarms", Icons.Default.Alarm),
                NavigationModuleItem("routines", "Routines", Icons.Default.Schedule),
                NavigationModuleItem("timeline", "Life Timeline", Icons.Default.Timeline),
                NavigationModuleItem("life-map", "Life Map", Icons.Default.AccountTree),
                NavigationModuleItem("progress", "Progress & Score", Icons.Default.Insights),
                NavigationModuleItem("settings", "Settings", Icons.Default.Settings),
                NavigationModuleItem("about", "About Developer", Icons.Default.Info)
            )

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Column(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    text = "LIFEOS",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }

                            val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 4.dp,
                                    bottom = 28.dp + navBarPadding
                                ),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(navModules) { mod ->
                                    NavigationDrawerItem(
                                        icon = { Icon(mod.icon, contentDescription = null) },
                                        label = { Text(mod.title) },
                                        selected = currentRoute == mod.route,
                                        onClick = {
                                            navController.navigate(mod.route) {
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.testTag("drawer_item_${mod.route}")
                                    )
                                }
                            }
                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = navModules.find { it.route == currentRoute }?.title ?: "LifeOS",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("open_drawer_button")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { showSearchModal = true },
                                    modifier = Modifier.testTag("open_search_button")
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Global Search")
                                }

                                IconButton(
                                    onClick = { navController.navigate("notifications") },
                                    modifier = Modifier.testTag("top_notif_button")
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadNotifCount > 0) {
                                                Badge { Text("$unreadNotifCount") }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            val bottomNavItems = listOf(
                                NavigationModuleItem("dashboard", "Home", Icons.Default.Dashboard),
                                NavigationModuleItem("today", "Planner", Icons.Default.Today),
                                NavigationModuleItem("habits", "Habits", Icons.Default.Loop),
                                NavigationModuleItem("focus", "Focus", Icons.Default.Timer),
                                NavigationModuleItem("alarms", "Alarms", Icons.Default.Alarm)
                            )

                            bottomNavItems.forEach { item ->
                                NavigationBarItem(
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo("dashboard") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = { Text(item.title) },
                                    modifier = Modifier.testTag("nav_item_${item.route}")
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showQuickActionSheet = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.testTag("global_fab_add")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Quick Add")
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard"
                        ) {
                            composable("dashboard") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigate = { route -> navController.navigate(route) },
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("today") {
                                TodayPlannerScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("week") { WeekPlannerScreen(viewModel) }
                            composable("habits") {
                                HabitsScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("goals") {
                                GoalsScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("study") {
                                StudyScreen(
                                    viewModel = viewModel,
                                    onNavigateToFocus = { navController.navigate("focus") }
                                )
                            }
                            composable("focus") { FocusScreen(viewModel) }
                            composable("notes") {
                                NotesScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("books") {
                                BooksScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("ideas") {
                                IdeasScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("finance") {
                                FinanceScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("notifications") { NotificationsScreen(viewModel) }
                            composable("alarms") {
                                AlarmsScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("routines") {
                                RoutinesScreen(
                                    viewModel = viewModel,
                                    onOpenQuickAction = { showQuickActionSheet = true }
                                )
                            }
                            composable("timeline") { TimelineScreen(viewModel) }
                            composable("life-map") { LifeMapScreen(viewModel) }
                            composable("progress") { ProgressScreen(viewModel) }
                            composable("settings") {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateToAbout = { navController.navigate("about") }
                                )
                            }
                            composable("about") { AboutScreen() }
                        }
                    }
                }
            }
        }
    }
}
