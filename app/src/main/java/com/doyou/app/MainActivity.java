package com.doyou.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.ValueCallback;
import android.widget.Toast;
import java.io.OutputStream;
import java.util.HashMap;

/** Android offline host for Do You. Nothing in this wrapper requires INTERNET permission. */
public class MainActivity extends Activity {
    private static final int REQUEST_FILE=1001, REQUEST_EXPORT=1002, REQUEST_NOTIFICATIONS=1003;
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private byte[] pendingExport;
    private final Handler mainHandler=new Handler(Looper.getMainLooper());

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(246,247,251));
        getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        webView=new WebView(this);
        setContentView(webView);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setAllowFileAccessFromFileURLs(false);
        s.setAllowUniversalAccessFromFileURLs(false);
        if(Build.VERSION.SDK_INT>=26) s.setSafeBrowsingEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest request){
                Uri uri=request.getUrl();
                if("file".equals(uri.getScheme()))return false;
                // Don't allow untrusted websites inside privileged local WebView.
                try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception ignored){}
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView wv,ValueCallback<Uri[]> cb,FileChooserParams params){
                if(fileCallback!=null)fileCallback.onReceiveValue(null);
                fileCallback=cb;
                try{Intent intent=new Intent(Intent.ACTION_GET_CONTENT);intent.setType("application/json");intent.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(Intent.createChooser(intent,"Choose Do You backup"),REQUEST_FILE);return true;}
                catch(Exception e){fileCallback=null;return false;}
            }
        });
        webView.addJavascriptInterface(new AppBridge(),"Android");
        AlarmReceiver.ensureChannel(this);
        webView.loadUrl("file:///android_asset/index.html");
        if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQUEST_NOTIFICATIONS);
    }
    @Override protected void onActivityResult(int request,int result,Intent resultData){
        super.onActivityResult(request,result,resultData);
        if(request==REQUEST_FILE){
            if(fileCallback!=null){
                Uri[] uris=result==RESULT_OK&&resultData!=null&&resultData.getData()!=null?new Uri[]{resultData.getData()}:null;
                fileCallback.onReceiveValue(uris);fileCallback=null;
            }
        }else if(request==REQUEST_EXPORT){
            if(result==RESULT_OK&&resultData!=null&&resultData.getData()!=null&&pendingExport!=null){
                try(OutputStream os=getContentResolver().openOutputStream(resultData.getData())){if(os!=null){os.write(pendingExport);Toast.makeText(this,"Do You backup saved",Toast.LENGTH_SHORT).show();}}
                catch(Exception e){Toast.makeText(this,"Export failed: "+e.getMessage(),Toast.LENGTH_LONG).show();}
            }
            pendingExport=null;
        }
    }
    @Override public void onBackPressed(){
        webView.evaluateJavascript("window.doYouGoBack && window.doYouGoBack()",v->{
            if(!"true".equals(v))super.onBackPressed();
        });
    }
    @Override protected void onDestroy(){if(webView!=null){webView.removeJavascriptInterface("Android");webView.destroy();}super.onDestroy();}
    public class AppBridge {
        @JavascriptInterface public void saveFile(String filename,String mime,String base64){
            // Only the local bundled app receives this bridge. Android SAF never needs storage permission.
            try{byte[] bytes=Base64.decode(base64,Base64.DEFAULT);mainHandler.post(()->{
                pendingExport=bytes;
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(mime);intent.putExtra(Intent.EXTRA_TITLE,filename);
                try{startActivityForResult(intent,REQUEST_EXPORT);}catch(Exception e){Toast.makeText(MainActivity.this,"No file manager available",Toast.LENGTH_LONG).show();}
            });}catch(Exception ignored){}
        }
        @JavascriptInterface public void scheduleReminder(String id,String title,long when){
            AlarmReceiver.schedule(MainActivity.this,id,title,when);
        }
        @JavascriptInterface public void cancelAllReminders(){AlarmReceiver.cancelAll(MainActivity.this);}
        @JavascriptInterface public void requestNotifications(){mainHandler.post(()->{
            if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQUEST_NOTIFICATIONS);
            else Toast.makeText(MainActivity.this,"Notification permission already available",Toast.LENGTH_SHORT).show();
        });}
    }
}
