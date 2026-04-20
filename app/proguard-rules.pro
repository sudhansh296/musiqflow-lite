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
-dontwarn org.slf4j.**
-dontwarn org.slf4j.impl.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

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

-keep,includedescriptorclasses class com.musiqflow.lite.**$serializer { *; }
-keepclassmembers class com.musiqflow.lite.** {
    *** Companion;
}
-keepclasseswithmembers class com.musiqflow.lite.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Innertube serialization
-keep,includedescriptorclasses class com.metrolist.innertube.**$serializer { *; }
-keepclassmembers class com.metrolist.innertube.** {
    *** Companion;
}
-keepclasseswithmembers class com.metrolist.innertube.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.metrolist.innertube.** { *; }

# Ktor
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

# Timber
-dontwarn com.jakewharton.timber.**
