package ui.smartpro.quizappj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.listeners.WebListener;
import ui.smartpro.quizappj.web.WebEngine;

public class CustomUrlActivity extends BaseActivity {
    //еременные для контекста, заголовка и адреса страницы, а также переменные классов Webview и WebEngine
    private Activity activity;
    private Context context;
    private String pageTitle, pageUrl;

    private WebView webView;
    private WebEngine webEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
    }
//инициализируем активити и контекст приложения, получаем интент и извлекаем из него заголовок и веб адрес
    private void initVar() {
        activity = CustomUrlActivity.this;
        context = activity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            pageTitle = intent.getStringExtra(AppConstants.BUNDLE_KEY_TITLE);
            pageUrl = intent.getStringExtra(AppConstants.BUNDLE_KEY_URL);
        }
    }
//инициализируем макет экрана и экранные компоненты
    private void initView() {
        setContentView(R.layout.activity_custom_url);
        initWebEngine();
        initLoader();
        ininToolbar(true);
        setToolbarTitle(pageTitle);
        enableUpButton();
    }
//инициализируем экземпляры классов WebView и WebEngine, а также определяем слушатель для управления отображением состояния загрузки
    public void initWebEngine() {
        webView = (WebView) findViewById(R.id.webView);

        webEngine = new WebEngine(webView, activity);
        webEngine.initWebView();

        webEngine.initListeners(new WebListener() {
            @Override
            public void onStart() {
                showLoader();
            }

            @Override
            public void onLoaded() {
                hideLoader();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onNetworkError() {
                showEmptyView();
            }

            @Override
            public void onPageTitle(String title) {

            }
        });
    }
    //инициируем загрузку страницы
    private void initFunctionality() {
        webEngine.loadPage(pageUrl);
    }

//для обработки нажатия кнопки в тулбаре для возврата на предыдущий экран
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
}