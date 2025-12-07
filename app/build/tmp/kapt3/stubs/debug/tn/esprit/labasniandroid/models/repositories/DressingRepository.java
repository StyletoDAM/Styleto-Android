package tn.esprit.labasniandroid.models.repositories;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004JL\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\tH\u0002J,\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J0\u0010\u0018\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\t0\u00190\u00062\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0012\u0010\u001f\u001a\u00020\t2\b\u0010 \u001a\u0004\u0018\u00010!H\u0002J*\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070#0\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b$\u0010%J*\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070#0\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\'\u0010%J6\u0010(\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070#0)0\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b*\u0010%J\u0012\u0010+\u001a\u00020\t2\b\u0010,\u001a\u0004\u0018\u00010\tH\u0002J4\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00140\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010.\u001a\u00020\t2\u0006\u0010/\u001a\u000200H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b1\u00102J\f\u00103\u001a\u00020\u0007*\u000204H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00065"}, d2 = {"Ltn/esprit/labasniandroid/models/repositories/DressingRepository;", "", "clothesApi", "Ltn/esprit/labasniandroid/api/ClothesApi;", "(Ltn/esprit/labasniandroid/api/ClothesApi;)V", "addCloth", "Lkotlin/Result;", "Ltn/esprit/labasniandroid/models/entities/Cloth;", "token", "", "imageURL", "category", "color", "style", "season", "addCloth-bMdYcbs", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "buildPlaceholder", "colorHex", "deleteCloth", "", "clothId", "deleteCloth-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "detectCloth", "Lkotlin/Pair;", "Ltn/esprit/labasniandroid/models/DetectionResult;", "bitmap", "Landroid/graphics/Bitmap;", "detectCloth-gIAlu-s", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "extractCategory", "categoryElement", "Lcom/google/gson/JsonElement;", "fetchClothes", "", "fetchClothes-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchSellSuggestions", "fetchSellSuggestions-gIAlu-s", "fetchVTOReadyClothes", "", "fetchVTOReadyClothes-gIAlu-s", "normalizeColor", "rawColor", "updateFeedback", "clotheId", "accepted", "", "updateFeedback-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toEntity", "Ltn/esprit/labasniandroid/api/ClothResponse;", "app_debug"})
public final class DressingRepository {
    @org.jetbrains.annotations.NotNull()
    private final tn.esprit.labasniandroid.api.ClothesApi clothesApi = null;
    
    public DressingRepository(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.api.ClothesApi clothesApi) {
        super();
    }
    
    private final tn.esprit.labasniandroid.models.entities.Cloth toEntity(tn.esprit.labasniandroid.api.ClothResponse $this$toEntity) {
        return null;
    }
    
    private final java.lang.String extractCategory(com.google.gson.JsonElement categoryElement) {
        return null;
    }
    
    private final java.lang.String normalizeColor(java.lang.String rawColor) {
        return null;
    }
    
    private final java.lang.String buildPlaceholder(java.lang.String colorHex) {
        return null;
    }
    
    public DressingRepository() {
        super();
    }
}