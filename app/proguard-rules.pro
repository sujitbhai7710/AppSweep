# Proguard rules for AppSweep

# Keep Compose related classes
-keep class androidx.compose.** { *; }

# Keep model classes (used in serialization)
-keep class com.student.appmanager.data.model.** { *; }

# Keep Application class
-keep class com.student.appmanager.AppSweepApp { *; }

# Google Ads
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
