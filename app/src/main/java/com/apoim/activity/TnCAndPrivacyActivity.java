package com.apoim.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.R;
import com.apoim.app.Apoim;
import com.apoim.server_task.WebService;
import com.apoim.util.InsLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

public class TnCAndPrivacyActivity extends AppCompatActivity {
    InsLoadingView loadingView;
    WebView webView;
    String baseWebViewUrl = "<iframe src='http://docs.google.com/gview?embedded=true&url=";
    String screenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tn_c);

        webView =  findViewById(R.id.webview);
        loadingView = findViewById(R.id.loading_view);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView action_bar = findViewById(R.id.action_bar);

        if(getIntent().getStringExtra("screenType") != null){
            screenType = getIntent().getStringExtra("screenType");

            if(screenType.equals("term_condition")){
                action_bar.setText("Terms and Conditions");
            }else {
                action_bar.setText("Privacy Policy");
            }
        }



        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingView.setVisibility(View.VISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                loadingView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        showAboutUsContent();

    }


    private void showAboutUsContent() {
        loadingView.setVisibility(View.VISIBLE);

        WebService service = new WebService(this, Apoim.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {

                        if(screenType.equals("term_condition")){
                            String termnCondition = jsonObject.getString("t&c");
                            webView.loadData( baseWebViewUrl+termnCondition+"' width='100%' height='100%' style='border: none;'></iframe>" , "text/html",  "UTF-8");
                        }else {
                            String privacyPolicy = jsonObject.getString("privacyPolicy");
                            String doc="<iframe src='http://docs.google.com/gview?embedded=true&url="+privacyPolicy+"' width='100%' height='100%' style='border: none;'></iframe>";
                            webView.loadData(baseWebViewUrl+privacyPolicy+"' width='100%' height='100%' style='border: none;'></iframe>" , "text/html", "UTF-8");
                        }
                    }
                    else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);

                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                loadingView.setVisibility(View.GONE);
            }
        });
        service.callGetSimpleVolley("user/getContent");
    }
}
