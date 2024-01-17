apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")
apply(plugin = "kotlin-parcelize")
apply(plugin = "androidx.navigation.safeargs.kotlin")

dependencies {
    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)

    "implementation"(AndroidKtx.viewModel_ktx)
    "implementation"(AndroidKtx.fragment_ktx)
    "implementation"(AndroidKtx.lifecycle_runtime)

    "implementation"(NavigationComponent.fragmentKtx)
    "implementation"(NavigationComponent.navigationUI)

    "implementation"(Koin.koin)

    "implementation"(Kotlinx.coroutinesAndroid)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"(Androidx.material)
    "implementation"(Androidx.legacy_support)

    // Networking
    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.retrofit_gson)
    "implementation"("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    "implementation"("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    "implementation"(BarcodeScanner.zxing)
    "implementation"(OtpView.depedencies)

    // shimmer loading
    "implementation"("com.facebook.shimmer:shimmer:0.5.0@aar")
}