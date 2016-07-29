package go_protractor.gucci1208.com.goprotractor.View;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DrawProtractor {
    DisplayMetrics displayMetrics;
    Bitmap mLastDrawBitmap;
    Canvas canvas;
    int widthPixels;

    double[] CpM = {0.0940000, 0.1351374, 0.1663979, 0.1926509, 0.2157325, 0.2365727, 0.2557201, 0.2735304, 0.2902499, 0.3060574, 0.3210876, 0.3354450, 0.3492127, 0.3624578, 0.3752356, 0.3875924, 0.3995673, 0.4111936, 0.4225000, 0.4335117, 0.4431076, 0.4530600, 0.4627984, 0.4723361, 0.4816850, 0.4908558, 0.4998584, 0.5087018, 0.5173940, 0.5259425, 0.5343543, 0.5426358, 0.5507927, 0.5588306, 0.5667545, 0.5745692, 0.5822789, 0.5898879, 0.5974000, 0.6048188, 0.6121573, 0.6194041, 0.6265671, 0.6336492, 0.6406530, 0.6475810, 0.6544356, 0.6612193, 0.6679340, 0.6745819, 0.6811649, 0.6876849, 0.6941437, 0.7005429, 0.7068842, 0.7131691, 0.7193991, 0.7255756, 0.7317000, 0.7347410, 0.7377695, 0.7407856, 0.7437894, 0.7467812, 0.7497610, 0.7527291, 0.7556855, 0.7586304, 0.7615638, 0.7644861, 0.7673972, 0.7702973, 0.7731865, 0.7760650, 0.7789328, 0.7817901, 0.7846370, 0.7874736, 0.7903000, 0.7931164};

    public DrawProtractor(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        mLastDrawBitmap = Bitmap.createBitmap(
                displayMetrics.widthPixels, displayMetrics.widthPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mLastDrawBitmap);

        widthPixels = displayMetrics.widthPixels;
    }

    public Bitmap drawProtractor(int trainerLevel) {
        // 初期化
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // 線の太さ
        int lineWidth = 10;

        //色のセット
        Paint paint = new Paint();
        paint.setColor(0xffa4c639);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(lineWidth);

        // 円の枠を描く
        RectF rectf = new RectF(lineWidth, lineWidth,
                widthPixels - (lineWidth * 2),
                widthPixels - (lineWidth * 2));
        canvas.drawArc(rectf, 180, 180, false, paint);

        //円の中心の座標
        int centerX = widthPixels / 2 - lineWidth / 2;
        int centerY = widthPixels / 2 - lineWidth;

        //円の半径
        int radius = widthPixels / 2 - (lineWidth * 2);
        int radius2;

        //描画する角度の数
        int drawAngleNum = Math.min((trainerLevel) * 2 + 2, 79);

        lineWidth = 6;
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(lineWidth);

        //Javascriptと計算結果が違いすぎるので苦肉の策
        int calc1 = 5;
        int calc2 = -1;
        for (int i = 0; i < drawAngleNum; i++) {
            calc1 += 5;
            calc2++;

            if (calc1 % 100 == 0) {
                radius2 = radius - 140;
            } else {
                if (calc2 % 2 == 0) {
                    radius2 = radius - 80;
                } else {
                    radius2 = radius - 40;
                }
            }

            //角度
            double degree = ((CpM[i] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
            if (degree > 180) {
                //180度を超えちゃったら描画しない
                continue;
            }

            //距離と角度から外円の座標を求める
            double x2 = radius * Math.cos(Math.PI / 180 * (degree + 180)) + centerX;
            double y2 = radius * Math.sin(Math.PI / 180 * (degree + 180)) + centerY;

            //距離と角度から内円の座標を求める
            double x3 = radius2 * Math.cos(Math.PI / 180 * (degree + 180)) + centerX;
            double y3 = radius2 * Math.sin(Math.PI / 180 * (degree + 180)) + centerY;
            canvas.drawLine((float) x2, (float) y2, (float) x3, (float) y3, paint);
        }

        return mLastDrawBitmap;
    }
}