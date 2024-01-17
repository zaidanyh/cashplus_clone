apply {
    from("$rootDir/android-library-build.gradle")
}

dependencies {
    "implementation"(project(Modules.travelDomain))

    // Networking
    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.retrofit_gson)

    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))
}