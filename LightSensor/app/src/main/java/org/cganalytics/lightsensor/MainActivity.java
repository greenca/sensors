package org.cganalytics.lightsensor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final int REQ_CODE_TAKE_PICTURE = 900;

    public void measureLight(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");

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

        }
    }
}
