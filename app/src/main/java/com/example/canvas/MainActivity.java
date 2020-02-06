package com.example.canvas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CanvasView canvasView;
    private String filename = "ImageDoodle.jpg";
    private String filepath = "Canvas_Doodle";
    File myExternalFile;
    public static final int PICK_IMAGE = 1;

    Bitmap canvasBitmap;
    Bitmap saveBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        canvasView = findViewById(R.id.draw);
    }

    private void init() {
        Button saveButton = findViewById(R.id.save_button);
        Button clearButton = findViewById(R.id.clear_button);
        saveButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveButton.setEnabled(false);
        } else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(myExternalFile);
                    System.out.println(fileOutputStream);
                    canvasBitmap = canvasView.getBitmap();
                    saveBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    Canvas canvas = new Canvas(saveBitmap);
                    canvas.drawRect(new Rect(0, 0, 320, 480), paint);
                    canvas.drawBitmap(canvasBitmap, new Rect(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight()), new Rect(0, 0, 320, 480), null);
                    if (saveBitmap == null) {
                        System.out.println("NULL bitmap save\n");
                    }
                    saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    showMessage("File saved successfully!");
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    showMessage("File not found!");
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage("Error while saving image!");
                }
                break;

            case R.id.clear_button:
                canvasView.clear();
                break;
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pick_image:
                openGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

