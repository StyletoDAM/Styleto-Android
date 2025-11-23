package tn.esprit.labasniandroid.ui.screen.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000T\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0003\u001a%\u0010\u0006\u001a\u00020\u00012\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0011\u0010\t\u001a\r\u0012\u0004\u0012\u00020\u00010\n\u00a2\u0006\u0002\b\u000bH\u0003\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0005H\u0003\u001a.\u0010\u000f\u001a\u00020\u00012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0007\u001a\u0018\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0005H\u0003\u001a\u0018\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0005H\u0003\u001a\u0010\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005H\u0002\u001a,\u0010\u001c\u001a\u0010\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u001f\u0018\u00010\u001d2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0082@\u00a2\u0006\u0002\u0010$\u00a8\u0006%"}, d2 = {"CircularProgressView", "", "progress", "", "points", "", "ProfileCard", "modifier", "Landroidx/compose/ui/Modifier;", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "ProfileLabeledField", "label", "value", "ProfileView", "onBack", "onLogout", "viewModel", "Ltn/esprit/labasniandroid/ui/screen/profile/ProfileViewModel;", "ProgressTile", "title", "SuggestionRow", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "text", "initials", "name", "loadBitmapAndBytes", "Lkotlin/Pair;", "Landroid/graphics/Bitmap;", "", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class ProfileViewKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class, androidx.compose.foundation.ExperimentalFoundationApi.class})
    @androidx.compose.runtime.Composable()
    public static final void ProfileView(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLogout, @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ProfileLabeledField(java.lang.String label, java.lang.String value) {
    }
    
    private static final java.lang.Object loadBitmapAndBytes(android.content.Context context, android.net.Uri uri, kotlin.coroutines.Continuation<? super kotlin.Pair<android.graphics.Bitmap, byte[]>> $completion) {
        return null;
    }
    
    private static final java.lang.String initials(java.lang.String name) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ProfileCard(androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SuggestionRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CircularProgressView(float progress, java.lang.String points) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ProgressTile(java.lang.String title, java.lang.String value) {
    }
}