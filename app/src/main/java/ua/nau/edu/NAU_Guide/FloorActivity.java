package ua.nau.edu.NAU_Guide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import org.voron.voronimageview.VoronImageView;

import ua.nau.edu.Systems.TouchImageView;

public class FloorActivity extends BaseNavigationDrawerActivity {
    public FloorActivity() {
    }

    Bitmap svgBitmap;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        switch (getIntent().getIntExtra("AnimChoose", -1)) {
            case 1: {
                RelativeLayout rl_1 = (RelativeLayout) findViewById(R.id.layout_map_1);
                rl_1.setVisibility(View.VISIBLE);

                RelativeLayout rl_2 = (RelativeLayout) findViewById(R.id.layout_map_2);
                rl_2.setVisibility(View.GONE);

                TouchImageView map_1 = (TouchImageView) findViewById(R.id.map_1);
                //map_1.setImageBitmap(svgToBitmap(getResources(), R.raw.splatter, 300, 200));
                map_1.setImageResource(R.drawable.map);

                mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        Toast.makeText(getApplicationContext(), "onLongPress", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        Toast.makeText(getApplicationContext(), "onFling", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                break;
            }
            case 2: {
                RelativeLayout rl_1 = (RelativeLayout) findViewById(R.id.layout_map_1);
                rl_1.setVisibility(View.GONE);

                RelativeLayout rl_2 = (RelativeLayout) findViewById(R.id.layout_map_2);
                rl_2.setVisibility(View.VISIBLE);

                VoronImageView vImageView = (VoronImageView) findViewById(R.id.map_2);
                vImageView.setImageResource(R.drawable.map);

                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Bitmap svgToBitmap(Resources res, int resource, int width, int height) {
        try {
            width = (int) (width * res.getDisplayMetrics().density);
            height = (int) (height * res.getDisplayMetrics().density);
            SVG svg = SVG.getFromResource(res, resource);

            Bitmap bmp;
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            svg.renderToCanvas(canvas);

            return bmp;
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
        // Return true if you have consumed the event, false if you haven't.
        // The default implementation always returns false.
    }
}
