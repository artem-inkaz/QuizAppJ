package ui.smartpro.quizappj.utilities;

import android.app.Activity;
import android.content.Intent;

import ui.smartpro.quizappj.constants.AppConstants;

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
}
