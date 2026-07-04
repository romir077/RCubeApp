package com.rcube.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.EventType
import com.rcube.app.data.model.Mode
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.feature.account.AccountScreen
import com.rcube.app.feature.account.NotificationsScreen
import com.rcube.app.feature.auth.ModeIntroScreen
import com.rcube.app.feature.auth.OtpScreen
import com.rcube.app.feature.auth.PhoneScreen
import com.rcube.app.feature.creator.CreatorProfilesScreen
import com.rcube.app.feature.creator.CreatorRequestsScreen
import com.rcube.app.feature.creator.EarningsScreen
import com.rcube.app.feature.creator.ProfileEditorScreen
import com.rcube.app.feature.creator.RequestDetailScreen
import com.rcube.app.feature.creator.ServiceEditorScreen
import com.rcube.app.feature.creator.StartEventScreen
import com.rcube.app.feature.organizer.BookingFormScreen
import com.rcube.app.feature.organizer.CreatorPublicScreen
import com.rcube.app.feature.organizer.DiscoverScreen
import com.rcube.app.feature.organizer.EventOtpScreen
import com.rcube.app.feature.organizer.MyBookingsScreen
import com.rcube.app.feature.organizer.OrganizerBookingDetailScreen
import com.rcube.app.feature.organizer.PaymentScreen
import com.rcube.app.feature.organizer.SearchResultsScreen

private data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any,
    val isSelected: (NavDestination?) -> Boolean,
)

private fun homeRouteFor(mode: Mode): Any =
    if (mode == Mode.CREATOR) CreatorProfilesRoute else DiscoverRoute

@Composable
fun RcubeApp() {
    val repo = LocalAppContainer.current.repository
    val session by repo.session.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination

    val tabs = if (session.mode == Mode.CREATOR) creatorTabs else organizerTabs
    val onTab = tabs.any { it.isSelected(currentDest) }
    val showBar = session.loggedIn && !session.needsModeSelection && onTab

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        val selected = tab.isSelected(currentDest)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.switchTab(tab.route, session.mode) },
                            icon = {
                                Icon(
                                    if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = PhoneRoute) {

            // ---- Auth ----
            composable<PhoneRoute> {
                PhoneScreen(
                    initialPhone = session.phone.removePrefix("+91 "),
                    onContinue = { phone ->
                        repo.startLogin(phone)
                        navController.navigate(OtpRoute)
                    },
                )
            }
            composable<OtpRoute> {
                OtpScreen(
                    phone = session.phone,
                    onBack = { navController.popBackStack() },
                    onVerified = {
                        repo.verifyOtpAndSignIn()
                        navController.navigate(ModeIntroRoute) {
                            popUpTo(PhoneRoute) { inclusive = true }
                        }
                    },
                )
            }
            composable<ModeIntroRoute> {
                ModeIntroScreen(onModeChosen = { mode ->
                    repo.selectInitialMode(mode)
                    navController.navigateHome(homeRouteFor(mode))
                })
            }

            // ---- Creator tabs ----
            composable<CreatorProfilesRoute> {
                CreatorProfilesScreen(
                    onOpenProfile = { navController.navigate(ProfileEditorRoute(it)) },
                    onCreateProfile = { navController.navigate(ProfileEditorRoute(null)) },
                    onOpenNotifications = { navController.navigate(NotificationsRoute) },
                    contentPadding = innerPadding,
                )
            }
            composable<CreatorRequestsRoute> {
                CreatorRequestsScreen(
                    onOpenRequest = { navController.navigate(RequestDetailRoute(it)) },
                    onOpenNotifications = { navController.navigate(NotificationsRoute) },
                    contentPadding = innerPadding,
                )
            }
            composable<EarningsRoute> {
                EarningsScreen(contentPadding = innerPadding)
            }

            // ---- Organizer tabs ----
            composable<DiscoverRoute> {
                DiscoverScreen(
                    onSearch = { c, e, r ->
                        navController.navigate(SearchResultsRoute(c.name, e.name, r))
                    },
                    onOpenNotifications = { navController.navigate(NotificationsRoute) },
                    contentPadding = innerPadding,
                )
            }
            composable<MyBookingsRoute> {
                MyBookingsScreen(
                    onOpenBooking = { navController.navigate(OrganizerBookingDetailRoute(it)) },
                    onDiscover = { navController.switchTab(DiscoverRoute, session.mode) },
                    onOpenNotifications = { navController.navigate(NotificationsRoute) },
                    contentPadding = innerPadding,
                )
            }

            // ---- Shared ----
            composable<AccountRoute> {
                AccountScreen(
                    onSwitchMode = {
                        repo.switchMode()
                        navController.navigateHome(homeRouteFor(repo.session.value.mode))
                    },
                    onOpenNotifications = { navController.navigate(NotificationsRoute) },
                    onLogout = {
                        repo.logout()
                        navController.navigateHome(PhoneRoute)
                    },
                    contentPadding = innerPadding,
                )
            }
            composable<NotificationsRoute> {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }

            // ---- Creator details ----
            composable<ProfileEditorRoute> { entry ->
                val route = entry.toRoute<ProfileEditorRoute>()
                ProfileEditorScreen(
                    profileId = route.profileId,
                    onBack = { navController.popBackStack() },
                    onAddService = { navController.navigate(ServiceEditorRoute(it)) },
                    onPreview = { navController.navigate(CreatorPublicRoute(it, null)) },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable<ServiceEditorRoute> { entry ->
                val route = entry.toRoute<ServiceEditorRoute>()
                ServiceEditorScreen(
                    profileId = route.profileId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable<RequestDetailRoute> { entry ->
                val route = entry.toRoute<RequestDetailRoute>()
                RequestDetailScreen(
                    bookingId = route.bookingId,
                    onBack = { navController.popBackStack() },
                    onStartEvent = { navController.navigate(StartEventRoute(it)) },
                )
            }
            composable<StartEventRoute> { entry ->
                val route = entry.toRoute<StartEventRoute>()
                StartEventScreen(
                    bookingId = route.bookingId,
                    onBack = { navController.popBackStack() },
                    onStarted = { navController.popBackStack() },
                )
            }

            // ---- Organizer details ----
            composable<SearchResultsRoute> { entry ->
                val route = entry.toRoute<SearchResultsRoute>()
                SearchResultsScreen(
                    category = CreatorCategory.valueOf(route.category),
                    eventType = EventType.valueOf(route.eventType),
                    radiusKm = route.radiusKm,
                    onBack = { navController.popBackStack() },
                    onOpenProfile = {
                        navController.navigate(CreatorPublicRoute(it, route.eventType))
                    },
                )
            }
            composable<CreatorPublicRoute> { entry ->
                val route = entry.toRoute<CreatorPublicRoute>()
                val eventType = route.eventType?.let { EventType.valueOf(it) }
                CreatorPublicScreen(
                    profileId = route.profileId,
                    eventType = eventType,
                    bookable = eventType != null,
                    onBack = { navController.popBackStack() },
                    onBook = { serviceId ->
                        navController.navigate(
                            BookingFormRoute(route.profileId, serviceId, route.eventType ?: EventType.OTHER.name),
                        )
                    },
                )
            }
            composable<BookingFormRoute> { entry ->
                val route = entry.toRoute<BookingFormRoute>()
                BookingFormScreen(
                    profileId = route.profileId,
                    serviceId = route.serviceId,
                    initialEventType = EventType.valueOf(route.eventType),
                    onBack = { navController.popBackStack() },
                    onSubmitted = { bookingId ->
                        navController.navigate(OrganizerBookingDetailRoute(bookingId))
                    },
                )
            }
            composable<OrganizerBookingDetailRoute> { entry ->
                val route = entry.toRoute<OrganizerBookingDetailRoute>()
                OrganizerBookingDetailScreen(
                    bookingId = route.bookingId,
                    onBack = { navController.popBackStack() },
                    onPay = { navController.navigate(PaymentRoute(it)) },
                    onShowEventCode = { navController.navigate(EventOtpRoute(it)) },
                )
            }
            composable<PaymentRoute> { entry ->
                val route = entry.toRoute<PaymentRoute>()
                PaymentScreen(
                    bookingId = route.bookingId,
                    onBack = { navController.popBackStack() },
                    onPaid = { navController.popBackStack() },
                )
            }
            composable<EventOtpRoute> { entry ->
                val route = entry.toRoute<EventOtpRoute>()
                EventOtpScreen(
                    bookingId = route.bookingId,
                    onBack = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavController.navigateHome(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavController.switchTab(route: Any, mode: Mode) {
    navigate(route) {
        popUpTo(homeRouteFor(mode)) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private val creatorTabs = listOf(
    TabItem("Profiles", Icons.Filled.Person, Icons.Outlined.Person, CreatorProfilesRoute) {
        it?.hasRoute(CreatorProfilesRoute::class) == true
    },
    TabItem("Requests", Icons.Filled.Inbox, Icons.Outlined.Inbox, CreatorRequestsRoute) {
        it?.hasRoute(CreatorRequestsRoute::class) == true
    },
    TabItem("Earnings", Icons.Filled.Payments, Icons.Outlined.Payments, EarningsRoute) {
        it?.hasRoute(EarningsRoute::class) == true
    },
    TabItem("Account", Icons.Filled.Person, Icons.Outlined.Person, AccountRoute) {
        it?.hasRoute(AccountRoute::class) == true
    },
)

private val organizerTabs = listOf(
    TabItem("Discover", Icons.Filled.Search, Icons.Outlined.Search, DiscoverRoute) {
        it?.hasRoute(DiscoverRoute::class) == true
    },
    TabItem("Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, MyBookingsRoute) {
        it?.hasRoute(MyBookingsRoute::class) == true
    },
    TabItem("Account", Icons.Filled.Person, Icons.Outlined.Person, AccountRoute) {
        it?.hasRoute(AccountRoute::class) == true
    },
)
