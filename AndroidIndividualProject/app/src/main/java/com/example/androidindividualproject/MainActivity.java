package com.example.androidindividualproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView GoldText, HungerText, FatigueText, HappinessText;
    private boolean dead;
    private int gold;
    private Timer timer;
    private DataManager dataManager;
    private StatsCalculator statsCalculator;
    private PetData petData;
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        GoldText = findViewById(R.id.text1);
        HungerText = findViewById(R.id.text2);
        FatigueText = findViewById(R.id.text3);
        HappinessText = findViewById(R.id.text4);

        ImageButton foodButton = findViewById(R.id.button1);
        ImageButton relaxButton = findViewById(R.id.button2);
        ImageButton gameButton = findViewById(R.id.button3);

        foodButton.setOnClickListener(v -> {
            ArrayList<Product> products = new ArrayList<>();
            products.add(new Product("Хлеб", "Восстанавливает 10 единиц голода", 10, 10, Product.ProductTypeEnum.Food));
            products.add(new Product("Яблоко", "Восстанавливает 15 единиц голода", 13, 15, Product.ProductTypeEnum.Food));
            products.add(new Product("Молоко", "Восстанавливает 20 единиц голода", 15, 20, Product.ProductTypeEnum.Food));
            products.add(new Product("Запечённая картошка", "Восстанавливает 25 единиц голода", 18, 25, Product.ProductTypeEnum.Food));
            products.add(new Product("Каша", "Восстанавливает 30 единиц голода", 20, 30, Product.ProductTypeEnum.Food));
            products.add(new Product("Салат", "Восстанавливает 40 единиц голода", 25, 40, Product.ProductTypeEnum.Food));
            products.add(new Product("Жареная рыба", "Восстанавливает 50 единиц голода", 30, 50, Product.ProductTypeEnum.Food));
            products.add(new Product("Стейк", "Восстанавливает 60 единиц голода", 35, 60, Product.ProductTypeEnum.Food));
            products.add(new Product("Пирог", "Восстанавливает 75 единиц голода", 40, 75, Product.ProductTypeEnum.Food));
            products.add(new Product("Праздничный пир", "Восстанавливает 100 единиц голода", 50, 100, Product.ProductTypeEnum.Food));
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            intent.putExtra("product_list", gson.toJson(products));
            startActivity(intent);
        });

        relaxButton.setOnClickListener(v -> {
            ArrayList<Product> products = new ArrayList<>();
            products.add(new Product("Зарядка", "Восстанавливает 10 единиц бодрости", 10, 10, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Короткая прогулка", "Восстанавливает 15 единиц бодрости", 12, 15, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Медитация", "Восстанавливает 20 единиц бодрости", 16, 20, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Сеанс массажа", "Восстанавливает 25 единиц бодрости", 20, 25, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Чтение книги", "Восстанавливает 30 единиц бодрости", 24, 30, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Тёплая ванна", "Восстанавливает 40 единиц бодрости", 30, 40, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Поездка на природу", "Восстанавливает 50 единиц бодрости", 35, 50, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Дневной сон", "Восстанавливает 60 единиц бодрости", 40, 60, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Вечер в спа", "Восстанавливает 75 единиц бодрости", 50, 75, Product.ProductTypeEnum.RelaxProcedure));
            products.add(new Product("Выходной в горах", "Восстанавливает 100 единиц бодрости", 60, 100, Product.ProductTypeEnum.RelaxProcedure));
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            intent.putExtra("product_list", gson.toJson(products));
            startActivity(intent);
        });
        gameButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, FallingItemsGameActivity.class);
            startActivity(intent);
        });


        statsCalculator = new StatsCalculator(this);
        dataManager = new DataManager(this);
        loadAndUpdateData();
        updateView();
    }
    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(dead) return;
                petData = statsCalculator.Recalculate();
                runOnUiThread(() -> {
                    updateView();
                    ImageView centerImage = findViewById(R.id.center_image);
                    if (petData.Hunger < 30 || petData.Fatigue < 30 || petData.Happiness < 30)
                        centerImage.setImageResource(R.drawable.sad_dog);
                    else centerImage.setImageResource(R.drawable.pet_icon);
                    checkDie();
                });
            }
        }, 0, 1000);
    }
    private void updateView(){
        GoldText.setText(String.format("Золото: %d",gold));
        HungerText.setText(String.format("Голод: %.0f",petData.Hunger));
        FatigueText.setText(String.format("Бодрость: %.0f",petData.Fatigue));
        HappinessText.setText(String.format("Счастье: %.0f",petData.Happiness));
    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private void loadAndUpdateData(){
        gold = dataManager.GetGold();
        petData = statsCalculator.Recalculate();
        checkDie();
    }
    private void checkDie(){
        if (!dead && (petData.Hunger <=0 || petData.Fatigue <= 0 || petData.Happiness <= 0)) {
            dead = true;
            stopTimer();
            showPetDieDialog(petData.LifeTime / 60);
        }
    }
    private void showPetDieDialog(long minutes) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pet_die_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button dialogButton = dialogView.findViewById(R.id.dialog_button);

        dialogTitle.setText("Конец игры");
        dialogMessage.setText(getEndgameText(minutes));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogButton.setOnClickListener(v -> {
            petData = new PetData();
            dataManager.SaveData(petData);
            dataManager.SaveGold(0);
            dead = false;
            startTimer();
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    private String getEndgameText(long totalMinutes) {
        long days = totalMinutes / (24 * 60);
        totalMinutes = totalMinutes % (24 * 60);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("Ваш питомец смог прожить %d дней %d часов %d минут", days, hours, minutes);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAndUpdateData();
        startTimer();
    }
}