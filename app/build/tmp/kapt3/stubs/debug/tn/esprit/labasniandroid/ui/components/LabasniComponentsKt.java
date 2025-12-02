package tn.esprit.labasniandroid.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000~\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a6\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00052\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\r2\u0006\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u001aN\u0010\u000f\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\u001c\u0010\u0013\u001a\u0018\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\r\u00a2\u0006\u0002\b\u0015\u00a2\u0006\u0002\b\u0016H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0017\u0010\u0018\u001ax\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00032\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\r2\u0006\u0010\u001c\u001a\u00020\u00032\u0015\b\u0002\u0010\u001d\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\t\u00a2\u0006\u0002\b\u00152\u0015\b\u0002\u0010\u001e\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\t\u00a2\u0006\u0002\b\u00152\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u001f\u001a\u00020 2\b\b\u0002\u0010!\u001a\u00020\"H\u0007\u001a@\u0010#\u001a\u00020\u00012\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%2\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\'\u001a\u00020(2\b\b\u0002\u0010)\u001a\u00020(H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b*\u0010+\u001af\u0010,\u001a\u00020\u00012\u0006\u0010-\u001a\u00020\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010.\u001a\u00020\u00112\b\b\u0002\u0010/\u001a\u00020\u00112\n\b\u0002\u00100\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u00101\u001a\u00020\u00052\b\b\u0002\u00102\u001a\u000203H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b4\u00105\u001a5\u00106\u001a\u00020\u00012\u0011\u00107\u001a\r\u0012\u0004\u0012\u00020\u00010\t\u00a2\u0006\u0002\b\u00152\u0006\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u001a\u001c\u00108\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u00109\u001a\u00020:H\u0007\u001a*\u0010;\u001a\u00020\u00012\u0006\u00107\u001a\u00020<2\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010=\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0003\u001aB\u0010>\u001a\u00020\u00012\f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0002\u0010\u0006\u001a\u00020\u00072 \b\u0002\u0010@\u001a\u001a\u0012\u0004\u0012\u00020A\u0012\u0004\u0012\u00020\u0001\u0018\u00010\r\u00a2\u0006\u0002\b\u0015\u00a2\u0006\u0002\b\u0016H\u0007\u001a4\u0010B\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0010\b\u0002\u0010C\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\tH\u0007\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006D"}, d2 = {"GenderChip", "", "title", "", "selected", "", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "Lkotlin/Function0;", "LabasniCheckboxRow", "checked", "onCheckedChange", "Lkotlin/Function1;", "label", "LabasniGradientBackground", "topColor", "Landroidx/compose/ui/graphics/Color;", "bottomColor", "content", "Landroidx/compose/foundation/layout/BoxScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "LabasniGradientBackground-IbeAmgk", "(Landroidx/compose/ui/Modifier;JJLkotlin/jvm/functions/Function1;)V", "LabasniOutlinedField", "value", "onValueChange", "placeholder", "leading", "trailing", "keyboardOptions", "Landroidx/compose/foundation/text/KeyboardOptions;", "visualTransformation", "Landroidx/compose/ui/text/input/VisualTransformation;", "LabasniPageIndicators", "activeIndex", "", "total", "activeSize", "Landroidx/compose/ui/unit/Dp;", "inactiveSize", "LabasniPageIndicators-oZzcvok", "(IILandroidx/compose/ui/Modifier;FF)V", "LabasniPillButton", "text", "background", "contentColor", "borderColor", "enabled", "padding", "Landroidx/compose/foundation/layout/PaddingValues;", "LabasniPillButton-nEz3R_A", "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;JJLandroidx/compose/ui/graphics/Color;ZLandroidx/compose/foundation/layout/PaddingValues;)V", "LabasniStatItem", "icon", "LabasniTabBar", "activeTab", "Ltn/esprit/labasniandroid/ui/components/LabasniTab;", "LabasniTabItem", "Landroidx/compose/ui/graphics/vector/ImageVector;", "isActive", "LabasniTopBar", "onBack", "extraContent", "Landroidx/compose/foundation/layout/RowScope;", "StyleTag", "onToggle", "app_debug"})
public final class LabasniComponentsKt {
    
    @androidx.compose.runtime.Composable()
    public static final void LabasniTopBar(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> extraContent) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LabasniOutlinedField(@org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onValueChange, @org.jetbrains.annotations.NotNull()
    java.lang.String placeholder, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> leading, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> trailing, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.text.KeyboardOptions keyboardOptions, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.text.input.VisualTransformation visualTransformation) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LabasniCheckboxRow(boolean checked, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onCheckedChange, @org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GenderChip(@org.jetbrains.annotations.NotNull()
    java.lang.String title, boolean selected, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StyleTag(@org.jetbrains.annotations.NotNull()
    java.lang.String title, boolean selected, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggle) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LabasniStatItem(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> icon, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LabasniTabBar(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.ui.components.LabasniTab activeTab) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LabasniTabItem(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, boolean isActive, androidx.compose.ui.Modifier modifier) {
    }
}