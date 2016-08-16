package go_protractor.gucci1208.com.goprotractor.Util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by rtaniguchi on 16/08/16.
 */
public class SettingUtil {
    /**
     * Androidのバージョンによってパーミッションの扱いが違うので、その設定を取得するメソッド
     * @return trueなら設定が有効、falseなら無効
     */
    static public boolean checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return Settings.canDrawOverlays(context);
    }
}
