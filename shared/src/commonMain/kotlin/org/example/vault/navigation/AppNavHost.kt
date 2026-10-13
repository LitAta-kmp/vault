package org.example.vault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.vault.feature.auth.AuthRoute
import org.example.vault.feature.vault.VaultRoute

/** Единственное место, которое знает, как экраны разных фич связаны между собой. */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthDestination,
        modifier = modifier,
    ) {
        composable<AuthDestination> {
            AuthRoute(
                onNavigateToVault = {
                    navController.navigate(VaultDestination) {
                        // Убираем PIN-экран из стека: «назад» из хранилища не должно возвращать на ввод PIN
                        popUpTo<AuthDestination> { inclusive = true }
                    }
                },
            )
        }
        composable<VaultDestination> {
            VaultRoute()
        }
    }
}
