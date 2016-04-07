package org.cganalytics.lightsensor;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {

    private static String TAG = CameraActivity.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        return c;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", data);

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // get Camera parameters
        Camera.Parameters params = mCamera.getParameters();

        // Turn off parameters than interfere with light measurement
        if (params.isAutoExposureLockSupported()) {
            params.setAutoExposureLock(true);
        }
        if (params.isAutoWhiteBalanceLockSupported()) {
            params.setAutoWhiteBalanceLock(true);
        }
        params.setExposureCompensation(0);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        // Create Preview view and set it as the content of the activity
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
