package b2infosoft.milkapp.com.ShareAds_Animal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import b2infosoft.milkapp.com.R;


public class VideoPlayerActivity extends AppCompatActivity {
    VideoView video_player;
    Toolbar toolbar;
    TextView toolbar_title;
    Context mContext;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);
        mContext = VideoPlayerActivity.this;
        video_player = findViewById(R.id.video_player);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.video_play));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent in = getIntent();
        in.setType("video/*");
        Uri file = Uri.parse(in.getExtras().get("contentUri").toString());
        Log.d("video_path====", String.valueOf(file));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video_player);
        video_player.setMediaController(mediaController);
        video_player.setVideoURI(file);
        video_player.start();

    }


/*
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        finish();
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
