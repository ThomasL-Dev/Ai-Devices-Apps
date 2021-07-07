package com.dante.jarvis;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class HomeView extends RelativeLayout {

    public HomeView(Context context) {
        super(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showTrash(){
        findViewById(R.id.trash).setVisibility(VISIBLE);
    }

    public void hideTrash(){
        findViewById(R.id.trash).setVisibility(GONE);
    }

    public boolean isViewTouchingTrash(View v){
        RectF trashRect = Tools.viewToRect(findViewById(R.id.trash));
        RectF vRect = Tools.viewToRect(v);

        return trashRect.intersect(vRect);
    }

    // added cuz needed
    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnContextClickListener(@Nullable OnContextClickListener l) {
        super.setOnContextClickListener(l);
    }

    @Override
    public boolean isClickable() {
        return super.isClickable();
    }
}
