package cc.pchospital.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TicketActivity extends AppCompatActivity {

    public static final int TYPE_LOGIN = 1;

    private DrawerLayout mDrawerLayout;

    private static final String TAG = "TicketActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // UI-Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_menu);
            actionBar.setTitle(R.string.title_your_ticket);
        }

        // UI-NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        navigationView.setCheckedItem(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_main:
                        mDrawerLayout.closeDrawers();
                        actionBar.setTitle(R.string.title_your_ticket);
                        replaceFragment(new MainFragment());
                        break;
                    case R.id.nav_about:
                        mDrawerLayout.closeDrawers();
                        actionBar.setTitle(R.string.title_about);
                        replaceFragment(new AboutFragment());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // UI-ChangeLanguage
        Switch language = (Switch) findViewById(R.id.nav_language);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        if (config.locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            language.setChecked(true);
        } else {
            language.setChecked(false);
        }
        language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                DisplayMetrics metrics = resources.getDisplayMetrics();
                if (config.locale.equals(Locale.SIMPLIFIED_CHINESE))
                    config.locale = Locale.ENGLISH;
                else
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                resources.updateConfiguration(config, metrics);
                restart();
            }
        });

        // 自动登录，如果没有自动登录信息则跳转到登录界面
        SharedPreferences userProfile =
                getSharedPreferences(getString(R.string.app_local_user_profile_filename),
                        MODE_PRIVATE);
        int userid = userProfile.getInt(getString(R.string.app_db_user_uid), -1);
        if (userid == -1) {
            Intent intent = new Intent(TicketActivity.this, LoginActivity.class);
            Toast.makeText(this, getString(R.string.toast_ticket_need_login),
                    Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, TYPE_LOGIN);
        } else {
            String userName = userProfile.getString(getString(R.string.app_db_user_uname),
                    null);
            String userPhone = userProfile.getString(getString(R.string.app_db_user_uphone),
                    null);
            // 写入用户信息到全局变量中
            MyApplication.setUserId(userid);
            MyApplication.setUserName(userName);
            MyApplication.setUserPhone(userPhone);
            // UI-NavigationHead
            TextView uName = (TextView) navigationView.getHeaderView(0).
                    findViewById(R.id.nav_username);
            uName.setText(MyApplication.getUserName());
            TextView uPhone = (TextView) navigationView.getHeaderView(0).
                    findViewById(R.id.nav_phone);
            uPhone.setText(MyApplication.getUserPhone());
        }

        // 启动主 Fragment
        replaceFragment(new MainFragment());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TYPE_LOGIN:
                if (resultCode == RESULT_OK) {
                    SharedPreferences userProfile =
                            getSharedPreferences(
                                    getString(R.string.app_local_user_profile_filename),
                                    MODE_PRIVATE);
                    int userid = userProfile.getInt(getString(R.string.app_db_user_uid),
                            -1);
                    if (userid == -1) {
                        Log.e(TAG, "onActivityResult: Unexpected Error!");
                        finish();
                    }
                    String userName = userProfile.getString(getString(R.string.app_db_user_uname),
                            null);
                    String userPhone = userProfile.getString(getString(R.string.app_db_user_uphone),
                            null);
                    // 写入用户信息到全局变量中
                    MyApplication.setUserId(userid);
                    MyApplication.setUserName(userName);
                    MyApplication.setUserPhone(userPhone);
                    // UI-NavigationHead
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    TextView uName = (TextView) navigationView.getHeaderView(0)
                            .findViewById(R.id.nav_username);
                    uName.setText(MyApplication.getUserName());
                    TextView uPhone = (TextView) navigationView.getHeaderView(0)
                            .findViewById(R.id.nav_phone);
                    uPhone.setText(MyApplication.getUserPhone());
                } else if (resultCode == RESULT_CANCELED) {
                    String resultdata = data.getStringExtra
                            (getString(R.string.app_intent_extra_login));
                    if (resultdata.equals
                            (getString(R.string.app_intent_extra_login_language))) {
                        Resources resources = getResources();
                        Configuration config = resources.getConfiguration();
                        DisplayMetrics metrics = resources.getDisplayMetrics();
                        if (config.locale.equals(Locale.SIMPLIFIED_CHINESE))
                            config.locale = Locale.ENGLISH;
                        else
                            config.locale = Locale.SIMPLIFIED_CHINESE;
                        resources.updateConfiguration(config, metrics);
                        restart();
                    } else {
                        finish();
                    }
                }
                break;
                default:
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.dynamic_content, fragment);
        transaction.commit();
    }

    private void restart() {
        finish();
        Intent it = new Intent(TicketActivity.this, TicketActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }
}
