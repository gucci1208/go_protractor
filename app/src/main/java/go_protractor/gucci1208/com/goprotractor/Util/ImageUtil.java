package go_protractor.gucci1208.com.goprotractor.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by rtaniguchi on 16/08/16.
 */
public class ImageUtil {
    /**
     * ギャラリーの画像のBitmapを取得するメソッド
     * @param context
     * @param uri ギャラリーで選択した画像のUri
     */
    static public Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * 【ユーザーによるカメラ機能使用時の処理関連】<br />
     * 表示しているViewの状態をそのままBitmapにする
     * @param view 撮りたいview
     * @return Bitmap 撮ったキャプチャ(Bitmap)
     */
    public static Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);

        // Viewのキャッシュを取得
        Bitmap cache = view.getDrawingCache();
        Bitmap screenShot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        return screenShot;
    }

    /**
     * 【ユーザーによるカメラ機能使用時の処理関連】<br />
     * Bitmapをbyte配列に変換してStringにして返す
     * @param bitmap 変換する元となるBitmap
     * @return
     */
    public static String calculateFileSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);
    }

}
