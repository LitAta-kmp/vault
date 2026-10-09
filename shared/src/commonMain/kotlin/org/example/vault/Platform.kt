package org.example.vault

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform