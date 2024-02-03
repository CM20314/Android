package com.cm20314.mapapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;

import com.cm20314.mapapp.R;

public class CanvasView extends View {
    private ShapeDrawable drawable;

    public CanvasView(Context context) {
        super(context);
        new CanvasView(context, null);
    }

    public CanvasView(Context context, AttributeSet attr){
        super(context, attr);
        int x = 10;
        int y = 10;
        int width = 300;
        int height = 50;
        setContentDescription(context.getResources().getString(
                R.string.canvas_view_desc));

        drawable = new ShapeDrawable(new OvalShape());
        // If the color isn't set, the shape uses black as the default.
        drawable.getPaint().setColor(0xff74AC23);
        // If the bounds aren't set, the shape can't be drawn.
        drawable.setBounds(x, y, x + width, y + height);
    }

    protected void onDraw(Canvas canvas) {
        drawable.draw(canvas);
    }
}
