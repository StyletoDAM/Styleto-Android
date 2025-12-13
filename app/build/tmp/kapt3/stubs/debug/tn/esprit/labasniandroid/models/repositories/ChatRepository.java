package tn.esprit.labasniandroid.models.repositories;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 ?2\u00020\u0001:\u0001?B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J,\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000eH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001b\u0010\u001cJ)\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u000e\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b \u0010!J,\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u000eH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b%\u0010\u001cJ\u0006\u0010&\u001a\u00020\u001eJ*\u0010\'\u001a\b\u0012\u0004\u0012\u00020#0\u000b2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020#0\u000b2\u0006\u0010\u0019\u001a\u00020\u000eH\u0082@\u00a2\u0006\u0002\u0010)J<\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000e2\b\b\u0002\u0010*\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b+\u0010,J*\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020#0\u000b0\u00182\u0006\u0010\u0019\u001a\u00020\u000eH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b.\u0010/J\u000e\u00100\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\u000eJ\u0010\u00101\u001a\u00020\f2\u0006\u00102\u001a\u000203H\u0002JB\u00104\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u00105\u001a\u00020\u000e2\u0006\u00106\u001a\u00020\u000e2\n\b\u0002\u00107\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u00108\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u000eJ4\u00109\u001a\b\u0012\u0004\u0012\u00020\f0\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u00105\u001a\u00020\u000eH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b:\u0010;J\f\u0010<\u001a\u00020#*\u00020=H\u0002J\f\u0010<\u001a\u00020\f*\u00020>H\u0002R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0012R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006@"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/ChatRepository;", "", "chatApi", "Ltn/esprit/labasniandroid/api/ChatApi;", "authApi", "Ltn/esprit/labasniandroid/api/AuthApi;", "(Ltn/esprit/labasniandroid/api/ChatApi;Ltn/esprit/labasniandroid/api/AuthApi;)V", "_isConnected", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_messages", "", "Ltn/esprit/labasniandroid/models/entities/Message;", "configuredToken", "", "currentConversationId", "isConnected", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "messages", "getMessages", "socket", "Lio/socket/client/Socket;", "checkForNewMessages", "Lkotlin/Result;", "token", "conversationId", "checkForNewMessages-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "connectSocket", "", "userId", "connectSocket-gIAlu-s", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", "createConversation", "Ltn/esprit/labasniandroid/models/entities/Conversation;", "participantId", "createConversation-0E7RQCE", "disconnectSocket", "enrichParticipants", "conversations", "(Ljava/util/List;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "forceRefresh", "getMessages-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMyConversations", "getMyConversations-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "joinConversation", "parseMessage", "json", "Lorg/json/JSONObject;", "sendMessage", "content", "senderId", "senderName", "senderAvatar", "sendMessageViaRest", "sendMessageViaRest-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toEntity", "Ltn/esprit/labasniandroid/api/ConversationResponse;", "Ltn/esprit/labasniandroid/api/MessageResponse;", "Companion", "app_debug"})
public final class ChatRepository {
    @org.jetbrains.annotations.NotNull()
    private final tn.esprit.labasniandroid.api.ChatApi chatApi = null;
    @org.jetbrains.annotations.NotNull()
    private final tn.esprit.labasniandroid.api.AuthApi authApi = null;
    @org.jetbrains.annotations.Nullable()
    private io.socket.client.Socket socket;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentConversationId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String configuredToken;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Message>> _messages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Message>> messages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile tn.esprit.labasniandroid.models.repositories.ChatRepository INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.models.repositories.ChatRepository.Companion Companion = null;
    
    public ChatRepository(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.ChatApi chatApi, @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.AuthApi authApi) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Message>> getMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected() {
        return null;
    }
    
    public final void disconnectSocket() {
    }
    
    public final void joinConversation(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId) {
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.NotNull()
    java.lang.String senderId, @org.jetbrains.annotations.Nullable()
    java.lang.String senderName, @org.jetbrains.annotations.Nullable()
    java.lang.String senderAvatar, @org.jetbrains.annotations.Nullable()
    java.lang.String token) {
    }
    
    /**
     * Enrichit les participants avec leurs infos complètes (comme iOS enrichParticipants)
     * Si un participant n'a pas de nom (juste un ID), on fait un appel API pour récupérer ses infos
     */
    private final java.lang.Object enrichParticipants(java.util.List<tn.esprit.labasniandroid.models.entities.Conversation> conversations, java.lang.String token, kotlin.coroutines.Continuation<? super java.util.List<tn.esprit.labasniandroid.models.entities.Conversation>> $completion) {
        return null;
    }
    
    private final tn.esprit.labasniandroid.models.entities.Message parseMessage(org.json.JSONObject json) {
        return null;
    }
    
    private final tn.esprit.labasniandroid.models.entities.Message toEntity(tn.esprit.labasniandroid.api.MessageResponse $this$toEntity) {
        return null;
    }
    
    private final tn.esprit.labasniandroid.models.entities.Conversation toEntity(tn.esprit.labasniandroid.api.ConversationResponse $this$toEntity) {
        return null;
    }
    
    public ChatRepository() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\tR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/ChatRepository$Companion;", "", "()V", "INSTANCE", "Ltn/esprit/labasniandroid/models/repositories/ChatRepository;", "getInstance", "chatApi", "Ltn/esprit/labasniandroid/api/ChatApi;", "reset", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final tn.esprit.labasniandroid.models.repositories.ChatRepository getInstance(@org.jetbrains.annotations.NotNull()
        tn.esprit.labasniandroid.api.ChatApi chatApi) {
            return null;
        }
        
        public final void reset() {
        }
    }
}