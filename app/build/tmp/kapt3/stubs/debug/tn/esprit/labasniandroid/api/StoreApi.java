package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\rJ$\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u000f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0010J$\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u000f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0010J2\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0015"}, d2 = {"Ltn/esprit/labasniandroid/api/StoreApi;", "", "createStoreItem", "Lretrofit2/Response;", "Ltn/esprit/labasniandroid/api/StoreItemResponse;", "token", "", "request", "Ltn/esprit/labasniandroid/api/CreateStoreItemRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/CreateStoreItemRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteStoreItem", "", "id", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllStoreItems", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getStoreItems", "updateStoreItem", "Ltn/esprit/labasniandroid/api/UpdateStoreItemRequest;", "(Ljava/lang/String;Ljava/lang/String;Ltn/esprit/labasniandroid/api/UpdateStoreItemRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface StoreApi {
    
    @retrofit2.http.GET(value = "/store/my")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getStoreItems(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.StoreItemResponse>>> $completion);
    
    @retrofit2.http.GET(value = "/store")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllStoreItems(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.StoreItemResponse>>> $completion);
    
    @retrofit2.http.POST(value = "/store")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createStoreItem(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.CreateStoreItemRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.StoreItemResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "/store/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteStoreItem(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.PATCH(value = "/store/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateStoreItem(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.UpdateStoreItemRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.StoreItemResponse>> $completion);
}