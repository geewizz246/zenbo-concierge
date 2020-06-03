package com.asus.zenboconcierge.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.asus.zenboconcierge.MenuActivity;
import com.asus.zenboconcierge.R;
import com.asus.zenboconcierge.dtos.OrderItem;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.List;

public class OrderPreviewListAdapter extends ArrayAdapter<OrderItem> {

    private static final String TAG = "OrderPreviewListAdapter";

    private Context mContext;
    private int mResource;
    private List<OrderItem> items;

    /**
     * Holds variables for views
     */
    static class ViewHolder {
        TextView textViewItemName;
        TextView textViewItemPrice;
        ImageView imgViewItemImg;
        ElegantNumberButton numberPicker;
        ImageButton btnRemoveItem;
    }

    public OrderPreviewListAdapter(@NonNull Context context, int resource, @NonNull List<OrderItem> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mResource = resource;
        this.items = items;
    }

    @Nullable
    @Override
    public OrderItem getItem(int position) {
        return items.get(position);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the information from the item
        final OrderItem item = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        final ViewHolder holder = new ViewHolder();

        holder.textViewItemName = convertView.findViewById(R.id.textview_item_name);
        holder.textViewItemPrice = convertView.findViewById(R.id.textview_item_price);
//        holder.imgViewItemImg = convertView.findViewById(R.id.imageview_item_img);
        holder.numberPicker = convertView.findViewById(R.id.number_picker);
        holder.btnRemoveItem = convertView.findViewById(R.id.btn_remove_item);

        convertView.setTag(holder);

        holder.textViewItemName.setText(item.getFoodItem().getName());
        holder.textViewItemPrice.setText(String.format("$ %.2f", item.getFoodItem().getPrice()));
//        holder.imgViewItemImg.setImageBitmap(image);

        // Set up NumberPicker
        holder.numberPicker.setNumber(Integer.toString(item.getQuantityOrdered()));
        holder.numberPicker.setRange(1, item.getFoodItem().getQuantity());
        holder.numberPicker.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                item.setQuantityOrdered(newValue);
                items.set(position, item);

                ((MenuActivity) mContext).updateOrderPreview();
            }
        });

        // Set up ImageButton
        holder.btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.remove(position);

                ((MenuActivity) mContext).updateOrderPreview();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
