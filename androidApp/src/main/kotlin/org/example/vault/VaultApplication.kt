package org.example.vault

import android.app.Application
import org.example.vault.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Koin должен стартовать до первого экрана: AuthViewModel берётся из него
        startKoin {
            androidContext(this@VaultApplication)
            modules(appModules)
        }
    }
}
