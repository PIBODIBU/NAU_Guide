package ua.nau.edu.NAU_Guide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import ua.nau.edu.Systems.TouchImageView;

public class FloorActivity extends BaseToolbarActivity {
    public FloorActivity() {
    }

    Bitmap svgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        getToolbar();

        /*TouchImageView map = (TouchImageView) findViewById(R.id.map);
        //map_1.setImageBitmap(svgToBitmap(getResources(), R.raw.splatter, 300, 200));
        map.setImageResource(R.drawable.map);*/

        SubsamplingScaleImageView map = (SubsamplingScaleImageView) findViewById(R.id.map);
        map.setImage(ImageSource.resource(R.drawable.map_1));
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

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            svg.renderToCanvas(canvas);

            return bmp;
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
