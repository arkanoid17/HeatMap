package com.example.heatmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.material.navigation.NavigationView;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Incidences> listIncidents = new ArrayList<>();

    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;
    TileOverlay vmOverlay;
    Dialog dialog;

    String category = "All";
    HashMap<String,Integer> typeColor = new HashMap<>();

    TextView tvType,tvCount,tvTypeDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvType = findViewById(R.id.tv_type);
        tvCount = findViewById(R.id.tv_count);
        tvTypeDesc = findViewById(R.id.tv_type_desc);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.bringToFront();


        setupToolbar();


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.fbt:
                        mDrawerLayout.closeDrawers();
                        showDialog("type");
                        category = "type";
                        break;
                    case R.id.fbc:
                        showDialog("city");
                        mDrawerLayout.closeDrawers();
                        category = "city";
                        break;
                    case R.id.fbi:
                        showDialog("id");
                        mDrawerLayout.closeDrawers();
                        category = "id";
                        break;
                }
                return true;
            }

        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();







        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new GetIncidences().execute();


    }

    void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    void showDialog(String s){
         dialog = new Dialog(this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_filter);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        RecyclerView rv = dialog.findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));

        switch (s){
            case "type":
                List<String> list = new ArrayList<>();
                list.clear();
                for (int i=0;i<listIncidents.size();i++){
                    if (!(list.contains(listIncidents.get(i).getParentIncidentType()))){
                        list.add(listIncidents.get(i).getParentIncidentType());
                    }
                }

                DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this,list);

                rv.setAdapter(adapter);
                androidx.appcompat.widget.SearchView sv = dialog.findViewById(R.id.sv);
                ((EditText)sv.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.black));
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                    adapter.getFilter().filter(s);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return true;
                    }
                });
                break;
            case "city":
                List<String> list1 = new ArrayList<>();
                list1.clear();
                for (int i=0;i<listIncidents.size();i++){
                    if(!(list1.contains(listIncidents.get(i).getCity()))) {
                        list1.add(listIncidents.get(i).getCity());
                    }

                }

                DrawerItemCustomAdapter adapter1 = new DrawerItemCustomAdapter(this,list1);

                rv.setAdapter(adapter1);
                androidx.appcompat.widget.SearchView sv1 = dialog.findViewById(R.id.sv);
                ((EditText)sv1.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.black));
                sv1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        adapter1.getFilter().filter(s);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter1.getFilter().filter(s);
                        return true;
                    }
                });
                break;
            case "id":
                List<String> list2 = new ArrayList<>();
                list2.clear();
                for (int i=0;i<listIncidents.size();i++){
                        list2.add(listIncidents.get(i).getIncidentId());
                }

                DrawerItemCustomAdapter adapter2 = new DrawerItemCustomAdapter(this,list2);

                rv.setAdapter(adapter2);
                androidx.appcompat.widget.SearchView sv2 = dialog.findViewById(R.id.sv);
                ((EditText)sv2.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.black));
                sv2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        adapter2.getFilter().filter(s);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter2.getFilter().filter(s);
                        return true;
                    }
                });
                break;



        }

    }


    private void addHeatMap(String type) {

        if(vmOverlay!=null){
            vmOverlay.remove();
            mMap.clear();
        }

        int height = 5;
        int width = 5;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);



        List<LatLng> list = new ArrayList<>();

//        List<String> type = new ArrayList<>();



        // Get the data: latitude/longitude positions of police stations.

        switch (category){
            case "All":
                tvTypeDesc.setText("Incident Type");
                for (int i=0;i<listIncidents.size();i++) {
                    list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude())));
                    MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
                    m.alpha(0);
                    m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(m);
                }
                break;
            case "type":
                tvTypeDesc.setText("Incident Type");
                for (int i=0;i<listIncidents.size();i++){
                    if(type.equalsIgnoreCase(listIncidents.get(i).getParentIncidentType())){
                        list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
                        MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
                        m.alpha(0);
                        m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mMap.addMarker(m);
                    }
                }
                break;
            case "city":
                tvTypeDesc.setText("Incident City");

                for (int i=0;i<listIncidents.size();i++){
                    if(type.equalsIgnoreCase(listIncidents.get(i).getCity())){
                        list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
                        MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
                        m.alpha(0);
                        m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mMap.addMarker(m);
                    }
                }
                break;
            case "id":
                tvTypeDesc.setText("Incident Id");

                for (int i=0;i<listIncidents.size();i++){
                    if(type.equalsIgnoreCase(listIncidents.get(i).getIncidentId())){
                        list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
                        MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
                        m.alpha(0);
                        m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mMap.addMarker(m);
                    }
                }
            break;


        }

//        if (type.equalsIgnoreCase("All")){
//            for (int i=0;i<listIncidents.size();i++){
//                list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
//            }
//        }else {
//            for (int i=0;i<listIncidents.size();i++){
//
//                if(type.equalsIgnoreCase(listIncidents.get(i).getIncidentTypePrimary())){
//                    list.add(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()),Double.parseDouble(listIncidents.get(i).getLongitude())));
//                }
//
//            }
//        }

//
//        for (int i=0;i<listIncidents.size();i++) {
//            if (type.equalsIgnoreCase(listIncidents.get(i).getIncidentTypePrimary())){
//                MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
//                m.alpha(0);
//                m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                mMap.addMarker(m);
//            }else{
//                if (type.equalsIgnoreCase("All")){
//                    MarkerOptions m = new MarkerOptions().position(new LatLng(Double.parseDouble(listIncidents.get(i).getLatitude()), Double.parseDouble(listIncidents.get(i).getLongitude()))).title(listIncidents.get(i).getIncidentId());
//                    m.alpha(0);
//                    m.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                    mMap.addMarker(m);
//                }
//            }
//        }


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
         vmOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        mProvider.setOpacity(0.7);
        vmOverlay.clearTileCache();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0),14));
        tvType.setText(type);
        tvCount.setText(list.size()+" " );

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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                }
            },2000);

            if (result != null) {
                try {
                    JSONArray ParentjObject = new JSONArray(result);

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Incidences>>(){}.getType();
                    listIncidents.addAll((Collection<? extends Incidences>) gson.fromJson(ParentjObject.toString(), type));
                    addHeatMap("All");
                    List<String> list = new ArrayList<>();
                    list.clear();
                    list.add("All");
                    for (int i = 0;i<listIncidents.size();i++){
                        if (!(list.contains(listIncidents.get(i).getIncidentTypePrimary()))){
                            list.add(listIncidents.get(i).getIncidentTypePrimary());
                        }
                    }


                    for (int i=0;i<listIncidents.size();i++){

                        if (!(typeColor.keySet().contains(listIncidents.get(i).getIncidentTypePrimary()))){
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            typeColor.put(listIncidents.get(i).getIncidentTypePrimary(),color);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public class DrawerItemCustomAdapter extends RecyclerView.Adapter<DrawerItemCustomAdapter.ViewHolder> implements Filterable {

        Context mContext;
        List<String> data;
        List<String> list_filtered;
        public DrawerItemCustomAdapter(Context mContext,  List<String> data) {
            this.mContext = mContext;
            this.data = data;
            this.list_filtered = data;
        }

        @NonNull
        @Override
        public DrawerItemCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MapsActivity.this).inflate(R.layout.list_view_item_row,parent,false));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull DrawerItemCustomAdapter.ViewHolder holder, int position) {
            holder.textViewName.setText(list_filtered.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.closeDrawers();
                    addHeatMap(list_filtered.get(position));
                    dialog.dismiss();
                }
            });
        }


        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    Log.v("TAG", string);
                    if (string.isEmpty()) {
                        list_filtered = data;
                    } else {
                        List<String> filteredList = new ArrayList<>();
                        for (String s : data) {
                            if (s.toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }
                        list_filtered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<String>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
            }
        }
    }

}
