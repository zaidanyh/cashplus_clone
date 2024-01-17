apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")

dependencies {
    "implementation"(project(Modules.cashGoldDomain))

    "implementation"(project(Modules.cashGoldUIBuy))
    "implementation"(project(Modules.cashGoldUIAddress))
    "implementation"(project(Modules.cashGoldUpdateProfile))
    "implementation"(project(Modules.cashGoldUICore))
    "implementation"(project(Modules.libraryCore))

    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)

    "implementation"(Koin.koin)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"(Androidx.material)
    "implementation"(Androidx.legacy_support)


    // Navigation component Kotlin
    "implementation"(NavigationComponent.fragmentKtx)
    "implementation"(NavigationComponent.navigationUI)
}