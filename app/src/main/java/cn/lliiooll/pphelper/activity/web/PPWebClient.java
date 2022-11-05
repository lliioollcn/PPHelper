package cn.lliiooll.pphelper.activity.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.utils.PLog;
import org.jetbrains.annotations.NotNull;

public class PPWebClient extends WebViewClient {
    private final WebViewClient client;

    public PPWebClient(WebViewClient client) {
        super();
        this.client = client;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (client == null) return super.shouldOverrideUrlLoading(view, url);
        return client.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (client == null) {
            return super.shouldOverrideUrlLoading(view, request);

        }
        return client.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        if (client == null) {
            super.onPageStarted(view, url, favicon);
            return;
        }
        client.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        CookieManager manager = CookieManager.getInstance();
        PLog.log("链接 " + url + " 的cookie如下: " + manager.getCookie(url));
        /*
        if (url != "http://192.168.31.99/index.php"){
            view.loadUrl("http://192.168.31.99/index.php");
        }

         */
        if (client == null) {
            super.onPageFinished(view, url);
            return;
        }
        client.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (client == null) {
            super.onLoadResource(view, url);
            return;
        }
        client.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (client == null) {
            super.onPageCommitVisible(view, url);
            return;
        }
        client.onPageCommitVisible(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (client == null) return super.shouldInterceptRequest(view, url);
        return client.shouldInterceptRequest(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (client == null) return super.shouldInterceptRequest(view, request);

        return client.shouldInterceptRequest(view, request);
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        if (client == null) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
            return;
        }
        client.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (client == null) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            return;
        }
        client.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (client == null) {
            super.onReceivedError(view, request, error);
            return;
        }
        client.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (client == null) {
            super.onReceivedHttpError(view, request, errorResponse);
            return;
        }
        client.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (client == null) {
            super.onFormResubmission(view, dontResend, resend);
            return;
        }
        client.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (client == null) {
            super.doUpdateVisitedHistory(view, url, isReload);
            return;
        }
        client.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (client == null) {
            super.onReceivedSslError(view, handler, error);
            return;
        }
        client.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (client == null) {
            super.onReceivedClientCertRequest(view, request);
            return;
        }
        client.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (client == null) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            return;
        }

        client.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (client == null) return super.shouldOverrideKeyEvent(view, event);
        return client.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (client == null) {
            super.onUnhandledKeyEvent(view, event);
            return;
        }
        client.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (client == null) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }
        client.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
        if (client == null) {
            super.onReceivedLoginRequest(view, realm, account, args);
            return;
        }
        client.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (client == null) return super.onRenderProcessGone(view, detail);
        return client.onRenderProcessGone(view, detail);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {


        if (client != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            client.onSafeBrowsingHit(view, request, threatType, callback);
        } else {
            super.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }
}
