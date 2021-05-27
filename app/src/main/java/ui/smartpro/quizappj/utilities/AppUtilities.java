package ui.smartpro.quizappj.utilities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import ui.smartpro.quizappj.R;

public class AppUtilities {
    private static long backPressed = 0;
    //показ оповещений для пользователя
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    //будет отображать тост с предложением нажать кнопку назад еще раз для выхода
    public  static void tapPromtToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            activity.finish();
        } else {
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tap_again));
        }
        backPressed = System.currentTimeMillis();
    }
}
