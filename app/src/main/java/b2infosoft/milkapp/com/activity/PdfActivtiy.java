package b2infosoft.milkapp.com.activity;

import static b2infosoft.milkapp.com.useful.MyApp.TAG;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import b2infosoft.milkapp.com.R;

public class PdfActivtiy extends AppCompatActivity {
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_activtiy);
        pdfView = findViewById(R.id.pdfv);

        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        Log.d(TAG, "onCreate: " + str);
        try {
         //   AssetManager am = getAssets();
            pdfView.fromFile(new File("//android_asset/sampleee.pdf"));
           // pdfView.fromAsset(String.valueOf((am.open("sampleee.pdf"))));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}