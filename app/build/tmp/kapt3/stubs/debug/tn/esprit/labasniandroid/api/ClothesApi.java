package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\b\b\u0001\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J$\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00140\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0015J$\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00140\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0015J2\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0018H\u00a7@\u00a2\u0006\u0002\u0010\u0019\u00a8\u0006\u001a"}, d2 = {"Ltn/esprit/labasniandroid/api/ClothesApi;", "", "createCloth", "Lretrofit2/Response;", "Ltn/esprit/labasniandroid/api/ClothResponse;", "token", "", "request", "Ltn/esprit/labasniandroid/api/CreateClothRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/CreateClothRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteCloth", "", "id", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "detectCloth", "Ltn/esprit/labasniandroid/api/DetectionApiResponse;", "photo", "Lokhttp3/MultipartBody$Part;", "(Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getClothes", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSellSuggestions", "updateFeedback", "Ltn/esprit/labasniandroid/api/UpdateFeedbackRequest;", "(Ljava/lang/String;Ljava/lang/String;Ltn/esprit/labasniandroid/api/UpdateFeedbackRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ClothesApi {
    
    @retrofit2.http.GET(value = "/cloth/my")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getClothes(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.ClothResponse>>> $completion);
    
    @retrofit2.http.POST(value = "/cloth")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createCloth(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.CreateClothRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.ClothResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "/cloth/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCloth(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "/detect")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object detectCloth(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part photo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.DetectionApiResponse>> $completion);
    
    @retrofit2.http.PATCH(value = "/cloth/{id}/feedback")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateFeedback(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.UpdateFeedbackRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "/cloth/sell-suggestions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSellSuggestions(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.ClothResponse>>> $completion);
}