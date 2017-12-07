package cc.pchospital.app;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cc.pchospital.app.db.LoginTask;
import cc.pchospital.app.gson.User;

public class LoginActivity extends AppCompatActivity {
    public User user;

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
        // 根据调用方决定是否显示语言按钮
        Intent intent = getIntent();
        String extra = intent.getStringExtra(getString(R.string.app_intent_extra_settings));
        if (extra != null) {
            language.setVisibility(View.GONE);
        }

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
}
