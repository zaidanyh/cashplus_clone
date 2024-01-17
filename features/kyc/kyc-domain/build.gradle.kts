apply {
    from("$rootDir/library-build.gradle")
}

plugins {

}
dependencies {
    "implementation"(Kotlinx.coroutinesCore)
    "implementation"(project(mapOf("path" to Modules.libraryCore)))
}