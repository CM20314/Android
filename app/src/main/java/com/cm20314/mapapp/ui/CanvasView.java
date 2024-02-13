package com.cm20314.mapapp.ui;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MASK;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Color;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.models.Building;
import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.models.MapDataResponse;

public class CanvasView extends View {
    private MapDataResponse mapData;
    private Paint paint = new Paint(); // Paint object for coloring shapes
    private float radius = 100f; // Radius of circles to be drawn

    private float initX = 0f ;// See onTouchEvent
    private float initY = 0f ;// See onTouchEvent

    private float canvasX = 0f; // x-coord of canvas center
    private float canvasY = 0f; // y-coord of canvas center
    private float dispWidth = 0f; // (Supposed to be) width of entire canvas
    private float dispHeight = 0f ;// (Supposed to be) height of entire canvas

    private boolean dragging = false; // May be unnecessary
    private boolean firstDraw = true;

    // Detector for scaling gestures (i.e. pinching or double tapping
    private ScaleGestureDetector detector = new ScaleGestureDetector(getContext(), new ScaleListener());
    private float scaleFactor = 1f; // Zoom level (initial value is 1x)

    private float MIN_ZOOM = 1f;
    private float MAX_ZOOM  = 10f;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();// save() and restore() are used to reset canvas data after each draw
        // Set the canvas origin to the center of the screen only on the first time onDraw is called
        //  (otherwise it'll break the panning code)
        if (firstDraw) {
            canvasX = 0;
            canvasY = 0;
            firstDraw = false;
        }
        canvas.scale(scaleFactor, scaleFactor) ;// Scale the canvas according to scaleFactor

        // Just draw a bunch of circles (this is for testing panning and zooming
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#000000"));
        canvas.translate(canvasX, canvasY);
        drawBuildings(canvas);
//        canvas.drawCircle(0f,0f,radius,paint);
//        for (int i = 2; i<=40; i+=2) {
//            canvas.drawCircle(radius*i,0f,radius,paint);
//            canvas.drawCircle(-radius*i,0f,radius,paint);
//            canvas.drawCircle(0f,radius*i,radius,paint);
//            canvas.drawCircle(0f,-radius*i,radius,paint);
//            canvas.drawCircle(radius*i,radius*i,radius,paint);
//            canvas.drawCircle(radius*i,-radius*i,radius,paint);
//            canvas.drawCircle(-radius*i,radius*i,radius,paint);
//            canvas.drawCircle(-radius*i,-radius*i,radius,paint);
//        }

        canvas.restore();

        dispWidth = getWidth();
        dispHeight = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        //@TODO: HIGH PRIORITY
        // - Prevent user from scrolling past ends of canvas

        //@TODO: LOW PRIORITY
        // - Add functionality such that initX and initY snap to the position of whichever
        //    finger is up first, be it pointer or main (to prevent jumpiness)
        // - Make sure that when the user zooms in or out the focal point is the midpoint of a line
        //    connecting the main and pointer fingers

        switch (event.getAction() & ACTION_MASK) {
            case ACTION_DOWN:
                // Might not be necessary; check out later
                dragging = true;
                // We want to store the coords of the user's finger as it is before they move
                //  in order to calculate dx and dy
                initX = x;
                initY = y;
                break;

            case ACTION_MOVE:
                // Self explanatory; the difference in x- and y-coords between successive calls to
                //  onTouchEvent
                float dx = x - initX;
                float dy = y - initY;

                if (dragging) {
                    // Move the canvas dx units right and dy units down
                    // dx and dy are divided by scaleFactor so that panning speeds are consistent
                    //  with the zoom level
                    canvasX += dx/scaleFactor;
                    canvasY += dy/scaleFactor;

                    invalidate(); // Re-draw the canvas

                    // Change initX and initY to the new x- and y-coords
                    initX = x;
                    initY = y;
                }
            break;
        case ACTION_POINTER_UP:
                // This sets initX and initY to the position of the pointer finger so that the
                //  screen doesn't jump when it's lifted with the main finger still down
                initX = x;
                initY = y;
            break;
            case ACTION_UP:
                dragging = false; // Again, may be unnecessary
        }

        detector.onTouchEvent(event); // Listen for scale gestures (i.e. pinching or double tap+drag

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            // Self-explanatory
            scaleFactor *= detector.getScaleFactor();
            // If scaleFactor is less than 0.5x, default to 0.5x as a minimum. Likewise, if
            //  scaleFactor is greater than 10x, default to 10x zoom as a maximum.
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            invalidate(); // Re-draw the canvas

            return true;
        }
    }

    public void SetMapData(MapDataResponse data){
        mapData = data;
        invalidate();
    }

    private void drawBuildings(Canvas canvas){
        Paint paint = new Paint(); // Declare and initialize a Paint object
        paint.setColor(Color.GRAY); // Set the line color to black
        paint.setStrokeWidth(4 / scaleFactor);

        if( mapData == null) return;

        for(Building building : mapData.buildings){
                for(int i = 0; i < building.polyline.coordinates.size(); i++){
                    Coordinate startCoordinate = building.polyline.coordinates.get(i);
                    int secondCoordIndex = i != building.polyline.coordinates.size() - 1 ? i + 1 : 0;
                    Coordinate endCoordinate = building.polyline.coordinates.get(secondCoordIndex);

                    float startX = (float) (startCoordinate.x);
                    float startY = (float) (startCoordinate.y);
                    float endX = (float) (endCoordinate.x);
                    float endY = (float) (endCoordinate.y);

                    canvas.drawLine(startX, startY, endX, endY, paint);
                }

                paint.setTextSize(15);
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.CENTER);

                float midX = (float) building.polyline.coordinates.stream().mapToDouble(c -> c.x).average().getAsDouble();
                float midY = (float) building.polyline.coordinates.stream().mapToDouble(c -> c.y).average().getAsDouble();
                canvas.drawText(building.shortName, midX, midY, paint);
            }

    }
}
