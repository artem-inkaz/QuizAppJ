package ui.smartpro.quizappj.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.utilities.ActivityUtilities;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private Animation animation;
    private ProgressBar progressBar;
    private ConstraintLayout layout;

    // константа для длительности отображения экрана приветствия
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // инициализация компонентов
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout = (ConstraintLayout) findViewById(R.id.splashLayout);
        imageView = (ImageView) findViewById(R.id.ivSplashIcon);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
    }

//создаем метод initFunctionality(), в котором создаем отдельный поток, и в нем отображаем progressBar,
// применяем анимацию к изображению, и создаем слушатель анимации. В его методе onAnimationEnd(),
// который вызывается по окончании анимации, будем запускать главный экран приложения.
//Сам метод initFunctionality() будем вызывать в методе жизненного цикла onResume().
private void initFunctionality() {
    layout.postDelayed(new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
            imageView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // ActivityUtilities Для вызова новых активити
                    ActivityUtilities.getInstance().invokeNewActivity(SplashActivity.this, MainActivity.class, true);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }, SPLASH_DURATION);
}

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }
}