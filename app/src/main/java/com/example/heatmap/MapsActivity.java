package com.example.heatmap;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Incidences> listIncidents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new GetIncidences().execute();


    }



    private void addHeatMap() {

        int height = 5;
        int width = 5;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        for (int i=0;i<listIncidents.size();i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
            m.alpha(0);
            m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            mMap.addMarker(m);
        }


        List<LatLng> list = new ArrayList<>();

//        List<String> type = new ArrayList<>();
        HashMap<String,Integer> typeColor = new HashMap<>();

        for (int i=0;i<listIncidents.size();i++){

            if (!(typeColor.keySet().contains(listIncidents.get(i).getIncidentTypePrimary()))){
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                typeColor.put(listIncidents.get(i).getIncidentTypePrimary(),color);
            }
        }

        // Get the data: latitude/longitude positions of police stations.

        for (int i=0;i<listIncidents.size();i++){
            list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
        }


        mMap.setInfoWindowAdapter(new CustomInfoWindow(MapsActivity.this,listIncidents,typeColor));



        Log.v("tag","size "+list.size());


        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);


        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay vmOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        mProvider.setOpacity(0.7);
        vmOverlay.clearTileCache();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0),14));

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private class GetIncidences extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MapsActivity.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");



            Request request = new Request.Builder()
                    .url("http://129.174.126.176:8080/api/crimerate/Lati=38.9236/Longi=-77.5211")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();


                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();

            if (result != null) {
                try {
                    JSONArray ParentjObject = new JSONArray(result);

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Incidences>>(){}.getType();
                    listIncidents.addAll((Collection<? extends Incidences>) gson.fromJson(ParentjObject.toString(), type));
                    addHeatMap();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
