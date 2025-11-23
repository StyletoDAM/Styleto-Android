package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ.\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\r\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u000eJ$\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0010J(\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0013\u00a8\u0006\u0014"}, d2 = {"Ltn/esprit/labasniandroid/api/ChatApi;", "", "createConversation", "Lretrofit2/Response;", "Ltn/esprit/labasniandroid/api/ConversationResponse;", "token", "", "request", "Ltn/esprit/labasniandroid/api/CreateConversationRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/CreateConversationRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMessages", "", "Ltn/esprit/labasniandroid/api/MessageResponse;", "conversationId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMyConversations", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "Ltn/esprit/labasniandroid/api/SendMessageRequest;", "(Ljava/lang/String;Ltn/esprit/labasniandroid/api/SendMessageRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ChatApi {
    
    @retrofit2.http.POST(value = "/chat/conversations")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createConversation(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.CreateConversationRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.ConversationResponse>> $completion);
    
    @retrofit2.http.GET(value = "/chat/conversations")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMyConversations(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.ConversationResponse>>> $completion);
    
    @retrofit2.http.GET(value = "/chat/conversations/{id}/messages")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMessages(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<tn.esprit.labasniandroid.api.MessageResponse>>> $completion);
    
    @retrofit2.http.POST(value = "/chat/messages")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendMessage(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.SendMessageRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<tn.esprit.labasniandroid.api.MessageResponse>> $completion);
}