package tn.esprit.labasniandroid;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0014J\u0010\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Ltn/esprit/labasniandroid/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "paymentSheet", "Lcom/stripe/android/paymentsheet/PaymentSheet;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onPaymentSheetResult", "paymentSheetResult", "Lcom/stripe/android/paymentsheet/PaymentSheetResult;", "app_debug"})
@androidx.compose.material3.ExperimentalMaterial3Api()
public final class MainActivity extends androidx.activity.ComponentActivity {
    private com.stripe.android.paymentsheet.PaymentSheet paymentSheet;
    
    public MainActivity() {
        super(0);
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void onPaymentSheetResult(com.stripe.android.paymentsheet.PaymentSheetResult paymentSheetResult) {
    }
}