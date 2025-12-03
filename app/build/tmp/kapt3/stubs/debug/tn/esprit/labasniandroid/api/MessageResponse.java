package tn.esprit.labasniandroid.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001\u0012\u0006\u0010\u0005\u001a\u00020\u0001\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0001H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0001H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003Ja\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00012\b\b\u0002\u0010\u0005\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0004\u001a\u00020\u00018\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0018\u0010\b\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0018\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u0016\u0010\u0005\u001a\u00020\u00018\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0018\u0010\t\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000e\u00a8\u0006\'"}, d2 = {"Ltn/esprit/labasniandroid/api/MessageResponse;", "", "id", "", "conversationId", "senderId", "content", "readAt", "createdAt", "updatedAt", "extractedInfo", "Ltn/esprit/labasniandroid/api/ExtractedInfoResponse;", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ltn/esprit/labasniandroid/api/ExtractedInfoResponse;)V", "getContent", "()Ljava/lang/String;", "getConversationId", "()Ljava/lang/Object;", "getCreatedAt", "getExtractedInfo", "()Ltn/esprit/labasniandroid/api/ExtractedInfoResponse;", "getId", "getReadAt", "getSenderId", "getUpdatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class MessageResponse {
    @com.google.gson.annotations.SerializedName(value = "_id")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @com.google.gson.annotations.SerializedName(value = "conversationId")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Object conversationId = null;
    @com.google.gson.annotations.SerializedName(value = "senderId")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Object senderId = null;
    @com.google.gson.annotations.SerializedName(value = "content")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String content = null;
    @com.google.gson.annotations.SerializedName(value = "readAt")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String readAt = null;
    @com.google.gson.annotations.SerializedName(value = "createdAt")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String createdAt = null;
    @com.google.gson.annotations.SerializedName(value = "updatedAt")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String updatedAt = null;
    @com.google.gson.annotations.SerializedName(value = "extractedInfo")
    @org.jetbrains.annotations.Nullable()
    private final tn.esprit.labasniandroid.api.ExtractedInfoResponse extractedInfo = null;
    
    public MessageResponse(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.Object conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.Object senderId, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.String readAt, @org.jetbrains.annotations.Nullable()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    tn.esprit.labasniandroid.api.ExtractedInfoResponse extractedInfo) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.Object getConversationId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.Object getSenderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getContent() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getReadAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getUpdatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final tn.esprit.labasniandroid.api.ExtractedInfoResponse getExtractedInfo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.Object component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.Object component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final tn.esprit.labasniandroid.api.ExtractedInfoResponse component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final tn.esprit.labasniandroid.api.MessageResponse copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.Object conversationId, @org.jetbrains.annotations.NotNull()
    java.lang.Object senderId, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.String readAt, @org.jetbrains.annotations.Nullable()
    java.lang.String createdAt, @org.jetbrains.annotations.Nullable()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    tn.esprit.labasniandroid.api.ExtractedInfoResponse extractedInfo) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}