package go_protractor.gucci1208.com.goprotractor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import net.nend.android.NendAdInterstitial;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final int RESULT_PICK_IMAGEFILE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });

        //インタースティシャル広告
        NendAdInterstitial.loadAd(getApplicationContext(), "bdde9fb68c3ab7e9f54398557e9cb54af0f70591", 634067);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    ((ImageView) findViewById(R.id.screenshot_image)).setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onBackPressed() {
        //インタースティシャル広告表示
        NendAdInterstitial.showAd(this, new NendAdInterstitial.OnClickListener() {
            @Override
            public void onClick(NendAdInterstitial.NendAdInterstitialClickType clickType) {
                switch (clickType) {
                    case CLOSE:
                        // ×ボタンまたは範囲外タップ
                        finish();
                        break;
                    case DOWNLOAD:
                        // ダウンロードボタン
                        break;
                    case INFORMATION:
                        // インフォメーションボタン
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
