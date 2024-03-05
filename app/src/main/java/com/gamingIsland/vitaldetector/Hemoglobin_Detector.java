package com.gamingisland.vitaldetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Hemoglobin_Detector extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    PyObject pyf;
    LoadingDialog loadingDialog;
    Handler handler = new Handler();
    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hemoglobin_detector);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        loadingDialog = new LoadingDialog(Hemoglobin_Detector.this);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();
        pyf = py.getModule("hello");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    public void start(View view) {
        if (imageView.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.test).getConstantState()) {
            textView.setText("loading");
            loadingDialog.startLoadingDialog();
            Log.i("checking", "loading");
            ExampleRunnable runnable = new ExampleRunnable();
            new Thread(runnable).start();
        } else {
            Toast.makeText(this, "Choose a new Image", Toast.LENGTH_SHORT).show();

        }
    }

    public class ExampleRunnable implements Runnable {

        @Override
        public void run() {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap b = drawable.getBitmap();
            int sz = b.getByteCount();
            float fl = (float) sz / 1000;
            Log.i("size", Float.toString(fl));


            b = b.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap scaled = Bitmap.createScaledBitmap(b, 500, 800, true);
            int w = b.getWidth();
            int h = b.getHeight();
            int colour;
            int[][] red = new int[500][800];
            int[][] green = new int[500][800];
            int[][] blue = new int[500][800];
            int[][] alpha = new int[500][800];

            for (int i = 0; i < 500; i++)
                for (int j = 0; j < 800; j++) {
                    colour = scaled.getPixel(i, j);
                    red[i][j] = Color.red(colour);
                    green[i][j] = Color.green(colour);
                    blue[i][j] = Color.blue(colour);
                    alpha[i][j] = Color.alpha(colour);
                }
            final int[][][] data = pyf.callAttr("test", red, green, blue).toJava(int[][][].class);
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 800; y++) {

                    int color = Color.argb(255, data[0][y][x], data[1][y][x], data[2][y][x]);

                    scaled.setPixel(x, y, color);
                }
            }
            final Bitmap scaledup = Bitmap.createScaledBitmap(scaled, 625, 1000, true);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(scaledup);
                    String strDouble = String.format("%.2f", 0.10427298290070008 * (data[3][0][0] / (data[3][0][1] * 1.5)) + 0.324912796713134);
                    textView.setText("Hemoglobin level : " + strDouble);

                }
            });
            loadingDialog.dismissDialog();
        }
    }

    public void chooseImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};

                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission Deniedd!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data.getData());
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


}