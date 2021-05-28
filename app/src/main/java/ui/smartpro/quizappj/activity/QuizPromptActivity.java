package ui.smartpro.quizappj.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.data.AppPreference;
import ui.smartpro.quizappj.utilities.ActivityUtilities;

public class QuizPromptActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;
    private Button mBtnYes, mBtnNo;
    private TextView firstText, thirdtext;
    //значения номера теста, предыдущего результата и счетчика вопросов
    private String categoryId, score, questionsCount;
    //private String questionsCount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initListener();
    }
    //инициализируем переменные
    // В первом инициализируем переменные. Номер теста получаем из интента,
    // а счетчики вопросов и правильных ответов из метода getQuizResult().
    // Он получает значения из SharedPreferences.
    // Это способ хранения данных приложения в виде пар ключ — значение.
    // Все эти переменные нам нужны для того,
    // чтобы передавать на этот экран результаты предыдущего прохождения теста,
    // которые мы будем сохранять в коде класса экрана тестирования.
    private void initVar() {
        mActivity = QuizPromptActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
            score = AppPreference.getInstance(mContext).getString(categoryId);
            questionsCount = AppPreference.getInstance(mContext).getString(categoryId + AppConstants.QUESTIONS_IN_TEST);
        }
    }
//инициализируем экранные компоненты, а также проверяем, если счетчики вопросов и
// правильных ответов содержат значения, используем их в первом текстовом поле для отображения
// результата последнего прохождения теста.
// Используем выражения %s в строковом ресурсе quiz_promt_first_text,
// они будут заменены на значения переданных в метод getString() переменных score и questionsCount.
    private void initView() {
        setContentView(R.layout.activity_quiz_prompt);

        mBtnYes = (Button) findViewById(R.id.btn_yes);
        mBtnNo = (Button) findViewById(R.id.btn_no);

        firstText = (TextView) findViewById(R.id.first_text);
        thirdtext = (TextView) findViewById(R.id.third_text);

        if (score != null && questionsCount != null) {
            firstText.setText(getString(R.string.quiz_promt_first_text, score, questionsCount));
            thirdtext.setText(R.string.quiz_promt_third_text);
        }

        initToolbar(true);
        setToolbarTitle(getString(R.string.quiz_prompt));
        enableUpButton();


    }

//присваиваем слушатели кликов кнопкам и
// в случае нажатия кнопки «Да» вызываем класс QuizActivity с помощью метода invokeCommonQuizActivity()
    private void initListener() {
        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeCommonQuizActivity(mActivity, QuizActivity.class, categoryId, true);
            }
        });
        //При нажатии кнопки «Нет» в классе QuizPromptActivity вызывается главный экран приложения
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
            }
        });
    }
//определяет стрелку «Назад» в тулбаре и возвращает на главный экран при ее нажатии
    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//перехватывает системную кнопку «Назад» и добавляет ей аналогичное действие
    @Override
    public void onBackPressed() {
        ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
    }
}
