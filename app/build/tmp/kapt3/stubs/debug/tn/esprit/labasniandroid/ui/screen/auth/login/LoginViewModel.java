package tn.esprit.labasniandroid.ui.screen.auth.login;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0019\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u0007H\u0002J\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u0007J\u000e\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u0007J2\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u00072\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u0007J\u000e\u0010%\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!J\b\u0010&\u001a\u00020\nH\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0019\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0019\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011\u00a8\u0006\'"}, d2 = {"Ltn/esprit/labasniandroid/ui/screen/auth/login/LoginViewModel;", "Landroidx/lifecycle/ViewModel;", "authService", "Ltn/esprit/labasniandroid/models/services/AuthService;", "(Ltn/esprit/labasniandroid/models/services/AuthService;)V", "_email", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_errorMessage", "_isLoading", "", "_password", "_signinResponse", "Ltn/esprit/labasniandroid/models/Responses$SigninResponse;", "email", "Lkotlinx/coroutines/flow/StateFlow;", "getEmail", "()Lkotlinx/coroutines/flow/StateFlow;", "errorMessage", "getErrorMessage", "isLoading", "password", "getPassword", "signinResponse", "getSigninResponse", "isValidEmail", "resetFeedback", "", "setEmail", "value", "setPassword", "signInWithGoogle", "context", "Landroid/content/Context;", "googleId", "fullName", "profilePicture", "signin", "validateFields", "app_debug"})
public final class LoginViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final tn.esprit.labasniandroid.models.services.AuthService authService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _email = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> email = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _password = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> password = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<tn.esprit.labasniandroid.models.Responses.SigninResponse> _signinResponse = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.models.Responses.SigninResponse> signinResponse = null;
    
    public LoginViewModel(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.models.services.AuthService authService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getPassword() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getErrorMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.models.Responses.SigninResponse> getSigninResponse() {
        return null;
    }
    
    public final void setEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void setPassword(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void signin(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void signInWithGoogle(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String googleId, @org.jetbrains.annotations.NotNull()
    java.lang.String fullName, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.Nullable()
    java.lang.String profilePicture) {
    }
    
    public final void resetFeedback() {
    }
    
    private final boolean validateFields() {
        return false;
    }
    
    private final boolean isValidEmail(java.lang.String email) {
        return false;
    }
    
    public LoginViewModel() {
        super();
    }
}