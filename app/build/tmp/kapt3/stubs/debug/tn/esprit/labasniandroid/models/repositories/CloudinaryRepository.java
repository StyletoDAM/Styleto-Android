package tn.esprit.labasniandroid.models.repositories;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001:\u0002\r\u000eB\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\"\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000b\u0010\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u000f"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/CloudinaryRepository;", "", "httpClient", "Lokhttp3/OkHttpClient;", "gson", "Lcom/google/gson/Gson;", "(Lokhttp3/OkHttpClient;Lcom/google/gson/Gson;)V", "fetchGallery", "Lkotlin/Result;", "", "Ltn/esprit/labasniandroid/models/entities/CloudinaryImage;", "fetchGallery-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "CloudinaryListResponse", "CloudinaryResourceDto", "app_debug"})
public final class CloudinaryRepository {
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient httpClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    
    public CloudinaryRepository(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient httpClient, @org.jetbrains.annotations.NotNull()
    com.google.gson.Gson gson) {
        super();
    }
    
    public CloudinaryRepository() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/CloudinaryRepository$CloudinaryListResponse;", "", "resources", "", "Ltn/esprit/labasniandroid/models/repositories/CloudinaryRepository$CloudinaryResourceDto;", "(Ljava/util/List;)V", "getResources", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class CloudinaryListResponse {
        @com.google.gson.annotations.SerializedName(value = "resources")
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto> resources = null;
        
        public CloudinaryListResponse(@org.jetbrains.annotations.NotNull()
        java.util.List<tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto> resources) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto> getResources() {
            return null;
        }
        
        public CloudinaryListResponse() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryListResponse copy(@org.jetbrains.annotations.NotNull()
        java.util.List<tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto> resources) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J5\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001R\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0018"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/CloudinaryRepository$CloudinaryResourceDto;", "", "publicId", "", "secureUrl", "url", "format", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getFormat", "()Ljava/lang/String;", "getPublicId", "getSecureUrl", "getUrl", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    static final class CloudinaryResourceDto {
        @com.google.gson.annotations.SerializedName(value = "public_id")
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String publicId = null;
        @com.google.gson.annotations.SerializedName(value = "secure_url")
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String secureUrl = null;
        @com.google.gson.annotations.SerializedName(value = "url")
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String url = null;
        @com.google.gson.annotations.SerializedName(value = "format")
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String format = null;
        
        public CloudinaryResourceDto(@org.jetbrains.annotations.NotNull()
        java.lang.String publicId, @org.jetbrains.annotations.NotNull()
        java.lang.String secureUrl, @org.jetbrains.annotations.Nullable()
        java.lang.String url, @org.jetbrains.annotations.Nullable()
        java.lang.String format) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPublicId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSecureUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getFormat() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final tn.esprit.labasniandroid.models.repositories.CloudinaryRepository.CloudinaryResourceDto copy(@org.jetbrains.annotations.NotNull()
        java.lang.String publicId, @org.jetbrains.annotations.NotNull()
        java.lang.String secureUrl, @org.jetbrains.annotations.Nullable()
        java.lang.String url, @org.jetbrains.annotations.Nullable()
        java.lang.String format) {
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
}