package go_protractor.gucci1208.com.goprotractor.Activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import go_protractor.gucci1208.com.goprotractor.R;
import go_protractor.gucci1208.com.goprotractor.Util.WrapperShared;

public class LayerService extends Service {
    View view;
    WindowManager wm;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Viewからインフレータを作成する
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        // 重ね合わせするViewの設定を行う
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        // WindowManagerを取得する
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // レイアウトファイルから重ね合わせするViewを作成する
        view = layoutInflater.inflate(R.layout.overlay, null);

        //ステータスバーの高さを取得
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        //受け取ったbitmapをセットする
        WrapperShared wrapperShared = new WrapperShared(this);
        String bitmapStr = wrapperShared.getString(WrapperShared.KEY_VIEW_BITMAP, "");
        if (!bitmapStr.equals("")) {
            byte[] b = Base64.decode(bitmapStr, Base64.DEFAULT);
            Bitmap bitmapCamera = BitmapFactory.decodeByteArray(b, 0, b.length).copy(Bitmap.Config.ARGB_8888, true);
            //ステータスバー分だけ下げないとなんかずれちゃう
            view.findViewById(R.id.screenshot_image).setPadding(0, statusBarHeight, 0, 0);
            ((ImageView)view.findViewById(R.id.screenshot_image)).setImageBitmap(bitmapCamera);

            // Viewを画面上に重ね合わせする
            wm.addView(view, params);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // サービスが破棄されるときには重ね合わせしていたViewを削除する
        wm.removeView(view);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
