package cc.pchospital.app;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static int userId;
    private static String userName;
    private static String userPhone;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserPhone() {
        return userPhone;
    }

    public static void setUserId(int userId) {
        MyApplication.userId = userId;
    }

    public static void setUserName(String userName) {
        MyApplication.userName = userName;
    }

    public static void setUserPhone(String userPhone) {
        MyApplication.userPhone = userPhone;
    }
}
