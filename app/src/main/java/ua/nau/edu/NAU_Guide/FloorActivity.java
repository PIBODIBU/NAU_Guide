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

import ua.nau.edu.Systems.TouchImageView;

public class FloorActivity extends BaseNavigationDrawerActivity {
    public FloorActivity() {
    }

    Bitmap svgBitmap;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_test);

        //TouchImageView map_1 = (TouchImageView) findViewById(R.id.map_1);
        //map_1.setImageBitmap(svgToBitmap(getResources(), R.raw.splatter, 300, 200));
        //map_1.setImageResource(R.drawable.map);
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
}
