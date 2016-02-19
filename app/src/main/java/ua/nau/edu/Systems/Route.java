package ua.nau.edu.Systems;

/**
 * Created by Roman on 01.12.2015.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import ua.nau.edu.NAU_Guide.R;


/**
 * @author zeeshan0026 and Fernando Valle
 *         http://stackoverflow.com/users/1547539/zeeshan0026
 *         http://stackoverflow.com/users/1826257/fernando-valle
 */


//for set language look at the web: https://spreadsheets.google.com/spreadsheet/pub?key=0Ah0xU81penP1cDlwZHdzYWkyaERNc0xrWHNvTTA1S1E&gid=1
// example Spanish "es".

public class Route {
    private GoogleMap mMap;
    private Context context;
    private String lang;
    private int iconId = -99;

    public static String LANGUAGE_SPANISH = "es";
    public static String LANGUAGE_ENGLISH = "en";
    public static String LANGUAGE_FRENCH = "fr";
    public static String LANGUAGE_GERMAN = "de";
    public static String LANGUAGE_RUSSIAN = "ru";
    public static String LANGUAGE_CHINESE_SIMPLIFIED = "zh-CN";
    public static String LANGUAGE_CHINESE_TRADITIONAL = "zh-TW";

    public static String TRANSPORT_DRIVING = "driving";
    public static String TRANSPORT_WALKING = "walking";
    public static String TRANSPORT_BIKE = "bicycling";
    public static String TRANSPORT_TRANSIT = "transit";

    private ArrayList<Polyline> polylines = new ArrayList<>();

    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, boolean withIndications, String language, boolean optimize) {
        mMap = map;
        context = c;
        lang = language;
        if (points.size() == 2) {
            String url = makeURL(points.get(0).latitude, points.get(0).longitude, points.get(1).latitude, points.get(1).longitude, "driving");
            new connectAsyncTask(url, withIndications).execute();
            return true;
        } else if (points.size() > 2) {
            String url = makeURL(points, "driving", optimize);
            new connectAsyncTask(url, withIndications).execute();
            return true;
        }

        return false;

    }

    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, String language, boolean optimize) {
        mMap = map;
        context = c;
        lang = language;
        if (points.size() == 2) {
            String url = makeURL(points.get(0).latitude, points.get(0).longitude, points.get(1).latitude, points.get(1).longitude, "driving");
            new connectAsyncTask(url, false).execute();
            return true;
        } else if (points.size() > 2) {
            String url = makeURL(points, "driving", optimize);
            new connectAsyncTask(url, false).execute();
            return true;
        }

        return false;

    }


    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, String mode, boolean withIndications, String language, boolean optimize) {
        mMap = map;
        context = c;
        lang = language;
        if (points.size() == 2) {
            String url = makeURL(points.get(0).latitude, points.get(0).longitude, points.get(1).latitude, points.get(1).longitude, mode);
            new connectAsyncTask(url, withIndications).execute();
            return true;
        } else if (points.size() > 2) {
            String url = makeURL(points, mode, optimize);
            new connectAsyncTask(url, withIndications).execute();
            return true;
        }

        return false;

    }

    //


    public void drawRoute(GoogleMap map, Context c, LatLng source, LatLng dest, boolean withIndications, String language) {
        mMap = map;
        context = c;

        String url = makeURL(source.latitude, source.longitude, dest.latitude, dest.longitude, "driving");
        new connectAsyncTask(url, withIndications).execute();
        lang = language;

    }


    public void drawRoute(GoogleMap map, Context c, LatLng source, LatLng dest, String language) {
        mMap = map;
        context = c;

        String url = makeURL(source.latitude, source.longitude, dest.latitude, dest.longitude, "driving");
        new connectAsyncTask(url, false).execute();
        lang = language;

    }


    public void drawRoute(GoogleMap map, Context c, LatLng source, LatLng dest, String mode, boolean withIndications, String language) {
        mMap = map;
        context = c;

        String url = makeURL(source.latitude, source.longitude, dest.latitude, dest.longitude, mode);
        new connectAsyncTask(url, withIndications).execute();
        lang = language;

    }

    public void drawRoute(GoogleMap map, Context c, LatLng source, LatLng dest, String mode, boolean withIndications, String language, int ic) {
        mMap = map;
        context = c;
        iconId = ic;

        String url = makeURL(source.latitude, source.longitude, dest.latitude, dest.longitude, mode);
        new connectAsyncTask(url, withIndications).execute();
        lang = language;

    }

    private String makeURL(ArrayList<LatLng> points, String mode, boolean optimize) {
        StringBuilder urlString = new StringBuilder();

        if (mode == null)
            mode = "driving";

        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(points.get(0).latitude);
        urlString.append(',');
        urlString.append(points.get(0).longitude);
        urlString.append("&destination=");
        urlString.append(points.get(points.size() - 1).latitude);
        urlString.append(',');
        urlString.append(points.get(points.size() - 1).longitude);

        urlString.append("&waypoints=");
        if (optimize)
            urlString.append("optimize:true|");
        urlString.append(points.get(1).latitude);
        urlString.append(',');
        urlString.append(points.get(1).longitude);

        for (int i = 2; i < points.size() - 1; i++) {
            urlString.append('|');
            urlString.append(points.get(i).latitude);
            urlString.append(',');
            urlString.append(points.get(i).longitude);
        }


        urlString.append("&sensor=true&mode=" + mode);


        return urlString.toString();
    }

    private String makeURL(double sourcelat, double sourcelog, double destlat, double destlog, String mode) {
        StringBuilder urlString = new StringBuilder();

        if (mode == null)
            mode = "driving";

        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=" + mode + "&alternatives=true&language=" + lang);
        return urlString.toString();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;
        boolean steps;

        connectAsyncTask(String urlPass, boolean withSteps) {
            url = urlPass;
            steps = withSteps;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.maps_dialog_fetching));
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            return jParser.getJSONFromUrl(url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result, steps, iconId);
            }
        }
    }

    private void drawPath(String result, boolean withSteps, int iconId) {
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(5) //Default = 4
                        .color(ContextCompat.getColor(context, R.color.colorAppPrimary))
                        .geodesic(true));
                polylines.add(line);
            }

            if (withSteps) {
                if (iconId == -99)
                    iconId = R.drawable.ic_place_black_24dp;

                JSONArray arrayLegs = routes.getJSONArray("legs");
                JSONObject legs = arrayLegs.getJSONObject(0);
                JSONArray stepsArray = legs.getJSONArray("steps");
                //put initial point

                for (int i = 0; i < stepsArray.length(); i++) {
                    final Step step = new Step(stepsArray.getJSONObject(i));
                    /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder
                                    .setTitle(step.distance)
                                    .setMessage(step.instructions)
                                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            final AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                            .setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                                }
                            });

                            dialog.show();

                            return false;
                        }
                    });*/

                    mMap.addMarker(new MarkerOptions()
                            .position(step.location)
                            .title(step.distance)
                            .snippet(step.instructions)
                            .icon(BitmapDescriptorFactory.fromResource(iconId)));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearPath() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }


    /**
     * Class that represent every step of the directions. It store distance, location and instructions
     */
    private class Step {
        public String distance;
        public LatLng location;
        public String instructions;

        Step(JSONObject stepJSON) {
            JSONObject startLocation;
            try {
                distance = stepJSON.getJSONObject("distance").getString("text");
                startLocation = stepJSON.getJSONObject("start_location");
                location = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));
                try {
                    instructions = URLDecoder.decode(Html.fromHtml(stepJSON.getString("html_instructions")).toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}