package cc.pchospital.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.List;
import cc.pchospital.app.gson.User;
import cc.pchospital.app.util.HttpUtil;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_arrow_back_black);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user = new User();
        Button language = (Button) findViewById(R.id.language);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra(getString(R.string.app_intent_extra_login),
                        getString(R.string.app_intent_extra_login_language));
                setResult(RESULT_CANCELED, it);
                finish();
            }
        });
        final Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 输入校验
                TextInputEditText inputname = (TextInputEditText) findViewById(R.id.input_name);
                String uname = inputname.getText().toString();
                TextInputEditText inputphone = (TextInputEditText) findViewById(R.id.input_phone);
                String uphone = inputphone.getText().toString();
                if (uname.length() > 40){
                    inputname.setError(getString(R.string.error_login_name_too_long));
                    return;
                } else if (uname.length() == 0) {
                    inputname.setError(getString(R.string.error_login_no_name));
                    return;
                } else if (uphone.length() != 11) {
                    inputphone.setError(getString(R.string.error_login_invalid_phone_number));
                    return;
                }
                // 登录或注册
                login.setEnabled(false);
                StringBuilder url = new StringBuilder();
                url.append("http://");
                url.append(getString(R.string.app_network_server_ip));
                url.append("/");
                url.append(getString(R.string.app_network_login_page));
                url.append("?");
                url.append(getString(R.string.app_db_user_uname));
                url.append("=");
                url.append(uname);
                url.append("&");
                url.append(getString(R.string.app_db_user_uphone));
                url.append("=");
                url.append(uphone);
                new LoginTask(LoginActivity.this).execute(url.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra(getString(R.string.app_intent_extra_login),
                getString(R.string.app_intent_extra_login_back));
        setResult(RESULT_CANCELED, it);
        finish();
    }

    private static class LoginTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<LoginActivity> activity;

        LoginTask(LoginActivity context) {
            activity = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button login = activity.get().findViewById(R.id.login);
            login.setText(activity.get().getString(R.string.states_login_signing_in));
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Response response = HttpUtil.sendGetOkHttpRequest(strings[0]);
                String responseData = response.body().string();
                Gson gson = new Gson();
                List<User> users = gson.fromJson(responseData,
                        new TypeToken<List<User>>(){}.getType());
                activity.get().user = users.get(0);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            LoginActivity loginActivity = activity.get();
            if (aBoolean) {
                User user = loginActivity.user;
                SharedPreferences.Editor editor =
                        loginActivity.getSharedPreferences(loginActivity.getString(R.string.app_local_user_profile_filename),
                        MODE_PRIVATE).edit();
                editor.putInt(loginActivity.getString(R.string.app_db_user_uid), user.getUid());
                editor.putString(loginActivity.getString(R.string.app_db_user_uname), user.getUname());
                editor.putString(loginActivity.getString(R.string.app_db_user_uphone), user.getUphone());
                editor.apply();
                loginActivity.setResult(RESULT_OK);
                loginActivity.finish();
            } else {
                Toast.makeText(loginActivity,
                        loginActivity.getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show();
            }
            Button login = loginActivity.findViewById(R.id.login);
            login.setText(loginActivity.getString((R.string.button_login_login_or_register)));
            login.setEnabled(true);
        }
    }
}
