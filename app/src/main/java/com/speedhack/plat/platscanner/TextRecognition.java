package com.speedhack.plat.platscanner;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.speedhack.plat.platscanner.GraphicOverlay.Graphic;

public class TextRecognition extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = ".TextRecognition";
    private ImageView mImageView;
    private Button mTextButton;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    private Bitmap gambar;

    private ArrayList<String> platNomor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        mImageView = findViewById(R.id.image_view);

        mTextButton = findViewById(R.id.button_text);

        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        gambar = CameraUtils.convertImagePathToBitmap(OpenCamera.getImageUrl, false);
        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognition();
            }
        });

        platNomor = new ArrayList<String>();
//        mSelectedImage = gambar;
//        Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();
//
//        int targetWidth = targetedSize.first;
//        int maxHeight = targetedSize.second;
//
//        // Determine how much to scale down the image
//        float scaleFactor =
//                Math.max(
//                        (float) mSelectedImage.getWidth() / (float) targetWidth,
//                        (float) mSelectedImage.getHeight() / (float) maxHeight);
//
//        Bitmap resizedBitmap =
//                Bitmap.createScaledBitmap(
//                        mSelectedImage,
//                        (int) (mSelectedImage.getWidth() / scaleFactor),
//                        (int) (mSelectedImage.getHeight() / scaleFactor),
//                        true);
//
//        mImageView.setImageBitmap(resizedBitmap);
//        mSelectedImage = resizedBitmap;

        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Captured Image"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        mTextButton.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                mTextButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        //ambil stringnya
//        String resultText = texts.getText();
        for (FirebaseVisionText.TextBlock block: blocks) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                //Hilangin spasinya
                String finalLineText = "";
                int counter = 0;
                for (int i = 0; i < lineText.length(); i++){
                    char c = lineText.charAt(i);
                    if(c != ' ') {finalLineText += c;
                        counter++;}
                }
                if(counter <= 2) continue;
                if((finalLineText.charAt(0) >= 65 && finalLineText.charAt(0) <= 90 )&&((finalLineText.charAt(1) >= 48 && finalLineText.charAt(1) <= 57)||(finalLineText.charAt(2) >= 48 && finalLineText.charAt(2) <= 57))){
                    System.out.println(finalLineText);
                    platNomor.add(finalLineText);
                }
            }
        }

        mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                    System.out.print(elements.get(k));

                }
            }
        }
        if(platNomor.isEmpty()){
            showToast("Tidak Ada plat nomor pada gambar. Foto Ulang!");
        }
        else{
            Intent intent = new Intent(this, DriverActivity.class);
            intent.putExtra("plat", platNomor.get(0));
//            System.out.println("TEST1");
            startActivity(intent);
        }
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = gambar;
                break;
            case 1:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "plat_2.jpg");
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "plat_3.jpg");
                break;
            case 3:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "plat_4.jpg");
                break;
            case 4:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "plat_4.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
