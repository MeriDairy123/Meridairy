package b2infosoft.milkapp.com.QRCodeScanner;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Dairy.DairyUserProfileActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBoxwithIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class QrScannerActivity extends AppCompatActivity implements
        ZXingScannerView.ResultHandler {
    public String TRADE_MARK_TEXT = "Show Meri Dairy Code";
    public int TRADE_MARK_TEXT_SIZE_SP = 17;
    ViewGroup contentFrame;
    QRCodeResult qrCodeResult;
    Context mContext;
    Toolbar toolbar;
    TextView tvSubtitle, tvclickLogin;
    Intent intent;
    String from = "";
    String weblogin = "weblogin";
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scan_qrcode);
        setupToolbar();
        mContext = QrScannerActivity.this;
        intent = getIntent();
        from = Constant.FromWhere;

        contentFrame = findViewById(R.id.content_frame);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvclickLogin = findViewById(R.id.tvclickLogin);
        if (from.equalsIgnoreCase(weblogin)) {
            tvSubtitle.setText(R.string.computerLogin);
            //मेरी डेयरी कॉम्प्युटर लॉग इन
            TRADE_MARK_TEXT = "        "+mContext.getResources().getString(R.string.app_name)+" "+mContext.getResources().getString(R.string.computerLogin);

        } else {
            tvSubtitle.setText(R.string.LOGIN);
            TRADE_MARK_TEXT = "  Show Meri Dairy Code";
            tvclickLogin.setVisibility(View.GONE);
        }

        initView();
    }

    public void initView() {
        contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText();
        System.out.println(">>>>" + rawResult.getText());

        shootSound();
        if (result == null) {

            showAlertBoxwithIntent(QrScannerActivity.this, "No Result Found, Please Try again", QrScannerActivity.class);
        } else {
            if (result.contains("meridairy")) {

                webLogin(result, QrScannerActivity.this);

            } else if (result.contains("user_group_id")) {

                if (from.equals("login")) {
                    LoginActivity.onScanResult(rawResult, QrScannerActivity.this);
                } else {
                    DairyUserProfileActivity.onScanResult(rawResult, QrScannerActivity.this);
                }

            } else {
                showToast(QrScannerActivity.this, "Contents = " + rawResult.getText() +
                        ", Format type = " + rawResult.getBarcodeFormat().toString());
                showAlertBoxwithIntent(QrScannerActivity.this, "No Result Found, Please Try again", QrScannerActivity.class);

            }
        }


    }

    public void shootSound() {
        MediaPlayer mp;

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        try {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            mp = MediaPlayer.create(getApplicationContext(), notification);
            if (mp != null) {
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub

                        mp.reset();
                        mp.release();
                        mp = null;
                        // mplayer.start();
                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    private class CustomViewFinderView extends ViewFinderView {

        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            if (from.equalsIgnoreCase(weblogin)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PAINT.setColor(mContext.getColor(R.color.colorWhite));
                } else {
                    PAINT.setColor(Color.WHITE);
                }
                PAINT.setAntiAlias(true);
                float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
                PAINT.setTextSize(textPixelSize);
                PAINT.setTypeface(Typeface.DEFAULT);
            } else {
                PAINT.setColor(Color.WHITE);
                PAINT.setAntiAlias(true);
                float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
                PAINT.setTextSize(textPixelSize);
                PAINT.setTypeface(Typeface.MONOSPACE);
            }

            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
    public void webLogin(String scanOutput, final Context mContext) {
        SessionManager sessionManager = new SessionManager(mContext);

        try {
            //converting the data to json
            String id = "", user_id = "", user_tokens = "", user_group_id = "";

            JSONObject obj = new JSONObject(scanOutput);

            id = obj.getString("id");
            user_group_id = "2";
            user_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
            user_tokens = sessionManager.getValueSesion(SessionManager.user_token);

            if (isConnected()) {
                NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                        mContext.getString(R.string.Please_Wait), true) {
                    @Override
                    public void handleResponse(String response) {
                        //fail,success

                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                finish();

                            } else {
                                finish();
                                showToast(mContext, jsonObject.getString("user_status_message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("id", id)
                        .addEncoded("user_id", user_id)
                        .addEncoded("user_group_id", user_group_id)
                        .addEncoded("token", user_tokens)
                        .build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.webQRCodeLoginAPI);

            } else {
                showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                finish();
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
