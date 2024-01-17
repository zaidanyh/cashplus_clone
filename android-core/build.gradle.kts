apply {
    from("$rootDir/android-library-build.gradle")
}
apply(plugin = "kotlin-android")
apply(plugin = "kotlin-parcelize")

dependencies {
    "implementation"(Retrofit.retrofit_gson)
    //progress button
    "api"("com.github.razir.progressbutton:progressbutton:2.1.0")
    "implementation"(AndroidKtx.fragment_ktx)
    "implementation"(Androidx.app_compact)
    "implementation"(Androidx.material)
    "implementation"(Androidx.constrain_layout)
    "implementation"(Androidx.recyclerview)

    //android otp
    "implementation"(OtpView.depedencies)
    "implementation"("androidx.legacy:legacy-support-v4:1.0.0")
    "implementation"("androidx.cardview:cardview:1.0.0")

    //range date
    "implementation"(DateSublimePicker.depedency)

    // QR Code
    "implementation"(BarcodeScanner.zxing)

    //Image Picker
    "implementation"("com.github.dhaval2404:imagepicker:2.1")

    "implementation"(Glide.glide)

    //paging
    "implementation"(Androidx.pagination)
}