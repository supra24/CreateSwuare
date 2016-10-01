package com.example.damian.createsquare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    private Button bSelectPicture, bAddSquare, bAccept;
    private ImageView imageView, imageViewEnd;
    private Bitmap selectBitmap;
    private VelocityTracker vTracker = null;
    private float startingDistanceBetweenFingers;
    private float Xaxis, Yaxis, lengthSide = 200;
    private boolean haveSquare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        bSelectPicture = (Button) findViewById(R.id.b_select_picture);
        bAddSquare = (Button) findViewById(R.id.b_add_square);
        bAccept = (Button) findViewById(R.id.b_accept);
        imageView = (ImageView) findViewById(R.id.i_image);
        imageViewEnd = (ImageView) findViewById(R.id.i_image_end);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)) {

            Uri imageUri = data.getData();
            InputStream imputStream;

            try {
                imputStream = getBaseContext().getContentResolver().openInputStream(imageUri);
                selectBitmap = BitmapFactory.decodeStream(imputStream);
                selectBitmap = Bitmap.createScaledBitmap(selectBitmap, 1024, 768, false);
                imageView.setImageBitmap(this.selectBitmap);

                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        onTouchEventImageView(v, event);
                        return true;
                    }
                });

                Xaxis = selectBitmap.getWidth() / 2;
                Yaxis = selectBitmap.getHeight() / 2;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.b_select_picture)
    public void selectPicture() {
        Toast.makeText(getBaseContext(), "Open gallery", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.b_add_square)
    public void addSquare() {

        bitmapWithSquare();
    }

    @OnClick(R.id.b_accept)
    public void accept() {

        final Bitmap newBitmap = Bitmap.createBitmap((int) lengthSide, (int) lengthSide, Bitmap.Config.ARGB_8888);

        final SquareImageSupport squareImageSupport = new SquareImageSupport(new SquareImageSupport.OnCompleteListener() {
            @Override
            public Bitmap doingBockgraund(final Bitmap bitmap) {

                int XaxisStart = (int) (Xaxis - lengthSide / 2);
                int YaxisStart = (int) (Yaxis - lengthSide / 2);

                for (int i = 0; i < newBitmap.getWidth(); i++) {
                    for (int j = 0; j < newBitmap.getHeight(); j++) {
                        newBitmap.setPixel(i, j, bitmap.getPixel(XaxisStart, YaxisStart));
                        YaxisStart++;
                    }
                    YaxisStart = (int) (Yaxis - lengthSide / 2);
                    XaxisStart++;
                }

                return newBitmap;
            }

            @Override
            public void onNewImageComplete(final Bitmap bitmap) {
                imageViewEnd.setImageBitmap(bitmap);

            }
        });

        squareImageSupport.execute(selectBitmap);

//        Bitmap bitmap = Bitmap.createBitmap((int) lengthSide, (int) lengthSide, Bitmap.Config.ARGB_8888);
//
//        int XaxisStart = (int) (Xaxis - lengthSide / 2);
//        int YaxisStart = (int) (Yaxis - lengthSide / 2);
//
//        for (int i = 0; i < bitmap.getWidth(); i++) {
//            for (int j = 0; j < bitmap.getHeight(); j++) {
//                bitmap.setPixel(i, j, selectBitmap.getPixel(XaxisStart, YaxisStart));
//                YaxisStart++;
//            }
//            YaxisStart = (int) (Yaxis - lengthSide / 2);
//            XaxisStart++;
//        }
//
//        imageViewEnd.setImageBitmap(bitmap);
    }

    private void bitmapWithSquare() {

        Bitmap bitmap = selectBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(Xaxis - lengthSide / 2, Yaxis - lengthSide / 2, Xaxis + lengthSide / 2, Yaxis + lengthSide / 2, paint);
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
        haveSquare = true;
    }

    public boolean onTouchEventImageView(View view, MotionEvent event) {

        if (haveSquare) {

            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (event.getPointerCount() == 1) {

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (vTracker == null) {
                            vTracker = VelocityTracker.obtain();
                        } else {
                            vTracker.clear();
                        }
                        vTracker.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        vTracker.addMovement(event);
                        vTracker.computeCurrentVelocity(500);

                        Xaxis = Xaxis + vTracker.getXVelocity() / 15;
                        Yaxis = Yaxis + vTracker.getYVelocity() / 15;

                        exceptionAxisAndLengthSide();

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                }
            } else if (event.getPointerCount() == 2) {
                switch (action) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        startingDistanceBetweenFingers = distanceBetweenTwoFingers(event);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_MOVE:
                        float newDistance = distanceBetweenTwoFingers(event);
                        if (newDistance != startingDistanceBetweenFingers) {

                            lengthSide = lengthSide + (newDistance - startingDistanceBetweenFingers) / 50;
                            exceptionAxisAndLengthSide();
                        }
                        break;
                }
            }
            bitmapWithSquare();
        }
        return true;
    }

    private float distanceBetweenTwoFingers(MotionEvent e) {

        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private void exceptionAxisAndLengthSide() {
        if (Xaxis + lengthSide / 2 >= selectBitmap.getWidth()) {
            Xaxis = selectBitmap.getWidth() - lengthSide / 2;
        }
        if (Xaxis - lengthSide / 2 < 0) {
            Xaxis = lengthSide / 2;
        }
        if (Yaxis + lengthSide / 2 >= selectBitmap.getHeight()) {
            Yaxis = selectBitmap.getHeight() - lengthSide / 2;
        }
        if (Yaxis - lengthSide / 2 < 0) {
            Yaxis = lengthSide / 2;
        }
        if (lengthSide >= selectBitmap.getWidth()) {
            lengthSide = selectBitmap.getWidth();
        }
        if (lengthSide >= selectBitmap.getHeight()) {
            lengthSide = selectBitmap.getHeight();
        }
        if (lengthSide < 0) {
            lengthSide = 0;
        }
    }
}
