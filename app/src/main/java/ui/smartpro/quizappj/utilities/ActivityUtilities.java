package ui.smartpro.quizappj.utilities;

import android.app.Activity;
import android.content.Intent;

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
}
