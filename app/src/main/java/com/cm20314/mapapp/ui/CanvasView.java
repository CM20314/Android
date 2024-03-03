package com.cm20314.mapapp.ui;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MASK;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.models.Building;
import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.models.NodeArcDirection;
import com.cm20314.mapapp.models.RouteResponseData;
import com.cm20314.mapapp.services.Constants;

public class CanvasView extends View {
    private MapDataResponse mapData;
    public RouteResponseData routeData;
    private boolean displayRoute = false;
    private Coordinate location;
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
    private float scaleFactor = 2f; // Zoom level (initial value is 1x)

    private float MIN_ZOOM = 1f;
    private float MAX_ZOOM  = 10f;

    private double maxWidth = -1;

    private double maxHeight = -1;

    private double PADDING = 200;
    private int fillColor;

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
        canvas.restore();

        dispWidth = getWidth();
        dispHeight = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        System.out.print(event.getSource());
        System.out.print(event.getAction());

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
                    if (maxWidth !=-1){
                        if(canvasX>0+PADDING){
                      //      canvasX= (float) (0+PADDING);
                        }
                        else if(canvasX<-maxWidth+(getWidth()-PADDING)/scaleFactor){
                        //   canvasX = (float) ((float)-maxWidth+(getWidth()-PADDING)/scaleFactor);

                        }
                    }

                    canvasY += dy/scaleFactor;
                    if (maxHeight !=-1){
                        if(canvasY>0+PADDING){
                            canvasY= (float) (0+PADDING);
                        }
                        else if(canvasY<-maxHeight+(getHeight()-PADDING)/scaleFactor){
                            canvasY = (float) ((float)-maxHeight+(getHeight()-PADDING)/scaleFactor);


                        }
                    }

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

    public void SetMapData(MapDataResponse data, Coordinate location, int fillColor){
        mapData = data;
        this.fillColor = fillColor;
        this.location = location;
        maxWidth = mapData.buildings.stream().mapToDouble(b->b.polyline.coordinates.stream().mapToDouble(c->c.x).max().orElse(0)).max().orElse(0);
        maxHeight = mapData.buildings.stream().mapToDouble(b->b.polyline.coordinates.stream().mapToDouble(c->c.y).max().orElse(0)).max().orElse(0);
        invalidate();
    }

    public void UpdateRoute(RouteResponseData data, boolean invalidateMap){
        routeData = data;
        if(invalidateMap) {
            SetDisplayRoute(true);
            invalidate();
        }
    }

    public void UpdateRoute(RouteResponseData data){
        UpdateRoute(data, true);
    }

    public void SetDisplayRoute(boolean display){
        displayRoute = display;
    }

    public void UpdateLocation(Coordinate location, boolean invalidateMap){
        this.location = location;
        if(invalidateMap) invalidate();
    }

    public void UpdateLocation(Coordinate location){
        UpdateLocation(location, true);
    }

    private void drawBuildings(Canvas canvas){
        Paint paint = new Paint(); // Declare and initialize a Paint object
        paint.setStrokeWidth(4 / scaleFactor);

        if( mapData == null) return;

        Paint pathsPaint = new Paint();
        pathsPaint.setColor(Color.GRAY);
        pathsPaint.setStyle(Paint.Style.STROKE);
        pathsPaint.setAlpha(30);
        pathsPaint.setAntiAlias(true);
        pathsPaint.setStrokeWidth(20 / scaleFactor);

        for(int i = 0; i < mapData.paths.size(); i++){
            Coordinate startCoordinate = mapData.paths.get(i).node1.coordinate;
            Coordinate endCoordinate = mapData.paths.get(i).node2.coordinate;

            float startX = (float) (startCoordinate.x);
            float startY = (float) (startCoordinate.y);
            float endX = (float) (endCoordinate.x);
            float endY = (float) (endCoordinate.y);

            canvas.drawLine(startX, startY, endX, endY, pathsPaint);
        }

        for(Building building : mapData.buildings){
            paint.setColor(Color.GRAY); // Set the line color to black
            Path vectorPath = new Path();
                for(int i = 0; i < building.polyline.coordinates.size(); i++){
                    Coordinate startCoordinate = building.polyline.coordinates.get(i);
                    int secondCoordIndex = i != building.polyline.coordinates.size() - 1 ? i + 1 : 0;
                    Coordinate endCoordinate = building.polyline.coordinates.get(secondCoordIndex);

                    float startX = (float) (startCoordinate.x);
                    float startY = (float) (startCoordinate.y);
                    float endX = (float) (endCoordinate.x);
                    float endY = (float) (endCoordinate.y);

                    if(i == 0){
                        vectorPath.moveTo(startX, startY);
                    }
                    vectorPath.lineTo(endX, endY);
                    //canvas.drawLine(startX, startY, endX, endY, paint);
                }
                vectorPath.close();

            Paint fillPaint = new Paint();
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setColor(fillColor);
            fillPaint.setAntiAlias(true);
            fillPaint.setDither(true);

            Paint borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(3 / scaleFactor);
            borderPaint.setColor(Color.BLACK);
            borderPaint.setAntiAlias(true);
            borderPaint.setDither(true);

            // First draw the fill path.
            canvas.drawPath(vectorPath, fillPaint);
// Then overlap this with the border path.
            canvas.drawPath(vectorPath, borderPaint);

            Coordinate offset = Constants.TEXT_OFFSETS.getOrDefault(building.shortName, new Coordinate(0,0));

            paint.setTextSize(7);
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.CENTER);

                float midX = (float) (building.polyline.coordinates.stream().mapToDouble(c -> c.x).average().getAsDouble() + offset.x);
                float midY = (float) (building.polyline.coordinates.stream().mapToDouble(c -> c.y).average().getAsDouble() + offset.y);
                canvas.drawText(building.shortName, midX, midY, paint);
            }
        // Draw location

        paint.setColor(getColor(androidx.appcompat.R.attr.colorPrimary));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawCircle((float) location.x, (float) location.y, 10 / scaleFactor, paint);
        paint.setAlpha(30);
        canvas.drawCircle((float) location.x, (float) location.y, 80 / scaleFactor, paint);

        if(displayRoute){
            // Display the route
            Paint pathPaint = new Paint();
            pathPaint.setColor(getColor(androidx.appcompat.R.attr.colorPrimary));
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setStrokeWidth(8 / scaleFactor);

            for(int i = 0; i < routeData.nodeArcDirections.size(); i++){
                    NodeArcDirection nodeArcDirection = routeData.nodeArcDirections.get(i);
                    Coordinate startCoordinate = nodeArcDirection.nodeArc.node1.coordinate;
                    Coordinate endCoordinate = nodeArcDirection.nodeArc.node2.coordinate;

                    float startX = (float) (startCoordinate.x);
                    float startY = (float) (startCoordinate.y);
                    float endX = (float) (endCoordinate.x);
                    float endY = (float) (endCoordinate.y);

                    canvas.drawLine(startX, startY, endX, endY, pathPaint);
            }

            pathPaint.setStyle(Paint.Style.FILL);
            pathPaint.setAntiAlias(true);
            Coordinate destCoord = routeData.nodeArcDirections.get(routeData.nodeArcDirections.size() - 1).nodeArc.node2.coordinate;
            canvas.drawCircle((float) destCoord.x, (float) destCoord.y, 10 / scaleFactor, pathPaint);

        }
    }
    private int getColor(int attrId){
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, typedValue, true);
        return  typedValue.data;
    }
}
