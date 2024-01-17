apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")
apply(plugin = "kotlin-parcelize")

dependencies {

    "implementation"(project(Modules.cashGoldUICore))
    "implementation"(project(Modules.cashGoldDomain))

    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)
    "implementation"(AndroidKtx.lifecycle_runtime)
    // ViewModel
    "implementation"(AndroidKtx.viewModel_ktx)

    "implementation"(Koin.koin)
    "implementation"(Kotlinx.coroutinesAndroid)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"(Androidx.material)
    "implementation"(Androidx.legacy_support)
}