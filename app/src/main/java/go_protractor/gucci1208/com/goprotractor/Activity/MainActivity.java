package go_protractor.gucci1208.com.goprotractor.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import net.nend.android.NendAdInterstitial;

import java.io.IOException;

import go_protractor.gucci1208.com.goprotractor.R;
import go_protractor.gucci1208.com.goprotractor.Util.ImageUtil;
import go_protractor.gucci1208.com.goprotractor.Util.SettingUtil;
import go_protractor.gucci1208.com.goprotractor.Util.WrapperShared;
import go_protractor.gucci1208.com.goprotractor.View.DrawProtractor;
import go_protractor.gucci1208.com.goprotractor.View.MatrixImageView;

public class MainActivity extends Activity {
    private WrapperShared wrapperShared;

    private static final int RESULT_PICK_IMAGEFILE = 1001;

    private DrawProtractor drawProtractor;

    private NumberPicker numberPicker;

    private boolean isShowAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_main);

        wrapperShared = new WrapperShared(this);

        //ステータスバーの高さを取得
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        //分度器お絵描きクラス
        drawProtractor = new DrawProtractor(MainActivity.this);

        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
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
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(40);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0) {
                    ((MatrixImageView) findViewById(R.id.img_view)).setImageResource(R.drawable.protractor);
                } else {
                    ((MatrixImageView) findViewById(R.id.img_view)).setImageBitmap(drawProtractor.drawProtractor(newVal));
                }

                wrapperShared.saveInt(WrapperShared.KEY_TRAINER_LEVEL, newVal);
            }
        });

        //初期状態
        numberPicker.setValue(wrapperShared.getInt(WrapperShared.KEY_TRAINER_LEVEL, 1));
        if (numberPicker.getValue() > 0) {
            ((MatrixImageView) findViewById(R.id.img_view)).setImageBitmap(
                    drawProtractor.drawProtractor(numberPicker.getValue()));
        }
        numberPicker.setPadding(0, statusBarHeight, 0, 0);

        findViewById(R.id.ad_icon).setPadding(0, statusBarHeight, 0, 0);

        //分度器のレイヤーを出し入れするボタン
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingUtil.checkOverlayPermission(MainActivity.this)) {
                    startServiceProcess();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);

                    Toast.makeText(MainActivity.this, "アプリの表示を重ねる設定を有効にしてください",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, LayerService.class));
                NendAdInterstitial.showAd(MainActivity.this);
            }
        });

        //インタースティシャル広告を読み込む
        NendAdInterstitial.loadAd(getApplicationContext(), "bdde9fb68c3ab7e9f54398557e9cb54af0f70591", 634067);
        isShowAd = false;
    }

    private void startServiceProcess() {
        //すでにサービスを開始していたら停止
        stopService(new Intent(MainActivity.this, LayerService.class));

        //画面の要素を分度器だけ残して全て非表示にする
        findViewById(R.id.screenshot_image).setVisibility(View.INVISIBLE);
        findViewById(R.id.numPicker).setVisibility(View.INVISIBLE);
        findViewById(R.id.superimpose_layout).setVisibility(View.INVISIBLE);

        //一時的なものなのでpreferenceに保存する
        Bitmap explainCapture = ImageUtil.getViewCapture(findViewById(R.id.activity_layout));
        String bitmapStr = ImageUtil.calculateFileSize(explainCapture);
        wrapperShared.saveString(WrapperShared.KEY_VIEW_BITMAP, bitmapStr);

        Intent intent = new Intent(MainActivity.this, LayerService.class);
        startService(intent);

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Bitmap bmp = ImageUtil.getBitmapFromUri(this, uri);
                    ((ImageView) findViewById(R.id.screenshot_image)).setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
