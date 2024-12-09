package com.example.androidindividualproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class FallingItemsGameActivity extends AppCompatActivity {
    public int basketX;
    public int basketWidth, basketHeight;

    public Bitmap basketBitmap;
    public Bitmap coinBitmap;
    public Bitmap bombBitmap;
    public Bitmap backgroundBitmap;

    public ArrayList<FallingItemsGameSurfaceView.FallingItem> items;
    public Random random;

    public int screenWidth, screenHeight;
    public int score = 0;
    public boolean isGameOver = false;

    public int spawnRate = 30;
    public int frameCount = 0;

    public Paint paint;
    public long lastSpeedIncreaseTime;
    public float speedMultiplier = 1.0f;
    public final float MAX_SPEED_MULTIPLIER = 7.0f;

    public StatsCalculator statsCalculator;
    public DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager(this);
        statsCalculator = new StatsCalculator(this);

        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        basketBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        coinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        bombBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        basketBitmap = scaleBitmap(basketBitmap, screenWidth/7);
        coinBitmap = scaleBitmap(coinBitmap, screenWidth/10);
        bombBitmap = scaleBitmap(bombBitmap, screenWidth/10);

        basketWidth = basketBitmap.getWidth();
        basketHeight = basketBitmap.getHeight();

        items = new ArrayList<>();
        random = new Random();
        basketX = 0;

        paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(0xFFFFFFFF);
        spawnRate = 20;

        FallingItemsGameSurfaceView gameSurfaceView = new FallingItemsGameSurfaceView(this, null,this);
        FrameLayout container = new FrameLayout(this);
        container.addView(gameSurfaceView);
        setContentView(container);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth) {
        float aspectRatio = (float) bitmap.getHeight() / bitmap.getWidth();
        int newHeight = Math.round(newWidth * aspectRatio);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FallingItemsGameSurfaceView gameSurfaceView = new FallingItemsGameSurfaceView(this, null,this);
        FrameLayout container = new FrameLayout(this);
        container.addView(gameSurfaceView);
        setContentView(container);
    }
}
