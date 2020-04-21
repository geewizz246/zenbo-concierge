package com.asus.zenboconcierge.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asus.zenboconcierge.R;
import com.asus.zenboconcierge.dtos.FoodItem;

import java.util.List;

public class FoodItemGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<FoodItem> foodItems;

    public FoodItemGridAdapter(Context context, List<FoodItem> foodItems) {
        this.mContext = context;
        this.foodItems = foodItems;
    }

    @Override
    public int getCount() {
        return foodItems.size();
    }

    @Override
    public FoodItem getItem(int pos) {
        return foodItems.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final FoodItem foodItem = foodItems.get(pos);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.list_item_order_menu, parent, false);
        }

        final ImageView imageView = view.findViewById(R.id.imageview_item_img);
        final TextView textViewName = view.findViewById(R.id.textview_menu_item_name);
        final TextView textViewPrice = view.findViewById(R.id.textview_menu_item_price);

//        imageView.setImageResource(foodItem.getImgPath());
        textViewName.setText(foodItem.getName());
        textViewPrice.setText(String.format("$ %.2f", foodItem.getPrice()));
        return view;
    }
}
