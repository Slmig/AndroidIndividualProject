package com.example.androidindividualproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class FallingItemsGameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private FallingItemsGameSurfaceView.FallingItemsGameThread thread;
    FallingItemsGameActivity activity;
    public FallingItemsGameSurfaceView(Context context, AttributeSet attrs, FallingItemsGameActivity _activity) {
        super(context, attrs);
        activity = _activity;
        getHolder().addCallback(this);
        thread = new FallingItemsGameThread(getHolder(), this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        activity.screenWidth = width;
        activity.screenHeight = height;
        activity.basketX = activity.screenWidth / 2 - activity.basketWidth / 2;

        activity.backgroundBitmap = FallingItemsGameActivity.scaleBitmap(activity.backgroundBitmap, activity.screenWidth);

        float scale = Math.max(
                (float) activity.screenWidth / activity.backgroundBitmap.getWidth(),
                (float) activity.screenHeight / activity.backgroundBitmap.getHeight()
        );
        int newWidth = Math.round(activity.backgroundBitmap.getWidth() * scale);
        int newHeight = Math.round(activity.backgroundBitmap.getHeight() * scale);

        activity.backgroundBitmap = Bitmap.createScaledBitmap(activity.backgroundBitmap, newWidth, newHeight, true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (activity.isGameOver) return;

        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - activity.lastSpeedIncreaseTime >= 30000 && activity.speedMultiplier < activity.MAX_SPEED_MULTIPLIER) {
            activity.speedMultiplier += 0.1f;
            activity.lastSpeedIncreaseTime = currentTime;
        }

        float adjustedSpawnRate = activity.spawnRate / activity.speedMultiplier;

        activity.frameCount++;
        if (activity.frameCount >= adjustedSpawnRate) {
            activity.frameCount = 0;
            Bitmap bitmap = activity.random.nextBoolean() ? activity.coinBitmap : activity.bombBitmap;
            activity.items.add(new FallingItem(
                    activity.random.nextInt(activity.screenWidth - bitmap.getWidth()),
                    0,
                    bitmap
            ));
        }

        Iterator<FallingItem> iterator = activity.items.iterator();
        while (iterator.hasNext()) {
            FallingItem item = iterator.next();
            item.y += 10 * activity.speedMultiplier;

            if (item.y + item.bitmap.getHeight() > activity.screenHeight - activity.basketHeight &&
                    item.x + item.bitmap.getWidth() > activity.basketX &&
                    item.x < activity.basketX + activity.basketWidth) {
                if (item.bitmap == activity.coinBitmap) {
                    activity.score++;
                } else {
                    if (activity instanceof Activity) {
                        ((Activity) activity).runOnUiThread(this::showGameOverDialog);
                    }
                }
                iterator.remove();
            } else if (item.y > activity.screenHeight) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            activity.basketX = (int) event.getX() - activity.basketWidth / 2;
            if (activity.basketX < 0) activity.basketX = 0;
            if (activity.basketX + activity.basketWidth > activity.screenWidth) activity.basketX = activity.screenWidth - activity.basketWidth;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas == null) return;

        int bgOffsetX = (activity.backgroundBitmap.getWidth() - activity.screenWidth) / 2;
        int bgOffsetY = (activity.backgroundBitmap.getHeight() - activity.screenHeight) / 2;
        canvas.drawBitmap(activity.backgroundBitmap, -bgOffsetX, -bgOffsetY, null);

        canvas.drawBitmap(activity.basketBitmap, activity.basketX, activity.screenHeight - activity.basketHeight, null);

        for (FallingItem item : activity.items) {
            canvas.drawBitmap(item.bitmap, item.x, item.y, null);
        }

        canvas.drawText("Счет: " + activity.score, 20, 60, activity.paint);
    }

    public class FallingItem {
        int x, y;
        Bitmap bitmap;

        FallingItem(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
        }
    }

    public class FallingItemsGameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private FallingItemsGameSurfaceView gameView;
        private boolean running;

        public FallingItemsGameThread(SurfaceHolder surfaceHolder, FallingItemsGameSurfaceView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameView.update();
                        gameView.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FallingItemsGameThread getThread() {
        return thread;
    }
    private void showGameOverDialog() {
        PetData petData = activity.statsCalculator.Recalculate();
        petData.Happiness = Math.min(petData.Happiness + activity.score, 100);
        int gold = activity.dataManager.GetGold();
        activity.dataManager.SaveGold(gold + activity.score);
        activity.dataManager.SaveData(petData);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Конец игры")
                .setMessage("Ваш счет: " + activity.score)
                .setCancelable(false)
                .setPositiveButton("Начать заново", (dialog, which) -> restartGame())
                .setNegativeButton("Вернуться", (dialog, which) -> {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                });

        AlertDialog dialog = builder.create();
        activity.isGameOver = true;
        dialog.show();
    }

    private void restartGame() {
        activity.score = 0;
        activity.isGameOver = false;
        activity.items.clear();
        activity.lastSpeedIncreaseTime = SystemClock.elapsedRealtime();
        activity.speedMultiplier = 1.0f;
        activity.frameCount = 0;
        activity.spawnRate = 30;
    }
}
