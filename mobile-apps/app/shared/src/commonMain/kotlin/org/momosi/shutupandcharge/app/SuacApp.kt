package org.momosi.shutupandcharge.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.app.resources.Res
import org.momosi.shutupandcharge.app.resources.tab_admin
import org.momosi.shutupandcharge.app.resources.tab_dashboard
import org.momosi.shutupandcharge.app.resources.tab_profile
import org.momosi.shutupandcharge.app.resources.tab_transactions
import org.momosi.shutupandcharge.core.common.crashreporting.CrashReporting
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.navigation.Navigator
import org.momosi.shutupandcharge.core.navigation.rememberNavigationState
import org.momosi.shutupandcharge.core.navigation.toEntries
import org.momosi.shutupandcharge.core.uilibrary.components.ErrorSnackbarHost
import org.momosi.shutupandcharge.core.uilibrary.components.NavigationBar
import org.momosi.shutupandcharge.core.uilibrary.components.NavigationBarItem
import org.momosi.shutupandcharge.core.uilibrary.components.rememberErrorSnackbarHostState
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.markTestTagsAsResourceId
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.admin.impl.navigation.adminEntries
import org.momosi.shutupandcharge.feature.dashboard.api.DashboardNavKey
import org.momosi.shutupandcharge.feature.dashboard.impl.navigation.dashboardEntries
import org.momosi.shutupandcharge.feature.profile.impl.navigation.profileEntries
import org.momosi.shutupandcharge.feature.transactions.impl.navigation.transactionsEntries
import org.momosi.shutupandcharge.feature.welcome.impl.navigation.welcomeEntries

@Composable
fun SuacApp(modifier: Modifier = Modifier, viewModel: SuacViewModel = koinViewModel()) {
    val navigationState = rememberNavigationState(
        startKey = remember { viewModel.access.value.startKey() },
        savedStateConfiguration = appSavedStateConfiguration,
    )
    val navigator = remember { Navigator(navigationState) }
    val errorSnackbarHostState = rememberErrorSnackbarHostState()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val tabs = navigationBarItems(isAdmin)

    LaunchedEffect(viewModel, navigator) {
        viewModel.access.collect { access ->
            access.redirectFrom(navigationState.currentKey)?.let(navigator::navigateToRoot)
        }
    }

    LaunchedEffect(isAdmin) {
        if (!isAdmin && navigationState.currentKey == MainTab.Admin.navKey) navigator.navigateToRoot(DashboardNavKey)
    }

    LaunchedEffect(navigationState.currentKey) {
        Logger.i("Nav", "-> ${navigationState.currentKey}")
        CrashReporting.setCustomKey("screen", navigationState.currentKey.toString())
    }

    LaunchedEffect(Unit) {
        viewModel.errors.collect { error -> errorSnackbarHostState.showError(error) }
    }

    SuacTheme {
        Scaffold(
            modifier = modifier.fillMaxSize().imePadding().markTestTagsAsResourceId(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { ErrorSnackbarHost(errorSnackbarHostState) },
            bottomBar = {
                NavigationBar(
                    items = tabs,
                    selectedKey = navigationState.currentKey,
                    isVisible = navigationState.currentKey.isMainTab(),
                    onSelectKey = { key -> navigator.navigate(root = DashboardNavKey, destination = key) },
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
                    entries = navigationState.toEntries(
                        entryProvider {
                            welcomeEntries()
                            dashboardEntries(navigator)
                            transactionsEntries()
                            adminEntries()
                            profileEntries()
                        },
                    ),
                    onBack = navigator::goBack,
                )
                if (viewModel.isDemoMode) DemoModeBadge(modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
private fun navigationBarItems(isAdmin: Boolean): ImmutableList<NavigationBarItem> {
    val dashboard = stringResource(Res.string.tab_dashboard)
    val transactions = stringResource(Res.string.tab_transactions)
    val admin = stringResource(Res.string.tab_admin)
    val profile = stringResource(Res.string.tab_profile)
    return remember(isAdmin, dashboard, transactions, admin, profile) {
        mainTabsFor(isAdmin).map { tab ->
            when (tab) {
                MainTab.Dashboard -> NavigationBarItem(tab.navKey, SuacIcons.Dashboard, SuacIcons.DashboardOutlined, dashboard)
                MainTab.Transactions -> NavigationBarItem(tab.navKey, SuacIcons.Transactions, SuacIcons.Transactions, transactions)
                MainTab.Admin -> NavigationBarItem(tab.navKey, SuacIcons.Admin, SuacIcons.AdminOutlined, admin)
                MainTab.Profile -> NavigationBarItem(tab.navKey, SuacIcons.AccountCircle, SuacIcons.AccountCircleOutlined, profile)
            }
        }.toImmutableList()
    }
}
