package com.wainow.island.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.wainow.island.R;
import com.wainow.island.adapter.holder.StockItemViewHolder;
import com.wainow.island.ui.item.ItemViewModel;

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {
    private final TextView tvContent;
    private final TextView tvDate;
    private MPPointF mOffset;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // find your layout components
        tvContent = findViewById(R.id.tvContent);
        tvDate = findViewById(R.id.tvDate);
    }

    // Callbacks every time the MarkerView is redrawn, can be used to update the
    // Content (user-interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        double price = StockItemViewHolder.Companion.round(e.getY());
        double time = e.getX();
        String date = ItemViewModel.Companion.timeToString((long) time);

        tvContent.setText("$" + price);
        tvDate.setText(date);
        // This will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // Center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
