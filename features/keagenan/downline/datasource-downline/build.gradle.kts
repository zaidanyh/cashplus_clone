apply {
    from("$rootDir/android-library-build.gradle")
}

plugins {

}
dependencies {
    "implementation"(project(Modules.keagenanDomain))

    // Networking
    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.retrofit_gson)

    "implementation"(project(mapOf("path" to Modules.androidCore)))
    "implementation"(project(mapOf("path" to Modules.libraryCore)))
}