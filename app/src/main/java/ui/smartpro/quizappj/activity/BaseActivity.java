package ui.smartpro.quizappj.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;

import ui.smartpro.quizappj.R;

//общий базовый класс для всех активити

//нужен для определения некоторых свойств, которые будут изменяться на всех экранах, например,
// установка тулбара и заголовка экрана, отображения прогрессбара и элементов навигации окна.
// Этими свойствами будут управлять соответствующие методы.
public class BaseActivity extends AppCompatActivity {
    //инициализируем активити и контекст.
    private Context context;
    private Activity activity;

    private Toolbar toolbar;
    private LinearLayout loadingView, noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = BaseActivity.this;
        context = activity.getApplicationContext();
    }
    //определяет и устанавливает тулбар для экрана.
    public void initToolbar(boolean isTitleEnabled) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(isTitleEnabled);
    }
    //устанавливает заголовок окна.
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    //добавляет кнопку со стрелкой в тулбаре, ведущую на предыдущий экран
    public void enableUpButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    // будет инициализировать окно состояния загрузки на экране.
    public void initLoader() {
        loadingView = (LinearLayout) findViewById(R.id.loadingView);
        noDataView = (LinearLayout) findViewById(R.id.noDataView);
    }
    //отображает прогрессбар и скрывает ошибку загрузки
    public void showLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }
    //скрывает все элементы окна загрузки
    public void hideLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }
    //скрывает прогрессбар и отображает ошибку загрузки
    public void showEmptyView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}