package com.example.heatmap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View view;
    private Context  context;
    List<Incidences> listIncidents;
    HashMap<String,Integer> typeColor;


    public CustomInfoWindow(Context context,List<Incidences> listIncidents, HashMap<String,Integer> typeColor) {
        this.view = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null,false);
        this.context = context;
        this.listIncidents = listIncidents;
        this.typeColor = typeColor;
    }

    void renderWindowText(Marker marker,View view){
        String id = marker.getTitle();

        Drawable d = view.getBackground();


        TextView tvId = view.findViewById(R.id.tv_incident_id);
        TextView tvDateTime = view.findViewById(R.id.tv_incident_date_time);
        TextView tvType = view.findViewById(R.id.tv_incident_type);
        TextView tvDescript = view.findViewById(R.id.tv_incident_date_description);

        for (int i=0;i<listIncidents.size();i++){
            if (listIncidents.get(i).getIncidentId().equalsIgnoreCase(id)){
                tvId.setText(Html.fromHtml("Incident ID:  "+"<b>"+id+"</b>"));
                tvDateTime.setText(Html.fromHtml("Incident Date:  "+"<b>"+listIncidents.get(i).getIncidentDatetime()+"</b>"));
                tvType.setText(Html.fromHtml("Incident Type:  "+"<b>"+listIncidents.get(i).getIncidentTypePrimary()+"</b>"));
                tvDescript.setText(Html.fromHtml("Incident Description:  "+"<b>"+listIncidents.get(i).getIncidentDescription()+"</b>"));
                for (String keys :typeColor.keySet()){
                    if (keys.equalsIgnoreCase(listIncidents.get(i).getIncidentTypePrimary())){
                        view.setBackgroundResource(0);
                        GradientDrawable border = new GradientDrawable();
                        border.setColor(0xFFFFFFFF); //white background
                        border.setStroke(5, typeColor.get(keys));
                        view.setBackground(border);
                        Log.v("tag","typeColor  "+keys+" "+typeColor.get(keys));
                    }
                }
            }


        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,view);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,view);
        return view;
    }
}
