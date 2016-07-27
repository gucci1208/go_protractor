package go_protractor.gucci1208.com.goprotractor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private static final int IMAGE_CHOOSER_RESULTCODE = 1001;
    private Uri mPictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ギャラリーから選択
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);

                // カメラで撮影
                String filename = System.currentTimeMillis() + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                mPictureUri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i2.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

                // ギャラリー選択のIntentでcreateChooser()
                Intent chooserIntent = Intent.createChooser(i, "Pick Image");
                // EXTRA_INITIAL_INTENTS にカメラ撮影のIntentを追加
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { i2 });

                startActivityForResult(chooserIntent, IMAGE_CHOOSER_RESULTCODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CHOOSER_RESULTCODE) {

            if (resultCode != RESULT_OK) {
                if (mPictureUri != null) {
                    getContentResolver().delete(mPictureUri, null, null);
                    mPictureUri = null;
                }
                return;
            }

            // 画像を取得
            Uri result = (data == null) ? mPictureUri : data.getData();
            ImageView iv = (ImageView) findViewById(R.id.screenshot_image);
            iv.setImageURI(result);

            mPictureUri = null;
        }
    }
}
