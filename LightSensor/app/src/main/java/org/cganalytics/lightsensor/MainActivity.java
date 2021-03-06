package org.cganalytics.lightsensor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private static final int REQ_CODE_TAKE_PICTURE = 900;
    private static String fileName = "lightsensordata.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void measureLight(View view) {
        Intent picIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.d(TAG, "ActivityResult");

        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            byte[] bitmapdata = (byte[]) intent.getExtras().get("data");
            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

            int width = bmp.getWidth();
            int height = bmp.getHeight();

            int[] pixels = new int[width*height];
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);

            double total = 0;

            for (int i=0; i<pixels.length; i++) {
                double red = Color.red(pixels[i]) * 0.299;
                double green = Color.green(pixels[i]) * 0.587;
                double blue = Color.blue(pixels[i]) * 0.114;
                total += red + green + blue;
            }

            double brightness = total/(width*height);

            TextView brightView = (TextView) findViewById(R.id.brightness);
            brightView.setText(String.valueOf(brightness));

            Date timestamp = new Date();
            String outputText = timestamp.toString() + "," + brightness + "\n";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(file, true);
                outputStream.write(outputText.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, outputText);

        }
    }
}
