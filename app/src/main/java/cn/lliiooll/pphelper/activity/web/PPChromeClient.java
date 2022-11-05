package cn.lliiooll.pphelper.activity.web;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.*;
import androidx.annotation.Nullable;

public class PPChromeClient extends WebChromeClient {
    private final WebChromeClient client;

    public PPChromeClient(WebChromeClient client) {
        super();
        this.client = client;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (client == null) {
            super.onProgressChanged(view, newProgress);
            return;
        }
        client.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (client == null) {
            super.onReceivedTitle(view, title);
            return;
        }
        client.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (client == null) {
            super.onReceivedIcon(view, icon);
            return;
        }
        client.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (client == null) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
            return;
        }
        client.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (client == null) {
            super.onShowCustomView(view, callback);
            return;
        }
        client.onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (client == null) {
            super.onShowCustomView(view, requestedOrientation, callback);
            return;
        }
        client.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        if (client == null) {
            super.onHideCustomView();
            return;
        }
        client.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (client == null) return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        return client.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (client == null) {
            super.onRequestFocus(view);
            return;
        }
        client.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (client == null) {
            super.onCloseWindow(window);
            return;
        }
        client.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (client == null) return super.onJsAlert(view, url, message, result);
        return client.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (client == null) return super.onJsConfirm(view, url, message, result);
        return client.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (client == null) return super.onJsPrompt(view, url, message, defaultValue, result);
        return client.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (client == null) return super.onJsBeforeUnload(view, url, message, result);
        return client.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (client == null) {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            return;
        }
        client.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (client == null) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            return;
        }
        client.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (client == null) {
            super.onGeolocationPermissionsHidePrompt();
            return;
        }
        client.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (client == null) {
            super.onPermissionRequest(request);
            return;
        }
        client.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (client == null) {
            super.onPermissionRequestCanceled(request);
            return;
        }
        client.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        if (client == null) return super.onJsTimeout();
        return client.onJsTimeout();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (client == null) {
            super.onConsoleMessage(message, lineNumber, sourceID);
            return;
        }
        client.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (client == null) return super.onConsoleMessage(consoleMessage);
        return client.onConsoleMessage(consoleMessage);
    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        if (client == null) return super.getDefaultVideoPoster();
        return client.getDefaultVideoPoster();
    }

    @Nullable
    @Override
    public View getVideoLoadingProgressView() {
        if (client == null) return super.getVideoLoadingProgressView();
        return client.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (client == null) {
            super.getVisitedHistory(callback);
            return;
        }
        client.getVisitedHistory(callback);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (client == null) return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        return client.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }
}
