package com.asus.zenboconcierge.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asus.zenboconcierge.R;
import com.asus.zenboconcierge.dtos.OrderItem;

import java.util.List;

public class OrderSummaryListAdapter extends ArrayAdapter<OrderItem> {

    private static final String TAG = "OrderSummaryListAdapter";

    private Context mContext;
    private int mResource;
    private List<OrderItem> items;

    /**
     * Holds variables for views
     */
    static class ViewHolder {
        TextView textViewItemName;
        TextView textViewItemPrice;
        TextView textViewItemQuantity;
        TextView textViewItemSubTotal;
    }

    public OrderSummaryListAdapter(@NonNull Context context, int resource, @NonNull List<OrderItem> items) {
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

        holder.textViewItemName = convertView.findViewById(R.id.textview_order_summary_item_name);
        holder.textViewItemPrice = convertView.findViewById(R.id.textview_order_summary_item_price);
        holder.textViewItemQuantity = convertView.findViewById(R.id.textview_order_summary_item_quantity);
        holder.textViewItemSubTotal = convertView.findViewById(R.id.textview_order_summary_item_subtotal);

        convertView.setTag(holder);

        holder.textViewItemName.setText(item.getFoodItem().getName());
        holder.textViewItemPrice.setText(String.format("@  $ %.2f", item.getFoodItem().getPrice()));
        holder.textViewItemQuantity.setText(String.format("%d", item.getQuantityOrdered()));
        holder.textViewItemSubTotal.setText(String.format("$ %.2f", item.getQuantityOrdered() * item.getFoodItem().getPrice()));

        return convertView;
    }

}
