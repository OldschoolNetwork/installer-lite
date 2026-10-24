-dontwarn javax.naming.**
-dontwarn java.nio.**
-dontwarn java.lang.management.**
-keepattributes Signature,InnerClasses,EnclosingMethod
-dontwarn com.oldschoolnetwork.lite.crypto.jcajce.**

-keep class com.oldschoolnetwork.lite.crypto.jce.provider.** { *; }
-keep class com.oldschoolnetwork.lite.crypto.jcajce.provider.** { *; }

-keep class com.oldschoolnetwork.services.liteinstaller.Payload { *; }
-keep class com.oldschoolnetwork.services.liteinstaller.SystemInstall { *; }
-keep class com.oldschoolnetwork.services.liteinstaller.GoogleComponents$Entry { *; }
