package com.example.caloriecounter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class CameraPage extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private SurfaceView surfaceView;

    private BarcodeDetector barcodeDetector;

    private CameraSource cameraSource;

    private ToneGenerator toneGen1;

    private TextView barcodeText;
    private TextView productNameLabel;
    private TextView calorieLabel;
    private TextView proteinLabel;
    private TextView fatLabel;
    private TextView carbLabel;

    private ProgressBar loadingSymbol;

    private String barcodeData;

    private Product selectedProduct;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_page);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = requireViewById(R.id.surfaceView);
        barcodeText = requireViewById(R.id.barcodeText);
        productNameLabel = requireViewById(R.id.productNameLabel);
        calorieLabel = requireViewById(R.id.calorieLabel);
        proteinLabel = requireViewById(R.id.fatLabel);
        fatLabel = requireViewById(R.id.proteinLabel);
        carbLabel = requireViewById(R.id.carbLabel);
        loadingSymbol = requireViewById(R.id.progressBar);
        initialiseDetectorsAndSources();

        APIQuery apiQuery = new APIQuery("5000168022451");
        loadingSymbol.setVisibility(View.VISIBLE);
        apiQuery.start();
        try {
            apiQuery.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        loadingSymbol.setVisibility(View.INVISIBLE);
        selectedProduct = apiQuery.getProduct();
        updateLabels();

    }


    public void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();


        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(CameraPage.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CameraPage.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    barcodeText.post(() -> {

                        barcodeData = barcodes.valueAt(0).displayValue;
                        if(!barcodeText.getText().equals(String.format(getString(R.string.productID), barcodeData)))
                        {
                            barcodeText.setText(String.format(getString(R.string.productID), barcodeData));
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 300);

                            APIQuery apiQuery = new APIQuery(barcodeData);
                            loadingSymbol.setVisibility(View.VISIBLE);
                            apiQuery.start();
                            try {
                                apiQuery.join();
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                            loadingSymbol.setVisibility(View.INVISIBLE);
                            selectedProduct = apiQuery.getProduct();
                            updateLabels();
                        }
                    });

                }
            }
        });
    }

    public void updateLabels(){
        productNameLabel.setText(String.format(getString(R.string.productNameLabelString), selectedProduct.getName()));
        calorieLabel.setText(String.format(getString(R.string.calorieLabel), String.valueOf(selectedProduct.getCalories())));
        proteinLabel.setText(String.format(getString(R.string.proteinLabel), String.valueOf(selectedProduct.getProtein())));
        fatLabel.setText(String.format(getString(R.string.fatLabel), String.format(String.valueOf(selectedProduct.getFat()), "%")));
        carbLabel.setText(String.format(getString(R.string.carbLabel), String.valueOf(selectedProduct.getCarbs())));
    }


}