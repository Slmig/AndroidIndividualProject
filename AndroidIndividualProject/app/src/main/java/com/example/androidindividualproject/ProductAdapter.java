package com.example.androidindividualproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidindividualproject.Product;
import com.example.androidindividualproject.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private DataManager dataManager;
    private StatsCalculator statsCalculator;
    private List<Product> productList;
    private Context savedContext;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        savedContext = context;
        dataManager = new DataManager(context);
        statsCalculator = new StatsCalculator(context);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.titleTextView.setText(product.Title);
        holder.descriptionTextView.setText(product.Description);
        holder.priceTextView.setText(String.valueOf(product.Price));
        holder.buyButton.setOnClickListener(v -> {
            int gold = dataManager.GetGold();
            if (gold >= product.Price) {
                statsCalculator.Recalculate();
                PetData data = dataManager.GetData();
                if (product.ProductType == Product.ProductTypeEnum.Food)
                    data.Hunger = Math.min(data.Hunger + product.RecoveryValue, 100);
                else data.Fatigue = Math.min(data.Fatigue + product.RecoveryValue, 100);
                dataManager.SaveData(data);
                gold -= product.Price;
                dataManager.SaveGold(gold);
            }
            else Toast.makeText(savedContext, "Не хватает средств", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView,priceTextView;
        Button buyButton;

        public ProductViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.product_title);
            descriptionTextView = itemView.findViewById(R.id.product_description);
            priceTextView = itemView.findViewById(R.id.product_price);
            buyButton = itemView.findViewById(R.id.buy_button);
        }
    }
}
