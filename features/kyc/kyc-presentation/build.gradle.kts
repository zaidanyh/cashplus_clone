apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")
apply(plugin = "androidx.navigation.safeargs.kotlin")

dependencies {
    "implementation"(project(Modules.kycDomain))

    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)

    "implementation"(Androidx.material)
    "implementation"(Androidx.legacy_support)
    "implementation"(AndroidKtx.viewModel_ktx)
    "implementation"(AndroidKtx.fragment_ktx)
    "implementation"(AndroidKtx.lifecycle_runtime)

    // camera
    "implementation"(Androidx.cameraX)
    "implementation"(Androidx.cameraLifecycle)
    "implementation"(Androidx.cameraView)

    "implementation"(NavigationComponent.fragmentKtx)
    "implementation"(NavigationComponent.navigationUI)

    "implementation"(Koin.koin)

    "implementation"(Kotlinx.coroutinesAndroid)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"("com.github.dhaval2404:imagepicker:2.1")
}