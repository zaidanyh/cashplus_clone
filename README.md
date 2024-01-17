# Cashplus
Cashplus is a charging and payment service that makes it easy for buyers to get the products they want. This repository is the source code for the CashPlus Android app.

----

## **Project Overview**
In this source code has implemented the concept of clean architecture (rudimentary), Modular, MVVM design patterns, Koin dependencies injection, and coroutines flow in the features module.

----

## **Project Structure**

- **android-core** &rarr; Module for function collections, object classes, display classes e.g. DialogFragment accessible across modules.
- **app** &rarr; Main module present in this source code
- **buildSrc** &rarr; Module for a collection of libraries used for all modules. Except for the Android object class used for APK and AAB build purposes.
- **features** &rarr; Module for CashPlus features.
  - **cash-topup** &rarr; Topup feature module.
  - **cash-transfer** &rarr; Cash transfer feature module.
  - **cashgold** &rarr; Gold trading feature module.
  - **keagenan**
    - **downline** &rarr; Downline feature module.
      - **datasource-downline**
      - **domain-downline**
      - **ui-downline-home**
      - **ui-downline-mutasi-summary-detail**
      - **ui-downline-nearby-agent**
      - **ui-downline-register**
      - **ui-downline-transfersaldo**
    - **rebate** &rarr; Rebate feature module.
  - **kyc** &rarr; E-KYC feature module.
    - **kyc-datasource**
    - **kyc-domain**
    - **kyc-presentation**
  - **nobu** &rarr; QRIS feature module. Nobu's is a third party. Possibility of replacing third parties.
  - **recapitulation** &rarr; Recap of transactions and deposits feature module.
  - **travelling** &rarr; Vacation features module.
    - **datasource**
    - **domain**
    - **flight**
    - **hotel**
    - **ship**
    - **train**
- **library-core** &rarr; This module is the same as the Android-Core module.
- **Gradle Script**
&nbsp;

**Additional Information**

Not all modules contain sub-modules DataSource, Domain, and Presentation. For your information, *datasource module* contains Data Transfer Object (Dto) classes, API Service for the features. *Domain module* containing the *Usecase* class, a class of object used for presentation (view). *Presentation* contains views such as Activity, Fragment and others.

----

## Development
Pastikan kamu sudah memahami konsep *modular* dengan *clean-architecture* agar melanjutkan pengembangan aplikasi Cashplus lebih mudah.

***Branch***

[core](https://github.com/cashplus-id/cashplus-android/tree/core) &rarr; *branch* utama yang harus sudah stabil. \
[fixbug](https://github.com/cashplus-id/cashplus-android/tree/fixbug) &rarr; *branch* yang digunakan untuk improve code, fixing bug, dll. Jika sudah selesai dan sudah terupload ke playstore, silahkan merge ke *branch* [core](https://github.com/cashplus-id/cashplus-android/tree/core).\
[recap](https://github.com/cashplus-id/cashplus-android/tree/recap) &rarr; ini adalah *branch* baru. Karena fitur yang dikerjakan adalah [recapitulation](https://github.com/cashplus-id/cashplus-android/tree/recap/features/recapitulation). Jika sudah selesai dan sudah terupload ke playstore, silahkan merge ke *branch* [core](https://github.com/cashplus-id/cashplus-android/tree/core) dan [fixbug](https://github.com/cashplus-id/cashplus-android/tree/fixbug). Dan buat branch baru ketika ada fitur baru yang ingin dikembangkan.

**API Documentation**

[API Cashplus](https://documenter.getpostman.com/view/7056579/SWE6ZHSQ?version=latest#47528873-1c25-4b4b-974b-c6f0ea003ef9)

**Figma Design**

[Figma Cashplus](https://www.figma.com/file/kvzL87IL2I1k1bVK9t5uT5/Cashplus-Design-v2?type=design&node-id=19507-80599&mode=design&t=jE5YMwnZJ8LPDs9R-0) &rarr; Jika tidak dapat diakses, hubungi pihak terkait.

**Firebase**

[Firebase Cashplus](https://console.firebase.google.com/u/0/project/cashplus-v1/overview?hl=id) &rarr; Jika tidak dapat diakses, hubungi pihak terkait.

Silahkan buka gambar berikut [Gambar](https://drive.google.com/file/d/1Zl_7fM9pqb6hI0T0ktstbKNagkzqy3jA/view?usp=sharing). \
Penjelasan : Remote Config berguna untuk menyimpan data (sementara). Cashplus menggunakannya untuk melakukan pengecekan versi aplikasi. \
Cara penggunaan : Setelah AAB terupload ke Play Store, tunggu beberapa hari kemudian update data *remote config* "data_version".

**Running App**

*BASE_URL* terletak di [build.gradle(:app)](https://github.com/cashplus-id/cashplus-android/blob/recap/app/build.gradle).\
Perhatikan kode di bawah &darr;
```kotlin
buildTypes {
    def KEY_BASE_URL_COMMON = 'BASE_URL_COMMON'
    def KEY_BASE_URL_HISTORY = 'BASE_URL_HISTORY'

    debug {
//            minifyEnabled true
//            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'

        buildConfigField 'String', 'BASE_URL_COMMON', "\"https://pentes.cashplus.id:3501/api/apps/\""
        buildConfigField 'String', 'BASE_URL_HISTORY', "\"https://pentes.cashplus.id:3501/api/apps/\""
//            buildConfigField 'String', KEY_BASE_URL_COMMON, "\"https://api.cashplus.id:3601/api/apps/\""
//            buildConfigField 'String', KEY_BASE_URL_HISTORY, "\"https://api.cashplus.id:3601/api/apps/\""
//            buildConfigField 'String', KEY_BASE_URL_HISTORY, "\"https://api.cashplus.id:3301/api/apps/\""
    }
}
```
Ketika ingin running app fokuslah pada *debug* object class untuk memilih server mana yang ingin diakses.

**Build APK & AAB**

Perhatikan kode di bawah &darr;
```kotlin
buildTypes {
    def KEY_BASE_URL_COMMON = 'BASE_URL_COMMON'
    def KEY_BASE_URL_HISTORY = 'BASE_URL_HISTORY'

    release {
//            debuggable true
        minifyEnabled true
        shrinkResources true

//          NOTE: DON'T CHANGE BUILD CONFIG IN RELEASE CONFIG (BELOW)
//            buildConfigField 'String', 'BASE_URL_COMMON', "\"https://pentes.cashplus.id:3501/api/apps/\""
//            buildConfigField 'String', 'BASE_URL_HISTORY', "\"https://pentes.cashplus.id:3501/api/apps/\""
        buildConfigField 'String', KEY_BASE_URL_COMMON, "\"https://api.cashplus.id:3601/api/apps/\""
        buildConfigField 'String', KEY_BASE_URL_HISTORY, "\"https://api.cashplus.id:3601/api/apps/\""
//            buildConfigField 'String', KEY_BASE_URL_HISTORY, "\"https://api.cashplus.id:3301/api/apps/\""
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```
Ketika ingin build *APK* atau *AAB* fokuslah pada *release* object class untuk memilih server mana yang ingin diakses. Unduh dan gunakan [Keystore](https://drive.google.com/drive/folders/13IYlkazeH3v8aj3AaSsLbImmUxTvVal1?usp=sharing) untuk keperluan build.

----

## Development Support

**VNC viewer(ST24 Engine Remote)**

Untuk mempermudah development, kita dapat mengakses sistem product ST24 (hanya pentest. production hanya boleh diakses sama admin & tim product). Untuk penjelasan penggunaan ST24 mulai dari cara menggunakan VNC sebagai berikut:

- Unduh aplikasi VNC Viewer yang tersedia di website kemudian install.
- Masukkan VNC Server address : 103.139.244.75:5000
- Kosongkan username, langsung masukkan password : Pentest@Sukses2024!
- Untuk penggunaannya silahkan tanyakan ke tim produk

---

## What's Next?

- **Perbaikan dan penyempurnaan fitur transfer luar negeri**
- **Mengerjakan fitur Topup E-Money *Flazz* dan *Mandiri***
- **Mengerjakan fitur *Point of Sale***

---

**Note**: Jika mengalami kendala dalam proses pengembangan, silahkan hubungi pihak terkait.
