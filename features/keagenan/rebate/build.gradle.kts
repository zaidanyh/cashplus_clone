apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")

dependencies {
    "implementation"(Androidx.ktx_core)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.constrain_layout)
    // ViewModel
    "implementation"(AndroidKtx.viewModel_ktx)
    "implementation"(AndroidKtx.lifecycle_runtime)

    "implementation"(Koin.koin)

    "implementation"(Kotlinx.coroutinesAndroid)
    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))

    "implementation"(Androidx.material)
    //range date
    "implementation"(DateSublimePicker.depedency)

    // Networking
    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.retrofit_gson)
    "implementation"("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    "implementation"("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
}