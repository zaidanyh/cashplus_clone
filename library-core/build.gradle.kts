apply {
    from("$rootDir/library-build.gradle")
}

plugins {

}
dependencies {
//    "implementation"(Kotlinx.coroutinesCore)
    "implementation"(Retrofit.retrofit_gson)
}