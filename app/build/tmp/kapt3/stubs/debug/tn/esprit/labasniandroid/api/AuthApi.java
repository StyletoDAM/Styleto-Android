package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ(\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\u0014\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001e\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u001e\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u001eH\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\"H\u00a7@\u00a2\u0006\u0002\u0010#J(\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\u0005\u001a\u00020%H\u00a7@\u00a2\u0006\u0002\u0010&J(\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010(\u001a\u00020)H\u00a7@\u00a2\u0006\u0002\u0010*J\u001e\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u00032\b\b\u0001\u0010\u0005\u001a\u00020-H\u00a7@\u00a2\u0006\u0002\u0010.J\u001e\u0010/\u001a\b\u0012\u0004\u0012\u0002000\u00032\b\b\u0001\u0010\u0005\u001a\u000201H\u00a7@\u00a2\u0006\u0002\u00102\u00a8\u00063"}, d2 = {"Ltn/esprit/labasniandroid/api/AuthApi;", "", "appleAuth", "Lretrofit2/Response;", "Ltn/esprit/labasniandroid/models/Responses$SigninResponse;", "request", "Ltn/esprit/labasniandroid/api/AppleAuthRequest;", "(Ltn/esprit/labasniandroid/api/AppleAuthRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteProfile", "Ltn/esprit/labasniandroid/models/Responses$MessageResponse;", "token", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "forgotPassword", "Ltn/esprit/labasniandroid/models/Responses$ForgotPasswordResponse;", "Ltn/esprit/labasniandroid/api/ForgotPasswordRequest;", "(Ltn/esprit/labasniandroid/api/ForgotPasswordRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfile", "Ltn/esprit/labasniandroid/models/entities/User;", "getUserById", "userId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "googleAuth", "Ltn/esprit/labasniandroid/api/GoogleAuthRequest;", "(Ltn/esprit/labasniandroid/api/GoogleAuthRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resetPassword", "Ltn/esprit/labasniandroid/models/Responses$ResetPasswordResponse;", "Ltn/esprit/labasniandroid/api/ResetPasswordRequest;", "(Ltn/esprit/labasniandroid/api/ResetPasswordRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signin", "Ltn/esprit/labasniandroid/api/SigninRequest;", "(Ltn/esprit/labasniandroid/api/SigninRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signup", "Ltn/esprit/labasniandroid/models/Responses$SignupResponse;", "Ltn/esprit/labasniandroid/api/SignupRequest;", "(Ltn/esprit/labasniandroid/api/SignupRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfile", "Ltn/esprit/labasniandroid/api/UpdateProfileRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/UpdateProfileRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfilePhoto", "image", "Lokhttp3/MultipartBody$Part;", "(Ljava/lang/String;Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyEmail", "Ltn/esprit/labasniandroid/models/Responses$VerifyEmailResponse;", "Ltn/esprit/labasniandroid/api/VerifyEmailRequest;", "(Ltn/esprit/labasniandroid/api/VerifyEmailRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyOtp", "Ltn/esprit/labasniandroid/models/Responses$VerifyOtpResponse;", "Ltn/esprit/labasniandroid/api/VerifyOtpRequest;", "(Ltn/esprit/labasniandroid/api/VerifyOtpRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface AuthApi {
    
    @retrofit2.http.POST(value = "/auth/signup")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object signup(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.SignupRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.SignupResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/signin")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object signin(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.SigninRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.SigninResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/google")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object googleAuth(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.GoogleAuthRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.SigninResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/apple")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object appleAuth(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.AppleAuthRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.SigninResponse>> $completion);
    
    @retrofit2.http.GET(value = "/auth/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getProfile(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.entities.User>> $completion);
    
    @retrofit2.http.PATCH(value = "/auth/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateProfile(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.UpdateProfileRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.entities.User>> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.PATCH(value = "/auth/profile/photo")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateProfilePhoto(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part image, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.entities.User>> $completion);
    
    @retrofit2.http.POST(value = "/auth/verify-email")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyEmail(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.VerifyEmailRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.VerifyEmailResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/forgot-password")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object forgotPassword(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.ForgotPasswordRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.ForgotPasswordResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/verify-otp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyOtp(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.VerifyOtpRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.VerifyOtpResponse>> $completion);
    
    @retrofit2.http.POST(value = "/auth/reset-password")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object resetPassword(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.ResetPasswordRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.ResetPasswordResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "/auth/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteProfile(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.Responses.MessageResponse>> $completion);
    
    @retrofit2.http.GET(value = "/users/{userId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUserById(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "userId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.models.entities.User>> $completion);
}