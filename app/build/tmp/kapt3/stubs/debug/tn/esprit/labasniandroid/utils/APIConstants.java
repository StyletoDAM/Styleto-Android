package tn.esprit.labasniandroid.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0012\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\u0007R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Ltn/esprit/labasniandroid/utils/APIConstants;", "", "()V", "APPLE_AUTH_PATH", "", "BASE_URL", "getBASE_URL", "()Ljava/lang/String;", "CLOUDINARY_CLOUD_NAME", "CLOUDINARY_GALLERY_TAG", "CLOUDINARY_GALLERY_URL", "getCLOUDINARY_GALLERY_URL", "DEFAULT_BASE_URL", "FORGOT_PASSWORD_PATH", "GOOGLE_AUTH_PATH", "JSON_CONTENT_TYPE", "PROFILE_PATH", "RESET_PASSWORD_PATH", "SIGNIN_PATH", "SIGNUP_PATH", "VERIFY_EMAIL_PATH", "VERIFY_OTP_PATH", "app_debug"})
public final class APIConstants {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEFAULT_BASE_URL = "http://10.0.2.2:3000";
    
    /**
     * URL du backend.
     *
     * - Émulateur officiel : laissez `labasni.baseUrl` vide pour utiliser 10.0.2.2.
     * - Téléphone réel : dans `local.properties`, ajoutez `labasni.baseUrl=http://<ip_de_votre_mac>:3000`.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SIGNUP_PATH = "/auth/signup";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SIGNIN_PATH = "/auth/signin";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GOOGLE_AUTH_PATH = "/auth/google";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APPLE_AUTH_PATH = "/auth/apple";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PROFILE_PATH = "/auth/profile";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VERIFY_EMAIL_PATH = "/auth/verify-email";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FORGOT_PASSWORD_PATH = "/auth/forgot-password";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VERIFY_OTP_PATH = "/auth/verify-otp";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String RESET_PASSWORD_PATH = "/auth/reset-password";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String JSON_CONTENT_TYPE = "application/json";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CLOUDINARY_CLOUD_NAME = "your_cloud_name";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CLOUDINARY_GALLERY_TAG = "profile_gallery";
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.APIConstants INSTANCE = null;
    
    private APIConstants() {
        super();
    }
    
    /**
     * URL du backend.
     *
     * - Émulateur officiel : laissez `labasni.baseUrl` vide pour utiliser 10.0.2.2.
     * - Téléphone réel : dans `local.properties`, ajoutez `labasni.baseUrl=http://<ip_de_votre_mac>:3000`.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBASE_URL() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCLOUDINARY_GALLERY_URL() {
        return null;
    }
}