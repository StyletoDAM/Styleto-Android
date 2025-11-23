package tn.esprit.labasniandroid.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0004J\u0016\u0010\u0013\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0004J\u0016\u0010\u0015\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Ltn/esprit/labasniandroid/utils/TokenManager;", "", "()V", "KEY_ACCESS_TOKEN", "", "KEY_GENDER", "KEY_USER_ID", "PREFS_NAME", "clearToken", "", "context", "Landroid/content/Context;", "getGender", "getSharedPreferences", "Landroid/content/SharedPreferences;", "getToken", "getUserId", "saveGender", "gender", "saveToken", "token", "saveUserId", "userId", "app_debug"})
public final class TokenManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "labasni_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ACCESS_TOKEN = "access_token";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_USER_ID = "user_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_GENDER = "gender";
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.TokenManager INSTANCE = null;
    
    private TokenManager() {
        super();
    }
    
    private final android.content.SharedPreferences getSharedPreferences(android.content.Context context) {
        return null;
    }
    
    public final void saveToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void clearToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void saveUserId(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getUserId(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void saveGender(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String gender) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getGender(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}