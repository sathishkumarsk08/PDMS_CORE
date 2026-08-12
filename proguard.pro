
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
-keep class com.msp.util.** { *; }

-keepattributes MethodParameters
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations


# Keep all service classes
-keep class com.msp.service.**

# Keep specific methods
-keepclassmembers class com.msp.service.mosip_pms.MispLicenseKeyService { *; }
-keepclassmembers class com.msp.service.mosip_pms.PartnerPolicyRequestService { *; }
-keepclassmembers class com.msp.service.mosip_pms.PartnerPolicyService { *; }
-keepclassmembers class com.msp.service.mosip_pms.PartnerRegistrationService { *; }
-keepclassmembers class com.msp.service.mosip_pms.partnerService { *; }
-keepclassmembers class com.msp.service.mosip_pms.PolicyGroupService { *; }
-keepclassmembers class com.msp.service.mosip_pms.PolicyTypeService { *; }

-keepclassmembers class com.msp.service.IDACaCertStoreService { *; }
-keepclassmembers class com.msp.service.KeyMgrCaCertStoreService { *; }
-keepclassmembers class com.msp.service.KeyMgrPartCertStoreService { *; }
-keepclassmembers class com.msp.service.UserdetailsService { *; }

-keepclassmembers class com.msp.service.msp_pms.DeviceModelRegistrationMSPService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPAuditLogs {
    public void setAuditRequestDto(...);
}
-keepclassmembers class com.msp.service.msp_pms.MSPBacklistService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPDeviceBlacklistedService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPDeviceRegistrationService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPDeviceRevokeService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPDeviceSuspensionService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPFeatureMasterService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPMsipLicenseService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPOtpService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPartnerPolicyRequestService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPartnerPolicyService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPartnerRevokeService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPartnerSuspensionService { *; }
-keepclassmembers class com.msp.service.msp_pms.MspPartnerTypeService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPolicyGroupService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPPolicyTypeService { *; }
-keepclassmembers class com.msp.service.msp_pms.MspRegDeviceStatusService { *; }
-keepclassmembers class com.msp.service.msp_pms.MspRegPartSuspension { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPRoleService { *; }
-keepclassmembers class com.msp.service.msp_pms.MSPSignCertStoreService { *; }
-keepclassmembers class com.msp.service.msp_pms.MstRoleGroupService { *; }
-keepclassmembers class com.msp.service.msp_pms.NotificationSystemService { *; }
-keepclassmembers class com.msp.service.msp_pms.PartnerRegistrationMSPService { *; }


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
