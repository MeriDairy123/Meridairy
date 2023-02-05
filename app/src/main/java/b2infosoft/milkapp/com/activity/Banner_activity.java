package b2infosoft.milkapp.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;



import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerCustomText;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImage;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImagePath;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Choudhary on 26-OCt-18.
 */

public class Banner_activity extends Activity {


    Context mContext;
    SessionManager sessionManager;
    Button btnNext;
    ImageView imgBanner;
    TextView tv_title_note;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_notification);
        mContext = Banner_activity.this;
        //NotificatoionBanner(mContext);
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
    }

    private void initView() {
        imgBanner = findViewById(R.id.imgBanner);
        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.btnNext);
        tv_title_note = findViewById(R.id.tv_title_note);
        setData();
    }

    public void setData() {
        String imgpath = "", baner_image = "", custom_msg = "";
        imgpath = sessionManager.getValueSesion(KEY_BannerImagePath);
        baner_image = sessionManager.getValueSesion(KEY_BannerImage);
        custom_msg = sessionManager.getValueSesion(KEY_BannerCustomText);
        imgpath = imgpath + baner_image;


        tv_title_note.setText("");
        tv_title_note.setVisibility(View.GONE);
      /*  Glide.with(mContext)
                .load(imgpath)
                .thumbnail(Glide.with(mContext).load(R.color.color_bg))
                .placeholder(R.color.color_bg)
                .error(R.color.colorBlack)
                .into(imgBanner);*/
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(imgpath).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                progressBar.setVisibility(View.GONE);
                return false;
            }


        }).into(imgBanner);
        // if button is clicked, close the custom dialog
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNextActivity();

            }
        });

    }

    public void moveNextActivity() {
        Intent i = new Intent(mContext, MainActivity.class);
        startActivity(i);
        finish();
    }


}
