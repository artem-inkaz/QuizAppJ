package ui.smartpro.quizappj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

import ui.smartpro.quizappj.activity.AboutDevActivity;
import ui.smartpro.quizappj.activity.BaseActivity;
import ui.smartpro.quizappj.activity.CustomUrlActivity;
import ui.smartpro.quizappj.utilities.ActivityUtilities;
import ui.smartpro.quizappj.utilities.AppUtilities;

public class MainActivity extends BaseActivity {
    //Объявим и инициализируем переменные активити и контекста
    private Activity activity;
    private Context context;

    private Toolbar toolbar;

    private AccountHeader header = null;
    private Drawer drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    ////Объявим и инициализируем переменные активити и контекста
        activity = MainActivity.this;
        context = getApplicationContext();

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
}