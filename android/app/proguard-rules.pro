# RCube ProGuard rules

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.rcube.app.**$$serializer { *; }
-keepclassmembers class com.rcube.app.** {
    *** Companion;
}

# Coil
-dontwarn org.jetbrains.annotations.**
