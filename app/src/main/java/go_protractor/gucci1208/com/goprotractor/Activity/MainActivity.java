package go_protractor.gucci1208.com.goprotractor.Activity;

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
import android.widget.NumberPicker;

import net.nend.android.NendAdInterstitial;

import java.io.FileDescriptor;
import java.io.IOException;

import go_protractor.gucci1208.com.goprotractor.R;
import go_protractor.gucci1208.com.goprotractor.Util.WrapperShared;
import go_protractor.gucci1208.com.goprotractor.View.DrawProtractor;
import go_protractor.gucci1208.com.goprotractor.View.MatrixImageView;

public class MainActivity extends Activity {
    private WrapperShared wrapperShared;

    private static final int RESULT_PICK_IMAGEFILE = 1001;

    private DrawProtractor drawProtractor;

    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        wrapperShared = new WrapperShared(this);

        //分度器お絵描きクラス
        drawProtractor = new DrawProtractor(MainActivity.this);

        ((Button) findViewById(R.id.load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });

        // 値が変化した時に通知を受け取るリスナーを登録する
        numberPicker = (NumberPicker)findViewById(R.id.numPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(40);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ((MatrixImageView) findViewById(R.id.img_view)).setImageBitmap(drawProtractor.drawProtractor(newVal));

                wrapperShared.saveInt(WrapperShared.KEY_TRAINER_LEVEL, newVal);
            }
        });

        //初期状態
        numberPicker.setValue(wrapperShared.getInt(WrapperShared.KEY_TRAINER_LEVEL, 1));
        ((MatrixImageView) findViewById(R.id.img_view)).setImageBitmap(
                drawProtractor.drawProtractor(numberPicker.getValue()));

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
