apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")

dependencies {

    "implementation"(project(Modules.cashGoldUICore))
    "implementation"(project(Modules.cashGoldDomain))

    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)
    // ViewModel
    "implementation"(AndroidKtx.viewModel_ktx)
    "implementation"(AndroidKtx.fragment_ktx)
    "implementation"(AndroidKtx.lifecycle_runtime)

    "implementation"(Koin.koin)
    "implementation"(Kotlinx.coroutinesAndroid)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"(Androidx.material)
    "implementation"(Androidx.legacy_support)

    "implementation"(DateSublimePicker.depedency)
}