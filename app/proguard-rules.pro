-keep class com.musiqflow.lite.** { *; }
-keep class androidx.media3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# NewPipeExtractor and dependencies
-dontwarn com.google.re2j.**
-dontwarn java.beans.**
-dontwarn javax.script.**
-dontwarn org.mozilla.javascript.**
-dontwarn org.jsoup.**

# Keep NewPipeExtractor classes
-keep class org.schabi.newpipe.** { *; }
-keep class org.mozilla.javascript.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.musiqflow.lite.**$$serializer { *; }
-keepclassmembers class com.musiqflow.lite.** {
    *** Companion;
}
-keepclasseswithmembers class com.musiqflow.lite.** {
    kotlinx.serialization.KSerializer serializer(...);
}
