package nendi.wmtreborn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class TentangActivity extends AppCompatActivity {
    ProgressBar progressBar;
    WebView wvAbout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tentang);

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_satu);

        ToolBarAtas2.setLogoDescription("Tentang Aplikasi");
        ToolBarAtas2.setTitle("Tentang Aplikasi");
        setSupportActionBar(ToolBarAtas2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.pgb);
        progressBar.bringToFront();


        wvAbout = (WebView) findViewById(R.id.wv_about);
        wvAbout.getSettings().setLoadsImagesAutomatically(true);
        wvAbout.getSettings().setJavaScriptEnabled(true);
        wvAbout.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wvAbout.setWebViewClient(new AppWebViewClients(progressBar));

        wvAbout.loadUrl("http://mobiwmt.tech/api/about.html");
//        wvAbout.loadUrl("https://stackoverflow.com/questions/41749192/how-to-bring-a-progress-bar-in-front-of-a-button-in-older-api-versions");
    }

    public class AppWebViewClients extends WebViewClient {
        private ProgressBar progressBar;

        public AppWebViewClients(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
}
