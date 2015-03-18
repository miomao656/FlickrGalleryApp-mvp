package com.misotest.flickrgalleryapp.presentation.views.customviews;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by miomao on 3/14/15.
 */
public class CustomRecyclerView extends RecyclerView {

    private GridLayoutManager manager;
    private int columnWidth = -1;

    public CustomRecyclerView(Context context) {
        super(context);
//        init(context, null);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        init(context, attrs);
    }

//    private void init(Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            int[] attrsArray = {
//                    android.R.attr.columnWidth
//            };
//            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
//            columnWidth = array.getDimensionPixelSize(0, -1);
//            array.recycle();
//        }
//
//        manager = new GridLayoutManager(getContext(), 1);
//        setLayoutManager(manager);
//    }
//
//    @Override
//    protected void onMeasure(int widthSpec, int heightSpec) {
//        super.onMeasure(widthSpec, heightSpec);
//        if (columnWidth > 0) {
//            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
//            manager.setSpanCount(spanCount);
//        }
//    }
}
