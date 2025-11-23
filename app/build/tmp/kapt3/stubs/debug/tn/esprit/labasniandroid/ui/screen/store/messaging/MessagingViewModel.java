package tn.esprit.labasniandroid.ui.screen.store.messaging;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0014\u001a\u00020\u0015J\u0016\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\nJ\u0010\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\nH\u0002J\b\u0010\u001a\u001a\u00020\u0015H\u0014R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Ltn/esprit/labasniandroid/ui/screen/store/messaging/MessagingViewModel;", "Landroidx/lifecycle/ViewModel;", "chatRepository", "Ltn/esprit/labasniandroid/models/repositories/ChatRepository;", "(Ltn/esprit/labasniandroid/models/repositories/ChatRepository;)V", "_conversations", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Ltn/esprit/labasniandroid/models/entities/Conversation;", "_errorMessage", "", "_isLoading", "", "conversations", "Lkotlinx/coroutines/flow/StateFlow;", "getConversations", "()Lkotlinx/coroutines/flow/StateFlow;", "errorMessage", "getErrorMessage", "isLoading", "clearMessages", "", "initialize", "token", "userId", "loadConversations", "onCleared", "app_debug"})
public final class MessagingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final tn.esprit.labasniandroid.models.repositories.ChatRepository chatRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Conversation>> _conversations = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Conversation>> conversations = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    
    public MessagingViewModel(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.models.repositories.ChatRepository chatRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<tn.esprit.labasniandroid.models.entities.Conversation>> getConversations() {
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
    
    public final void initialize(@org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
    
    private final void loadConversations(java.lang.String token) {
    }
    
    public final void clearMessages() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    public MessagingViewModel() {
        super();
    }
}