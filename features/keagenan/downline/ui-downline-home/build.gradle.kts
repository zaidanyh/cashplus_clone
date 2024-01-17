apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")
apply(plugin = "kotlin-parcelize")
apply(plugin = "androidx.navigation.safeargs.kotlin")

dependencies {
    "implementation"(project(Modules.keagenanDomain))

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
    "implementation"(Glide.glide)
    "implementation"(Androidx.pagination)

    //range date
    "implementation"(DateSublimePicker.depedency)
    "implementation"(NavigationComponent.fragmentKtx)

    // QR Code
    "implementation"(BarcodeScanner.zxing)

    // Gson
    "implementation"(Retrofit.retrofit_gson)

    // Recyclerview indicator
    "implementation"("com.github.martinstamenkovski:ARIndicatorView:2.0.0")

    // shimmer loading
    "implementation"("com.facebook.shimmer:shimmer:0.5.0@aar")
}