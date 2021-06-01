package ui.smartpro.quizappj.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ui.smartpro.quizappj.R;
import ui.smartpro.quizappj.adapters.CategoryAdapter;
import ui.smartpro.quizappj.constants.AppConstants;
import ui.smartpro.quizappj.data.sqlite.NotificationDbController;
import ui.smartpro.quizappj.listeners.ListitemClickListener;
import ui.smartpro.quizappj.models.notification.NotificationModel;
import ui.smartpro.quizappj.models.quiz.CategoryModel;
import ui.smartpro.quizappj.utilities.ActivityUtilities;
import ui.smartpro.quizappj.utilities.AppUtilities;

public class MainActivity extends BaseActivity {
    //Объявим и инициализируем переменные активити и контекста
    private Activity activity;
    private Context context;

    private Toolbar toolbar;

    private RelativeLayout mNotificationView;
    private AccountHeader header = null;
    private Drawer drawer = null;

    private ArrayList<CategoryModel> categoryList;
    private CategoryAdapter adapter = null;
    private RecyclerView recyclerView;

    //инициализируем компонеты и приступаем к реализации панели навигации
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    //Объявим и инициализируем переменные активити и контекста
        activity = MainActivity.this;
        context = getApplicationContext();
    // инициализация адаптера
        recyclerView = (RecyclerView) findViewById(R.id.rvContent);
        //Корневому макету разметки списка присваиваем GridLayoutManager, который определяет
        // расположение элементов списка в виде сетки, указываем число столбцов — 2
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(context, activity, categoryList);
        recyclerView.setAdapter(adapter);

        initLoader();
        loadData();
        initListener();

        //Сначала создаем профайл пользователя — здесь будет просто иконка приложения,
        // поскольку авторизация в нашем приложении не используется
        final IProfile profile = new ProfileDrawerItem().withIcon(R.drawable.ic_dev);
        //создаем хедер
        //методы добавляют привязку к активити, устанавливают прозрачность статусбара, добавляют
        // фоновую картинку и слушатель клика по профилю пользователя.
        // В этом слушателе мы потом пропишем вызов CustomUrlActivity для открытия ссылки на сайт
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        ActivityUtilities.getInstance().invokeCustomUrlActivity(activity, CustomUrlActivity.class,
                                getResources().getString(R.string.site), getResources().getString(R.string.site_url), false);
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .addProfiles(profile)
                .build();
//Привязываем панель к активити, указываем тулбар и хедер, а далее методом addDrawerItems()
// добавляем пункты меню. Для кажого пункта меню нужно указать заголовок, иконку, идентификатор,
// по которому мы будет определять, какой пункт меню нажат,
// а также параметр выделения цветом нажатого пункта меню.
// Группы пунктов меню отделим друг от друга разделителями.
// Конечно, не совсем правильно писать текст для заголовков пунктов меню прямо в коде.
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(header)
                //добавляем пункты меню
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("О приложении").withIcon(R.drawable.ic_dev).withIdentifier(10).withSelectable(false),

                        new SecondaryDrawerItem().withName("YouTube").withIcon(R.drawable.ic_youtube).withIdentifier(20).withSelectable(false),
                        new SecondaryDrawerItem().withName("Facebook").withIcon(R.drawable.ic_facebook).withIdentifier(21).withSelectable(false),
                        new SecondaryDrawerItem().withName("Twitter").withIcon(R.drawable.ic_twitter).withIdentifier(22).withSelectable(false),
                        new SecondaryDrawerItem().withName("Google+").withIcon(R.drawable.ic_google_plus).withIdentifier(23).withSelectable(false),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Настройки").withIcon(R.drawable.ic_settings).withIdentifier(30).withSelectable(false),
                        new SecondaryDrawerItem().withName("Оцените приложение").withIcon(R.drawable.ic_rating).withIdentifier(31).withSelectable(false),
                        new SecondaryDrawerItem().withName("Поделитесь").withIcon(R.drawable.ic_share).withIdentifier(32).withSelectable(false),
                        new SecondaryDrawerItem().withName("Соглашения").withIcon(R.drawable.ic_privacy_policy).withIdentifier(33).withSelectable(false),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Выход").withIcon(R.drawable.ic_exit).withIdentifier(40).withSelectable(false)


                )
                //слушатель нажатия пунктов меню, где мы будем вызывать другие экраны.
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                //добавим обработку нажатия пунктов меню панели навигации.
                        // Нажатые пункты будем определять по идентификаторам в условной конструкции.
                        // Пропишем соответствующие методы для вызова страниц соцсетей, шеринга и
                        // рейтинга приложения, а также документа с соглашениями.
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 10) {
                                ActivityUtilities.getInstance().invokeNewActivity(activity, AboutDevActivity.class, false);

                            } else if (drawerItem.getIdentifier() == 20) {
                                AppUtilities.youtubeLink(activity);
                            } else if (drawerItem.getIdentifier() == 21) {
                                AppUtilities.facebookLink(activity);
                            } else if (drawerItem.getIdentifier() == 22) {
                                AppUtilities.twitterLink(activity);
                            } else if (drawerItem.getIdentifier() == 23) {
                                AppUtilities.googlePlusLink(activity);
                            } else if (drawerItem.getIdentifier() == 30) {
                                // TODO: invoke SettingActivity
                            } else if (drawerItem.getIdentifier() == 31) {
                                AppUtilities.rateThisApp(activity);
                            } else if (drawerItem.getIdentifier() == 32) {
                                AppUtilities.shareApp(activity);
                            } else if (drawerItem.getIdentifier() == 33) {
                                ActivityUtilities.getInstance().invokeCustomUrlActivity(activity, CustomUrlActivity.class,
                                        getResources().getString(R.string.privacy), getResources().getString(R.string.privacy_url), false);
                            } else if (drawerItem.getIdentifier() == 40) {

                            }
                        }

                        return false;
                    }
                })
                //сохраняет состояние дровера
                .withSavedInstance(savedInstanceState)
                //регулирует возможность принудительного открытия панели навигации при первом запуске приложения
                .withShowDrawerOnFirstLaunch(true)
                //определяет отображение дровера при свайпе
                .withShowDrawerUntilDraggedOpened(true)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            AppUtilities.tapPromtToExit(this);
        }
    }

    private void loadData() {
        showLoader();
        loadJson();
    }
//загружает json файл со списком тестов по пути, указанном в константе
    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try{
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstants.CONTENT_FILE)));
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
//разбираем файл, считываем данные и заполняем ними список тестов,
// а затем методом notifyDataSetChanged() информируем адаптер о том,
// что набор данных изменился.
//Метод loadData() отображает процесс загрузки и вызывает метод loadJson().
    private void parseJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray(AppConstants.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String categoryId = object.getString(AppConstants.JSON_KEY_CATEGORY_ID);
                String categoryName = object.getString(AppConstants.JSON_KEY_CATEGORY_NAME);

                categoryList.add(new CategoryModel(categoryId, categoryName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideLoader();
        adapter.notifyDataSetChanged();
    }

    private void initListener() {

        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //добавим обработку нажатия значка уведомлений в тулбаре
                ActivityUtilities.getInstance().invokeNewActivity(activity, NotificationListActivity.class, false);
            }
        });

        // recycler list item click listener
        adapter.setItemClickListener(new ListitemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                CategoryModel model = categoryList.get(position);
                ActivityUtilities.getInstance().invokeCommonQuizActivity(activity, QuizPromptActivity.class, model.getCategoryId(), true);
            }
        });
    }

    // received new broadcast
    // получает широковещательное сообщение и вызывает метод initNotification()
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };
    //Считывает из БД новые непрочитанные уведомления и управляет отображением счетчика
    // непрочитанных уведомлений
    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(context);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    //регистрируем широковещательный приемник и вызываем метод initNotification()
        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstants.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

        // load full screen ad
        // TODO:  AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }
}