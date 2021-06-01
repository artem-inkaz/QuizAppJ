package ui.smartpro.quizappj.notification;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.data.sqlite.NotificationDbController;

//сервис, который будет постоянно работать в фоне, чтобы отслеживать приходящие уведомления.
public class MyFirebaseMessagingService extends FirebaseMessagingService {


//срабатывает по приходу уведомления и передает данные уведомления в метод sendNotification(),
// который сохраняет его в БД.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("FCMess", "onMessageReceived: " + remoteMessage);


        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();

            sendNotification(params.get("title"), params.get("message"), params.get("url"));
            broadcastNewNotification();
        }
    }


    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d("FCMess", "onMessageSent: " + s);
    }


    private void sendNotification(String title, String messageBody, String url) {

        // insert data into database
        NotificationDbController notificationDbController = new NotificationDbController(MyFirebaseMessagingService.this);
        notificationDbController.insertData(title, messageBody, url);

    }
    //который создает и отправляет широковещательное сообщение об уведомлении.
    private void broadcastNewNotification() {
        Intent intent = new Intent(AppConstants.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

}
