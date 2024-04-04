package com.android.megainventory;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.List;

public class CustomListViewPrint extends ArrayAdapter<ListItem> {

    private final Context context;
    private final int resource;
    private AppCompatTextView productCountTextView;
    private AppCompatTextView productCountWholeTextView;
    private int finalProductCount;
    private int finalProductCountWhole;
    private List<ListItem> listItems;
    public AppCompatTextView saved_product_list_count;


    public CustomListViewPrint(@NonNull Context context, int resource, List<ListItem> items, AppCompatTextView productCountTextView, AppCompatTextView productCountWholeTextView, AppCompatTextView saved_product_list_count) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.listItems = items;
        this.productCountTextView = productCountTextView;
        this.productCountWholeTextView = productCountWholeTextView;
        this.finalProductCount = items.size();
        this.finalProductCountWhole = returnWholeCount(items);
        this.saved_product_list_count = saved_product_list_count;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        ListItem item = getItem(position);


        AppCompatTextView product_name = rowView.findViewById(R.id.listview_product_name);
        product_name.setText(item.P_NAME_SALE);

        AppCompatTextView product_price = rowView.findViewById(R.id.listview_product_price);
        product_price.setText(String.format("ფასი: %s", item.P_PRICE));

        AppCompatTextView product_quantity = rowView.findViewById(R.id.listview_product_quantity);
        product_quantity.setText(String.format("რაოდენობა: %d", item.quantity));

        AppCompatImageButton list_item_delete_button = rowView.findViewById(R.id.list_item_delete_button);
        list_item_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListItem(item);
            }
        });


        AppCompatButton minus_button = rowView.findViewById(R.id.minus_button_listview);
        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusQuantityListItem(item, product_quantity);
            }

        });

        AppCompatButton plus_button = rowView.findViewById(R.id.plus_button_listview);
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plustQuantityListItem(item, product_quantity);
            }
        });


        productCountTextView.setText(String.format("რაოდენობა: %d", finalProductCount));
        productCountWholeTextView.setText(String.format("სულ: %d", finalProductCountWhole));

        return rowView;
    }

    private void deleteListItem(ListItem item) {
        this.listItems.remove(item);
        remove(item);
        finalProductCount--;
        finalProductCountWhole -= item.quantity;
        saved_product_list_count.setText(Integer.toString(this.listItems.size()));
        productCountTextView.setText(String.format("რაოდენობა: %d", finalProductCount));
        productCountWholeTextView.setText(String.format("სულ: %d", finalProductCountWhole));
        System.out.println(productCountTextView.getText());
    }


    @Override
    public ListItem getItem(int position) {
        return super.getItem(position);
    }


    private int returnWholeCount(List<ListItem> listItem) {
        int count = 0;

        for (ListItem item : listItem) {
            count += item.quantity;
        }

        return count;
    }

    private void minusQuantityListItem(ListItem item, TextView product_quantity) {

        int quant = item.quantity;

        if (quant > 0) {
            quant--;
            item.quantity = quant;
            product_quantity.setText(String.format("რაოდენობა: %d", quant));
            finalProductCountWhole--;
            productCountTextView.setText(String.format("რაოდენობა: %d", finalProductCount));
            productCountWholeTextView.setText(String.format("სულ: %d", finalProductCountWhole));
        }


    }

    private void plustQuantityListItem(@NonNull ListItem item, TextView product_quantity) {
        int quant = item.quantity;

        quant++;
        item.quantity = quant;
        product_quantity.setText(String.format("რაოდენობა: %d", quant));

        finalProductCountWhole++;
        productCountTextView.setText(String.format("რაოდენობა: %d", finalProductCount));
        productCountWholeTextView.setText(String.format("სულ: %d", finalProductCountWhole));
    }
}
