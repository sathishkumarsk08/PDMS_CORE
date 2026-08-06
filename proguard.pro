
-libraryjars <java.home>/jmods
-ignorewarnings

#################################
# Keep DTOs (used across boundary)
#################################
-keep class com.msp.Audit.** { *; }
-keep class com.msp.Bean.** { *; }
-keep class com.msp.common.** { *; }
-keep class com.msp.dao.** { *; }
-keep class com.msp.DbConfig.** { *; }
-keep class com.msp.ida.** { *; }
-keep class com.msp.keymanager.** { *; }
-keep class com.msp.mosip_pms.** { *; }
-keep class com.msp.pms.** { *; }
-keep class com.msp.PSA_service.** { *; }
-keep class com.msp.service.** { *; }
-keep class com.msp.util.** { *; }

-keepattributes MethodParameters
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

#####################################################
# Keep Enum Methods
#####################################################
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#################################
# Ignore framework warnings
#################################
-dontwarn java.**
-dontwarn javax.**
-dontwarn org.springframework.**
-dontwarn com.fasterxml.jackson.**
-dontwarn io.jsonwebtoken.**

#################################
# Obfuscation (aggressive)
#################################
-allowaccessmodification
#-overloadaggressively   # uncomment if you face name‑collision warnings

#################################
# Remove debug info (optional)
#################################
#-renamesourcefileattribute SourceFile

# Keep Spring framework
-keep class org.springframework.** { *; }
-keep class javax.** { *; }

# Keep Spring Boot loader
-keep class org.springframework.boot.loader.** { *; }
