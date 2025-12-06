package tn.esprit.labasniandroid.ui.components

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository

@Composable
fun PaymentWebView(
    url: String,
    token: String,
    subscriptionRepository: SubscriptionRepository,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true  // Required for Stripe
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false

                    if (url.contains("/subscriptions/success")) {
                        val uri = Uri.parse(url)
                        val sessionId = uri.getQueryParameter("session_id")
                        if (sessionId != null) {
                            scope.launch {
                                subscriptionRepository.verifyCheckoutSession(token, sessionId).fold(
                                    onSuccess = { onSuccess() },
                                    onFailure = { error -> onError(error.message ?: "Verification failed") }
                                )
                            }
                        } else {
                            onError("Missing session_id in redirect URL")
                        }
                        return true  // Prevent loading the page
                    } else if (url.contains("/subscriptions/cancel")) {
                        onCancel()
                        return true
                    }
                    return false
                }
            }
            loadUrl(url)
        }
    })
}