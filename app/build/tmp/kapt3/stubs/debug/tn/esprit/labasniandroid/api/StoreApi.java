package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J2\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ(\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ(\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J(\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0014\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0015J$\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00170\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0018J$\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00170\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0018J2\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0014\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006\u001d"}, d2 = {"Ltn/esprit/labasniandroid/api/StoreApi;", "", "confirmPurchase", "Lretrofit2/Response;", "Ltn/esprit/labasniandroid/api/StoreItemResponse;", "token", "", "storeItemId", "request", "Ltn/esprit/labasniandroid/api/ConfirmPurchaseRequest;", "(Ljava/lang/String;Ljava/lang/String;Ltn/esprit/labasniandroid/api/ConfirmPurchaseRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createPaymentIntent", "Ltn/esprit/labasniandroid/api/PaymentIntentResponse;", "Ltn/esprit/labasniandroid/api/CreatePaymentIntentRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/CreatePaymentIntentRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createStoreItem", "Ltn/esprit/labasniandroid/api/CreateStoreItemRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/CreateStoreItemRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteStoreItem", "", "id", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllStoreItems", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getStoreItems", "updateStoreItem", "Ltn/esprit/labasniandroid/api/UpdateStoreItemRequest;", "(Ljava/lang/String;Ljava/lang/String;Ltn/esprit/labasniandroid/api/UpdateStoreItemRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
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
    
    @retrofit2.http.POST(value = "/store/payment-intent")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createPaymentIntent(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.CreatePaymentIntentRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.PaymentIntentResponse>> $completion);
    
    @retrofit2.http.POST(value = "/store/purchase/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object confirmPurchase(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String storeItemId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.ConfirmPurchaseRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.StoreItemResponse>> $completion);
}