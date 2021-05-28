package ui.smartpro.quizappj.utilities;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.models.ResultModel;

public class ActivityUtilities {

    private static ActivityUtilities activityUtilities = null;

    public static ActivityUtilities getInstance() {
        if (activityUtilities == null) {
            activityUtilities = new ActivityUtilities();
        }
        return activityUtilities;
    }

//будет создержать интент для запуска нового экрана. Метод принимает контекст, вызываемое активити,
// а также логический параметр для указания необходимости принудительно закрыть активити,
// из которого происходит вызов
    public void invokeNewActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }
//для вызова экрана CustomUrlActivity
    //создаем интент с передачей ему заголовка и веб адреса, и запускаем его
    public void invokeCustomUrlActivity(Activity activity, Class<?> tClass, String pageTitle, String pageUrl, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, pageTitle);
        intent.putExtra(AppConstants.BUNDLE_KEY_URL, pageUrl);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }
//Метод создает интент для вызова активити и передает в интент номер теста.
    public void invokeCommonQuizActivity(Activity activity, Class<?> tClass, String categoryId, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstants.BUNDLE_KEY_INDEX, categoryId);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }
//Для вызова экрана результатов
    public void invokeScoreCardActivity(Activity activity, Class<?> tClass, int questionsCount, int score, int wrongAns, int skip, String categoryId, ArrayList<ResultModel> resultList, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstants.BUNDLE_KEY_SCORE, score);
        intent.putExtra(AppConstants.QUESTIONS_IN_TEST, questionsCount);
        intent.putExtra(AppConstants.BUNDLE_KEY_WRONG_ANS, wrongAns);
        intent.putExtra(AppConstants.BUNDLE_KEY_SKIP, skip);
        intent.putExtra(AppConstants.BUNDLE_KEY_INDEX, categoryId);
        intent.putParcelableArrayListExtra(AppConstants.BUNDLE_KEY_ITEM, resultList);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }
}
