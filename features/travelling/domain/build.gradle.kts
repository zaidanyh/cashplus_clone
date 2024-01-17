apply {
    from("$rootDir/library-build.gradle")
}
dependencies {
    "implementation"(Kotlinx.coroutinesCore)
    "implementation"(project(mapOf("path" to Modules.libraryCore)))
}