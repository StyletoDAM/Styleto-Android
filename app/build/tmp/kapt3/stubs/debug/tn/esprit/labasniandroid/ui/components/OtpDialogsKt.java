package tn.esprit.labasniandroid.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\u001ax\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001ax\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00032\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001an\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00032\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\u0006\u0010\u0018\u001a\u00020\u00032\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0006\u0010\b\u001a\u00020\t2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u00a8\u0006\u001b"}, d2 = {"OtpEntryDialog", "", "maskedPhone", "", "code", "onCodeChange", "Lkotlin/Function1;", "errorMessage", "isLoading", "", "resendSecondsRemaining", "", "canResend", "onVerify", "Lkotlin/Function0;", "onResend", "onDismiss", "PinEntryDialog", "email", "pinCode", "onPinCodeChange", "ResetPasswordDialog", "newPassword", "onNewPasswordChange", "confirmPassword", "onConfirmPasswordChange", "onReset", "app_debug"})
public final class OtpDialogsKt {
    
    /**
     * Dialog pour saisir le code OTP SMS lors de la réinitialisation de mot de passe
     */
    @androidx.compose.runtime.Composable()
    public static final void OtpEntryDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String maskedPhone, @org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCodeChange, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, boolean isLoading, int resendSecondsRemaining, boolean canResend, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerify, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResend, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    /**
     * Dialog pour définir un nouveau mot de passe après vérification OTP
     */
    @androidx.compose.runtime.Composable()
    public static final void ResetPasswordDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String newPassword, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onNewPasswordChange, @org.jetbrains.annotations.NotNull()
    java.lang.String confirmPassword, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirmPasswordChange, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, boolean isLoading, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReset, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    /**
     * Dialog pour saisir le code PIN email lors de l'inscription
     */
    @androidx.compose.runtime.Composable()
    public static final void PinEntryDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String pinCode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPinCodeChange, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, boolean isLoading, int resendSecondsRemaining, boolean canResend, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerify, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onResend, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}