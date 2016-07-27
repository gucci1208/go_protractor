package go_protractor.gucci1208.com.goprotractor;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MatrixImageView extends ImageView implements View.OnTouchListener {
    //繰り返し処理関連
    private int interval = 20;
    private Handler handler = new Handler();
    private Runnable runnable;
    // 移動、回転、ズーム用の変換行列
    private Matrix matrix = new Matrix();
    //ズーム関連
    private float oldDist = 0f;
    private PointF mid = new PointF();
    private float curRatio = 1f;
    //慣性移動に関するもの
    private boolean inertial = true;
    private float speedDecRatio = 0.8f;
    private float angleSpeedDecRatio = 0.8f;
    private PointF previous = new PointF();
    private PointF speed = new PointF();
    private float angleSpeed = 0;
    //回転に関するもの
    private Line previousLine;

    //モード判別（NONE: 未操作状態, ONE_POINT: ドラッグ中, TWO_POINT: 拡大縮小、回転中）
    enum Mode {
        NONE,
        ONE_POINT,
        TWO_POINT
    }

    private Mode mode = Mode.NONE;

    public MatrixImageView(Context context) {
        this(context, null);
    }

    public MatrixImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
//ScaleTypeは必ずMATRIXを設定
        setScaleType(ScaleType.MATRIX);
        if (attrs != null) {
            interval = attrs.getAttributeIntValue(null, "interval", 20);
            speedDecRatio = attrs.getAttributeFloatValue(null, "speedDecRatio", 0.8f);
            angleSpeedDecRatio = attrs.getAttributeFloatValue(null, "angleSpeedDecRatio", 0.8f);
            inertial = attrs.getAttributeBooleanValue(null, "inertial", true);
        }
        setOnTouchListener(this);
        if (inertial) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    redraw();
                    handler.postDelayed(this, interval);
                }
            };
            handler.postDelayed(runnable, interval);
        }
    }

    /**
     * 慣性効果の処理
     */
    private void redraw() {
        if (mode == Mode.NONE) {
//移動、回転の慣性反映
            previous.set(previous.x + speed.x, previous.y + speed.y);
            matrix.postTranslate(speed.x, speed.y);
            matrix.postRotate(angleSpeed, previous.x, previous.y);
//以下、次回の慣性のための移動スピード、回転スピード軽減処理
//移動による慣性
            speed.set(speed.x * speedDecRatio, speed.y * speedDecRatio);
//慣性の移動量が十分に小さくなったときは停止
            if (-1 < speed.x && speed.x < 1) {
                speed.x = 0;
            }
            if (-1 < speed.y && speed.y < 1) {
                speed.y = 0;
            }
//回転による慣性
            angleSpeed = angleSpeed * angleSpeedDecRatio;
//慣性による回転量が十分に小さくなったときは停止
            if (-1 < angleSpeed && angleSpeed < 1) {
                angleSpeed = 0;
            }
            setImageMatrix(matrix);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//現在の慣性をリセット
                speed.set(0, 0);
                angleSpeed = 0;
//移動（ズームは無し）開始
                previous.set(event.getX(), event.getY());
                mode = Mode.ONE_POINT;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
//移動・回転・ズーム開始
                previous.set(event.getX(), event.getY());
                oldDist = spacing(event);
// Android のポジション誤検知を無視
                if (oldDist > 10f) {
                    midPoint(previous, event);
                    mode = Mode.TWO_POINT;
                    previousLine = new Line(
                            new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1))
                    );
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = Mode.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                PointF current = null;
                if (mode == Mode.ONE_POINT) {
                    current = new PointF(event.getX(), event.getY());
                } else if (mode == Mode.TWO_POINT) {
                    current = new PointF();
                    midPoint(current, event);
                } else {
                    return false;
                }
//移動処理
                float distanceX = current.x - previous.x;
                float distanceY = current.y - previous.y;
                matrix.postTranslate(distanceX, distanceY);
                if (mode == Mode.TWO_POINT) {
//ズーム処理
                    float newDist = spacing(event);
                    midPoint(mid, event);
                    float scale = newDist / oldDist;
                    float tempRatio = curRatio * scale;
                    oldDist = newDist;
//倍率が上限値下限値の範囲外なら補正する
                    curRatio = Math.min(Math.max(0.1f, curRatio), 20f);
                    if (0.1f < tempRatio && tempRatio < 20f) {
                        curRatio = tempRatio;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
//回転処理
                    Line line = new Line(
                            new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1))
                    );
                    float angle = (float) (previousLine.getAngle(line) * 180 / Math.PI);
                    matrix.postRotate(angle, current.x, current.y);
//次回の準備
                    angleSpeed = angle;
                    previousLine = line;
                }
//次回の準備
                speed = new PointF(current.x - previous.x, current.y - previous.y);
                previous.set(current.x, current.y);
        }
// 変換の実行
        view.setImageMatrix(matrix);
        return true; // イベントがハンドリングされたことを示す
    }

    /**
     * 2点間の距離を計算
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 2点間の中間点を計算
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    static class Line {
        enum LineType {
            /**
             * 第1引数の点と第2引数の点を通る直線（終端はない）
             */
            STRAIGHT,
            /**
             * 第1引数の点から第2引数のほうに伸びる半直線
             */
            HALF,
            /**
             * 第1引数の点と第2引数の点の間の線分
             */
            SEGMENT
        }

        public PointF p1;
        public PointF p2;
        public LineType type;

        /**
         * @param p1
         * @param p2
         */
        public Line(PointF p1, PointF p2) {
            this(p1, p2, null);
        }

        /**
         * @param p1
         * @param p2
         * @param type
         */
        public Line(PointF p1, PointF p2, LineType type) {
            this.p1 = p1;
            this.p2 = p2;
            this.type = (type == null) ? LineType.STRAIGHT : type;
        }

        /**
         * 2つのLineインスタンスの交点を表わすPointインスタンスを取得する
         * 交点がない場合はnullを返す
         *
         * @param line
         * @return
         */
        public PointF getIntersectionPoint(Line line) {
            PointF vector1 = this.getVector();
            PointF vector2 = line.getVector();
            if (cross(vector1, vector2) == 0.0) {
//2直線が並行の場合はnullを返す
                return null;
            }
// 交点を this.p1 + s * vector1 としたとき
            float s = cross(vector2, subtract(line.p1, this.p1)) / cross(vector2, vector1);
// 交点を line.p1 + t * vector2 としたとき
            float t = cross(vector1, subtract(this.p1, line.p1)) / cross(vector1, vector2);
            if (this.validateIntersect(s) && line.validateIntersect(t)) {
                vector1.x *= s;
                vector1.y *= s;
                this.p1.set(p1.x + vector1.x, p1.y + vector1.y);
                return p1;
            } else {
                return null;
            }
        }

        /**
         * 2つのLineインスタンスが作る角度のラジアン値を返す
         *
         * @param line
         * @return
         */
        public float getAngle(Line line) {
            PointF vector1 = this.getVector();
            PointF vector2 = line.getVector();
            return (float) Math.atan2(vector1.x * vector2.y - vector1.y * vector2.x, vector1.x * vector2.x + vector1.y * vector2.y);
        }

        public PointF getVector() {
            return new PointF(p2.x - p1.x, p2.y - p1.y);
        }

        public PointF subtract(PointF p1, PointF p2) {
            return new PointF(p1.x - p2.x, p1.y - p2.y);
        }

        /**
         * 交点までのベクトルを p1 + n * (p2 - p1) であらわしたとき、
         * nが適切な値の範囲内かどうかを判定する。
         * <p/>
         * 直線の場合：nはどの値でもよい
         * 半直線の場合：nは0以上である必要がある
         * 線分の場合：nは0以上1以下である必要がある
         *
         * @param n
         * @return
         */
        private boolean validateIntersect(float n) {
            if (LineType.HALF.equals(this.type)) {
                return (0 <= n);
            } else if (LineType.SEGMENT.equals(this.type)) {
                return ((0 <= n) && (n <= 1));
            } else {
                return true;
            }
        }

        /**
         * 2つの2次元ベクトルの外積を返す
         *
         * @param vector1 2次ベクトルを表わすPointインスタンス
         * @param vector2 2次ベクトルを表わすPointインスタンス
         * @return
         */
        private float cross(PointF vector1, PointF vector2) {
            return (vector1.x * vector2.y - vector1.y * vector2.x);
        }

        public String toString() {
            String str = "";
            if (LineType.STRAIGHT.equals(type)) {
                str += "---> ";
            }
            str += "(" + p1.x + ", " + p1.y + ") ---> (" + p2.x + ", " + p2.y + ")";
            if (LineType.STRAIGHT.equals(type) || LineType.HALF.equals(type)) {
                str += " --->";
            }
            return str;
        }
    }
}