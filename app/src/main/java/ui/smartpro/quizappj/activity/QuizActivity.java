package ui.smartpro.quizappj.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.adapters.QuizAdapter;
import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.data.AppPreference;
import ui.smartpro.quizappj.listeners.ListitemClickListener;
import ui.smartpro.quizappj.models.QuizModel;
import ui.smartpro.quizappj.models.ResultModel;
import ui.smartpro.quizappj.utilities.ActivityUtilities;
import ui.smartpro.quizappj.utilities.BeatBox;
import ui.smartpro.quizappj.utilities.DialogUtilities;
import ui.smartpro.quizappj.utilities.SoundUtilities;

public class QuizActivity extends BaseActivity implements  DialogUtilities.OnCompleteListener {

    private Activity mActivity;
    private Context mContext;
    private ImageButton btnSpeaker;
    private Button btnNext;
    private RecyclerView mRecyclerQuiz;
    private TextView tvQuestionText;
    private TextView tvQuestionTitle;
    private ImageView imgFirstLife, imgSecondLife, imgThirdLife, imgFourthLife, imgFifthLife;

    private QuizAdapter mAdapter = null;
    //создаем списки для вопросов, ответов и цветов фона правильных-неправильных ответов
    private List<QuizModel> mItemList;
    ArrayList<String> mOptionList;
    ArrayList<String> mBackgroundColorList;
    private ArrayList<ResultModel> mResultList;

    private int mQuestionPosition = 0;
    private int mQuestionsCount = 0;
    private int mScore = 0, mWrongAns = 0, mSkip = 0;
    private int mLifeCounter = 5;
    private boolean mUserHasPressed = false;
    private boolean mIsSkipped = false, mIsCorrect = false;
    private String mQuestionText, mGivenAnsText, mCorrectAnsText, mCategoryId;

    private BeatBox mBeatBox;
    private List<SoundUtilities> mSounds;
    private boolean isSoundOn;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: initializeRewardedAds();
        //TODO: loadRewardedVideoAds();

        initVar();
        initView();
        loadData();
        initListener();


    }
//инициализируем переменные контекста, получаем номер теста из интента,
// создаем списки для работы с ответами,
// создаем экземпляр класса BeatBox и получаем список звуков
    private void initVar() {
        mActivity = QuizActivity.this;
        mContext = mActivity.getApplicationContext();


        Intent intent = getIntent();
        if (intent != null) {
            mCategoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
        }

        mItemList = new ArrayList<>();
        mOptionList = new ArrayList<>();
        mBackgroundColorList = new ArrayList<>();
        mResultList = new ArrayList<>();

        mBeatBox = new BeatBox(mActivity);
        mSounds = mBeatBox.getSounds();
    }
//получаем макет экрана и инициализируем экранные компоненты, создаем адаптер списка,
// добавляем тулбар, заголовок, стрелку «Назад» и индикатор загрузки
    private void initView() {
        setContentView(R.layout.activity_quiz);

        imgFirstLife = (ImageView) findViewById(R.id.firstLife);
        imgSecondLife = (ImageView) findViewById(R.id.secondLife);
        imgThirdLife = (ImageView) findViewById(R.id.thirdLife);
        imgFourthLife = (ImageView) findViewById(R.id.fourthLife);
        imgFifthLife = (ImageView) findViewById(R.id.fifthLife);
        btnSpeaker = (ImageButton) findViewById(R.id.btnSpeaker);
        btnNext = (Button) findViewById(R.id.btnNext);

        tvQuestionText = (TextView) findViewById(R.id.tvQuestionText);
        tvQuestionTitle = (TextView) findViewById(R.id.tvQuestionTitle);

        mRecyclerQuiz = (RecyclerView) findViewById(R.id.rvQuiz);
        mRecyclerQuiz.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new QuizAdapter(mContext, mActivity, mOptionList, mBackgroundColorList);
        mRecyclerQuiz.setAdapter(mAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.quiz));
        enableUpButton();
        initLoader();

    }
//показываем индикатор загрузки, проверяем сохраненное значение настройки выключения звука,
// устанавливаем картинку для кнопки отключения звука в методе setSpeakerImage() в зависимости от
// значения этой настройки, и вызываем метод loadJson()
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadData() {
        showLoader();

        isSoundOn = AppPreference.getInstance(mActivity).getBoolean(AppConstants.KEY_SOUND, true);
        setSpeakerImage();

        loadJson();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSpeakerImage() {
        if (isSoundOn) {
            btnSpeaker.setImageResource(R.drawable.ic_speaker);
        } else {
            btnSpeaker.setImageResource(R.drawable.ic_speaker_not);
        }
    }

//устанавливаем методом setOnClickListener() слушатели для кнопок и списков,
// обрабатиываем взаимодействие пользователя с ними. Сначала кнопка отключения звука,
// при нажатии меняем значение настройки методом setBoolean() класса AppPreference и
// вызываем метод setSpeakerImage() для смены значка кнопки.
// Затем кнопка перехода к следующему вопросу — при ее нажатии,
// если пользователь не выбрал ни одного ответа, отображаем диалог «Пропустить вопрос?»,
// иначе переходим к следующему вопросу методом setNextQuestion().
//Далее методом setItemClickListener() определяем слушатель для адаптера списка,
// который при первом нажатии на ответ в списке сравнивает его позицию со значением позиции правильного ответа,
// в случае успеха увеличивает на единицу счетчик правильных ответов (переменная mScore),
// сохраняет в список mBackgroundColorList константу с указанием на ресурс зеленого цвета
// (он будет использоваться для фона) и проигрывает звук.
// В случае неудачи передает в mBackgroundColorList красный цвет,
// увеличивает значение счетчика неправильных ответов mWrongAns,
// проигрывает звук и вызывает метод decreaseLifeAndStatus() для уменьшения количества попыток.
// Далее правильному ответу передается зеленый фон и список прокручивается до отображения его на экране.
// Затем в переменную mGivenAnsText сохраняется строка выбранного ответа,
// а в переменную mCorrectAnsText — строка правильного ответа, для экрана результатов.
// Переменной mUserHasPressed присваиваетя true, после чего ответ считается выбранным и уже не реагирует на нажатия.
// А метод notifyDataSetChanged() оповещает адаптер списка ответов об изменении списка для отображения.
    public void initListener() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                isSoundOn = !isSoundOn;
                AppPreference.getInstance(mActivity).setBoolean(AppConstants.KEY_SOUND, isSoundOn);
                setSpeakerImage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserHasPressed) {
                    FragmentManager manager = getSupportFragmentManager();
                    DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.skip_text),
                            getString(R.string.skip_prompt),
                            getString(R.string.yes),
                            getString(R.string.no),
                            AppConstants.BUNDLE_KEY_SKIP_OPTION);
                    dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
                } else {
                    updateResultSet();
                    setNextQuestion();
                }
            }
        });

        mAdapter.setItemClickListener(new ListitemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (!mUserHasPressed) {
                    int clickedAnswerIndex = position;
                    if (mItemList.get(mQuestionPosition).getCorrectAnswer() != -1) {
                        for (int currentItemIndex = 0; currentItemIndex < mOptionList.size(); currentItemIndex++) {
                            if (currentItemIndex == clickedAnswerIndex && currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_GREEN);
                                mScore++;
                                mIsCorrect = true;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                                }
                            } else if (currentItemIndex == clickedAnswerIndex && !(currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer())) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_RED);
                                mWrongAns++;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_SECOND_INDEX));
                                }
                                decreaseLifeAndStatus();
                            } else if (currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_GREEN);
                                ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(currentItemIndex);
                            }
                        }
                    } else {
                        mBackgroundColorList.set(clickedAnswerIndex, AppConstants.COLOR_GREEN);
                        mScore++;
                         mIsCorrect = true;
                        mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                    }

                     mGivenAnsText = mItemList.get(mQuestionPosition).getAnswers().get(clickedAnswerIndex);
                    mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());

                    mUserHasPressed = true;
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

    }
//вызывается при неправильном ответе для уменьшения количества отображаемых попыток.
    public void decreaseLifeAndStatus() {
        mLifeCounter--;
        setLifeStatus();
    }
//он может восстанавливать попытки, будем его использовать в качестве поощрения за просмотр рекламы.
    void increaseLifeAndStatus() {
        if (mLifeCounter < AppConstants.BUNDLE_KEY_MAX_LIFE) {
            mLifeCounter++;
            setLifeStatus();
        }
    }
//управляет видимостью на экране иконок количества попыток
    public void setLifeStatus() {
        switch (mLifeCounter) {
            case 1:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.GONE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 2:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 3:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 4:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 5:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.VISIBLE);
                break;
            default:
                imgFirstLife.setVisibility(View.GONE);
                imgSecondLife.setVisibility(View.GONE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
        }
    }


//инициирует отображение следующего вопроса на экране вызовом метода updateQuestionsAndAnswers().
// Если все попытки израсходованы, отображается диалог с предложение получить дополнительную попытку.
// При отказе будем вызывать здесь экран отображения результатов
    public void setNextQuestion() {
        if (isSoundOn) {
            mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_FIRST_INDEX));
        }
        mUserHasPressed = false;
        if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter > 0) {
            mQuestionPosition++;
            updateQuestionsAndAnswers();
        } else if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter == 0) {
            FragmentManager manager = getSupportFragmentManager();
            DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.reward_dialog_title), getString(R.string.reward_dialog_message), getString(R.string.yes), getString(R.string.no), AppConstants.BUNDLE_KEY_REWARD_OPTION);
            dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
        } else {
            ActivityUtilities.getInstance().invokeScoreCardActivity(mActivity, ScoreCardActivity.class, mQuestionsCount, mScore, mWrongAns, mSkip, mCategoryId, mResultList, true);
            AppPreference.getInstance(mActivity).setQuizResult(mCategoryId, mScore);
        }
    }
//очищает списки ответов и цветов для их фона, и прокручивает список в начальную позицию.
// Далее заполняем списки ответами и фоновыми цветами, оповещаем адаптер списка ответов об изменении списка.
// Затем получаем текст вопроса и устанавливаем его в поле вопроса,
// а также прописваем в поле заголовка текущий номер вопроса из общего количества.
    public void updateQuestionsAndAnswers() {
        mOptionList.clear();
        mBackgroundColorList.clear();
        ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(AppConstants.BUNDLE_KEY_ZERO_INDEX);

        mOptionList.addAll(mItemList.get(mQuestionPosition).getAnswers());
        mBackgroundColorList.addAll(mItemList.get(mQuestionPosition).getBackgroundColors());
        mAdapter.notifyDataSetChanged();

        mQuestionText = mItemList.get(mQuestionPosition).getQuestion();

        tvQuestionText.setText(Html.fromHtml(mQuestionText));
        tvQuestionTitle.setText(getString(R.string.quiz_question_title, mQuestionPosition + 1, mQuestionsCount));
    }
//вызывается, если пользователь пытактся покинуть экран тестирование с помощью стрелки в тулбаре или
// системной кнопки «Назад». При этом пользователю отображается диалог для подтверждения намерения прекратить тестирование.
    public void quizActivityClosePrompt() {
        FragmentManager manager = getSupportFragmentManager();
        DialogUtilities dialog = DialogUtilities.newInstance(
                getString(R.string.exit),
                getString(R.string.cancel_prompt),
                getString(R.string.yes),
                getString(R.string.no),
                AppConstants.BUNDLE_KEY_CLOSE_OPTION);
        dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
    }
//считывает данные из файла вопросов и отдает их методу parseJson(),
// который наполняет этими данными списки и
// делает первый вызов метода updateQuestionsAndAnswers() для отображения вопроса
    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstants.QUESTION_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseJson(sb.toString());
    }

    public void parseJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObjMain.getJSONArray(AppConstants.JSON_KEY_QUESTIONNAIRY);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String question = jsonObj.getString(AppConstants.JSON_KEY_QUESTION);
                int correctAnswer = Integer.parseInt(jsonObj.getString(AppConstants.JSON_KEY_CORRECT_ANS));
                String categoryId = jsonObj.getString(AppConstants.JSON_KEY_CATEGORY_ID);

                Log.d("TAG", categoryId.toString());

                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstants.JSON_KEY_ANSWERS);
                ArrayList<String> contents = new ArrayList<>();
                ArrayList<String> backgroundColors = new ArrayList<>();
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String item_title = jsonArray2.get(j).toString();
                    contents.add(item_title);
                    backgroundColors.add(AppConstants.COLOR_WHITE);
                }
                if (mCategoryId.equals(categoryId)) {
                    mItemList.add(new QuizModel(question, contents, correctAnswer, categoryId, backgroundColors));
                }
            }

            mQuestionsCount = mItemList.size();
            Collections.shuffle(mItemList);

            hideLoader();
            updateQuestionsAndAnswers();

        } catch (JSONException e) {
            e.printStackTrace();
            showEmptyView();
        }
    }
//отображает и обрабатывает стрелку «Назад» в тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                quizActivityClosePrompt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//при нажатии системной кнопки «Назад» вызывает метод quizActivityClosePrompt(), рассмотренный выше.
    @Override
    public void onBackPressed() {
        quizActivityClosePrompt();
    }
//Он обрабатывает взаимодействие пользователя с диалоговыми окнами.
// Например, вызывает главный экран в диалоге выхода,
// считает пропущенные вопросы при переходе на следующий вопрос без ответа,
// создает список пропущенных вопросов с ответами, отображает рекламу,
// которую мы внедрим в последующих уроках, если пользователь выбирает дополнительную попытку,
// а также отображает экран результатов и сохраняет значения счетчиков в случае отказа пользователя смотреть рекламу.
    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstants.BUNDLE_KEY_CLOSE_OPTION)) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_SKIP_OPTION)) {
                mSkip++;
                mIsSkipped = true;
                mGivenAnsText = getResources().getString(R.string.skipped_text);
                mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());
                updateResultSet();
                setNextQuestion();
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
               //TODO:  mRewardedVideoAd.show();
            }
        } else if (!isOkPressed && viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
            ActivityUtilities.getInstance().invokeScoreCardActivity(mActivity, ScoreCardActivity.class, mQuestionsCount, mScore, mWrongAns, mSkip, mCategoryId, mResultList, true);
            AppPreference.getInstance(mContext).setQuizResult(mCategoryId, mScore);
            AppPreference.getInstance(mContext).setQuizQuestionsCount(mCategoryId, mQuestionsCount);
        }
    }
//для наполнения списка результатов
    public void updateResultSet() {
        mResultList.add(new ResultModel(mQuestionText, mGivenAnsText, mCorrectAnsText, mIsCorrect, mIsSkipped));
        mIsCorrect = false;
        mIsSkipped = false;
    }

// будет вызван в случае уничножения активити, нужно вызвать метод release() класса BeatBox
// для освобождения ресурсов медиаплеера.
// Предварительно нужно переопределить метод onDestroy() в классе BaseActivity.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }
}
