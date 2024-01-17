# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-dontwarn sun.misc.**
-dontwarn okhttp3.**
-dontwarn okio.**
# Gson specific classes
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.* { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Application classes that will be serialized/deserialized over Gson, keepclassmembers
-keep class com.pasukanlangit.cashplus.model.request_body.* { *; }
-keep class com.pasukanlangit.cashplus.model.response.* { *; }
-keep class com.pasukanlangit.cashplus.utils.model.* { *; }
-keep class com.pasukanlangit.cashplus.ui.pembayarancart.courir.CourierType.* { *; }
-keep class com.pasukanlangit.id.network.dto.* { *; }
-keep class com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse { *; }
-keep class com.pasukanlangit.id.core.model.* { *; }
-keep class com.pasukanlangit.id.kyc_datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.library_core.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.datasource_downline.network.dto.* { *; }
-keep class com.pasukanlangit.id.rebate.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.nobu.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.nobu.datasource.network.dto.error.NobuErrorResponse { *; }
-keep class com.pasukanlangit.id.travelling.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.cash_transfer.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.cash_topup.datasource.network.dto.* { *; }
-keep class com.pasukanlangit.id.recapitulation.datasource.network.dto.RecapRequestDto
-keep class com.pasukanlangit.id.recapitulation.datasource.network.dto.trans.* { *; }
-keep class com.pasukanlangit.id.recapitulation.datasource.network.dto.deposit.* { *; }

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers enum * { *; }
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

# navigation fragment
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.* { *; }

-keepclassmembers class **.R$* {
       public static <fields>;
}