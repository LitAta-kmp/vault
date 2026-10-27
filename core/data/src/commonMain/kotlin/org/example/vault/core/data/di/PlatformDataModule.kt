package org.example.vault.core.data.di

import org.koin.core.module.Module

/** Платформенные зависимости (на Android: фабрика зашифрованной базы с Context). */
internal expect fun platformDataModule(): Module
