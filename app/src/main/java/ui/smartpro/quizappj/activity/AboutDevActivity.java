package ui.smartpro.quizappj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.utilities.ActivityUtilities;

public class AboutDevActivity extends BaseActivity {

    private ImageView imageView;
    private TextView tvDevTitle, tvDevSubTitle, tvDevText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dev);
        //Инициализируем экранные компоненты
        imageView = (ImageView) findViewById(R.id.imgDev);
        tvDevTitle = (TextView) findViewById(R.id.tvDevTitle);
        tvDevSubTitle = (TextView) findViewById(R.id.tvDevSubtitle);
        tvDevText = (TextView) findViewById(R.id.tvDevText);

        initToolbar(true);
        setToolbarTitle(getString(R.string.about_dev));
        enableUpButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
//В окне «О приложении» мы решили сделать изображение и текст кликабельными, они ведут на наш сайт.
// Для этого реализуем метод clickView(),
// где вызывается CustomUrlActivity с адресом нашего сайта.
// Этот метод нужно привязать в макете ко всем вьюшкам, которые мы хотим сделать кликабельными
    public void clickView(View view) {
        ActivityUtilities.getInstance().invokeCustomUrlActivity(AboutDevActivity.this,  CustomUrlActivity.class,
                getResources().getString(R.string.site), getResources().getString(R.string.site_url), false);
    }
}