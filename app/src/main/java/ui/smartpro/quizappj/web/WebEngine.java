package ui.smartpro.quizappj.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.listeners.WebListener;

//класс будет отвечать за загрузку страницы по ссылке
public class WebEngine {
    //переменные для активити, контекста и вебвью
    private WebView webView;
    private Activity activity;
    private Context context;
    //константf с адресом для просмотра документов Google, в этом формате у нас хранится текст соглашения
    private static final String GOOGLE_DOCS_VIEWER = "https://docs.google.com/viewerng/viewer?url=";
    //еременная интерфейса слушателя
    private WebListener webListener;

// Создаем конструктор, который будет принимать активити и вебвью, а также инициализировать переменную
// контекста, сохраняя в нее глобальный контекст приложения.
// Это даст возможность доступа к ресурсам и системным функциям из любого места приложения.
    public WebEngine(WebView webView, Activity activity) {
        this.webView = webView;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

// устанавливает настройки вебвью, такие как поддержку JavaScript,
// масштабирование страницы по ширине экрана, управление кешированием, кодировкой текста и другие.
    public void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheMaxSize(AppConstants.SITE_CASHE_SIZE);
        webView.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

    //проверяем, если сетевое соединение недоступно, загружаем страницу из кеша
        if (!isNetworkAvailable(context)) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    //инициализирует интерфейс слушателя
    public void initListeners(final WebListener webListener) {
        this.webListener = webListener;
    //устанавливаем WebChromeClient для реализации окна просмотра веб адреса, как в браузере.
        webView.setWebChromeClient(new WebChromeClient() {
   //сообщает текущий прогресс загрузки страницы, здесь вызываем метод нашего слушателя с
   // передачей ему прогресса загрузки
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                webListener.onProgress(newProgress);
            }
//сообщает об изменении заголовка страницы, передаем новый заголовок в метод onPageTitle() слушателя.
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                webListener.onPageTitle(webView.getTitle());
            }
        });
//устанавливаем клиент для вебвью и переопределяем его методы
        webView.setWebViewClient(new WebViewClient() {
            //вызываем загрузку веб адреса
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String webUrl) {

                loadPage(webUrl);
                return true;
            }
//ызываем соответствующие методы нашего слушателя
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webListener.onStart();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webListener.onLoaded();
            }
        });
    }
//определяет всевозможные типы адресов и в зависимости от выбранного типа использует различные методы
// открытия ссылки. Например, в первой группе адресов используется метод
// для запуска системных приложений для выполнения звонка, отправки сообщений или
// открытия местоположения на карте. Вторая группа адресов открывает документы с помощью GoogleDocs.
// Если используется стандартный веб адрес, то он открывается через вебвью.
    public void loadPage(String webUrl) {
        if (isNetworkAvailable(context)) {
            if (webUrl.startsWith("tel:") ||
                    webUrl.startsWith("sms:") ||
                    webUrl.startsWith("mms:") ||
                    webUrl.startsWith("smsto:") ||
                    webUrl.startsWith("mmsto:") ||
                    webUrl.startsWith("mailto:") ||
                    webUrl.contains("geo:")) {
                invokeNativeApp(webUrl);
            } else if (webUrl.contains("?target=blank")) {
                invokeNativeApp(webUrl.replace("?target=blank", ""));
            } else if (webUrl.endsWith(".doc") ||
                    webUrl.endsWith(".docx") ||
                    webUrl.endsWith(".xls") ||
                    webUrl.endsWith(".xlsx") ||
                    webUrl.endsWith(".pptx") ||
                    webUrl.endsWith(".pdf")) {
                webView.loadUrl(GOOGLE_DOCS_VIEWER + webUrl);
                webView.getSettings().setBuiltInZoomControls(true);
            } else {
                webView.loadUrl(webUrl);
            }

        } else {
            webListener.onNetworkError();
        }
    }

    //проверяет доступность сетевого соединения.
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    //создает интент для загрузки веб адреса через нативное приложение
    private void invokeNativeApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }
}
