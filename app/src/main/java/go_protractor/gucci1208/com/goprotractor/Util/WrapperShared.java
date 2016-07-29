package go_protractor.gucci1208.com.goprotractor.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class WrapperShared {
    Context context;

    SharedPreferences pref;
    final String FILE_NAME = "file_name";

    public static final String KEY_TRAINER_LEVEL = "trainer_level";

    public WrapperShared(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    // データの保存
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // データの取得
    public int getInt(String key, int default_value) {
        return pref.getInt(key, default_value);
    }

    public String getString(String key, String default_value) {
        return pref.getString(key, default_value);
    }

    public boolean getBoolean(String key, boolean default_value) {
        return pref.getBoolean(key, default_value);
    }
}