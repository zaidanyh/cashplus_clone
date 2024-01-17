# Optimize
-optimizations !field/*,!class/merging/*,*

# will keep line numbers and file name obfuscation
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Apache POI
-dontwarn org.apache.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn org.etsi.**
-dontwarn org.w3.**
-dontwarn com.microsoft.schemas.**
-dontwarn com.graphbuilder.**

-dontnote org.apache.**
-dontnote org.openxmlformats.schemas.**
-dontnote org.etsi.**
-dontnote org.w3.**
-dontnote com.microsoft.schemas.**
-dontnote com.graphbuilder.**

-keeppackagenames org.apache.poi.ss.formula.function

-keep,allowoptimization class org.apache.commons.compress.archivers.zip.** { *; }
-keep,allowoptimization class org.apache.poi.** { *; }
-keep,allowoptimization class org.apache.xmlbeans.** { *; }
-keep,allowoptimization class org.openxmlformats.schemas.** {*;}
-keep,allowoptimization class com.microsoft.schemas.** { *; }
-keep,allowoptimization class schemaorg_apache_xmlbeans.** { *; }