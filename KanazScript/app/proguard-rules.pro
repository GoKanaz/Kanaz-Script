-dontobfuscate
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class androidx.** { *; }
-keep public class * extends androidx.appcompat.view.ActionMode
-keep class com.google.** { *; }
-keep class org.eclipse.jgit.** { *; }
-keep class * implements org.eclipse.jgit.util.FS {
    *;
}
-keep class * implements org.eclipse.jgit.api.TransportCommand {
    *;
}
-keepclasseswithmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.squareup.moshi.JsonAdapter
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class * extends androidx.room.RoomDatabase
-keep class com.kanaz.script.** { *; }
-keepclassmembers class com.kanaz.script.** {
    *;
}
-keepclasseswithmembers class * {
    @dagger.hilt.* *;
}
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
