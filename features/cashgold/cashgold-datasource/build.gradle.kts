apply {
    from("$rootDir/android-library-build.gradle")
}

plugins {

}
dependencies {
    // Networking
    "implementation"(project(Modules.cashGoldDomain))
    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.retrofit_gson)

    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))
}