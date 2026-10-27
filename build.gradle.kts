plugins {
    // Плагины объявляем здесь с apply false, чтобы они загружались один раз
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.detekt) apply false
}

// Detekt подключаем ко всем модулям сразу, чтобы не дублировать настройку в каждом build-файле
subprojects {
    apply(plugin = "dev.detekt")

    extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
        // Берём стандартные правила detekt и поверх них применяем наши переопределения
        buildUponDefaultConfig = true
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    }

    tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
        // Сгенерированный код (Res.kt от Compose, запросы SQLDelight) не наш, его проверять не нужно.
        // Фильтруем по полному пути: шаблоны вроде **/build/** смотрят только на путь внутри
        // каталога исходников, а каталог build лежит выше и шаблоном не ловится
        exclude { it.file.invariantSeparatorsPath.contains("/build/") }
    }
}

// Единая команда проверки: .\gradlew.bat detektAll --continue
// В KMP-модулях общая задача detekt ничего не анализирует, работают только задачи по source set'ам.
// В androidApp (не KMP) работает обычная detekt.
tasks.register("detektAll") {
    group = "verification"
    description = "Runs detekt for every source set of every module"
    dependsOn(
        provider {
            subprojects.flatMap { sub ->
                sub.tasks.matching { task ->
                    val name = task.name
                    val isSourceSetTask = name.startsWith("detekt") &&
                            name.endsWith("SourceSet") &&
                            !name.startsWith("detektBaseline")
                    isSourceSetTask || (sub.path == ":androidApp" && name == "detekt")
                }
            }
        },
    )
}
