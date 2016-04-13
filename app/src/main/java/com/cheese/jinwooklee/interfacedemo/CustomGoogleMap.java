package com.cheese.jinwooklee.interfacedemo;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jinwooklee on 16-03-30.
 */
public class CustomGoogleMap implements OnMapReadyCallback{

    private GoogleMap mMap;
    private GoogleDataListener gl;
    private Context context;
    private Boolean clear;
    private Activity activity;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        gl.mapLoaded();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.setInfoWindowAdapter(new googlemapInfo(activity, marker.getTitle()));
                return false;
            }
        });
    }

    protected void placeMarker(ArrayList<HashMap<String, LatLng>> s){

        if(clear){
            clearMarkers();
        }

        for(int i = s.size()-1; i >= 0; i--){
            Iterator<String> IS = s.get(i).keySet().iterator();
            String country= IS.next();
            LatLng latLng = s.get(i).get(country);
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(country)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if(i == s.size()-1){
                cameraZOOM(s.get(i).get(country));
            }
        }
        gl.onJobComplete("Done");
    }

    protected void cameraZOOM(LatLng s){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s, 2));
    }

    protected void clearMarkers(){
        mMap.clear();
    }

    public CustomGoogleMap(Context context, Activity activity){
        this.gl = null;
        this.context = context;
        this.activity = activity;
    }

    public interface GoogleDataListener{
        public void mapLoaded();
        public void onJobComplete(String s);
    }

    public void setGl(GoogleDataListener g_l){
        this.gl = g_l;
    }

    public void startReverse(ArrayList<HashMap<String, String>> arrayList, Boolean clear){
        this.clear = clear;
        ReverseGeoCoding RG = new ReverseGeoCoding(context);
        RG.execute(arrayList);
    }

    protected class ReverseGeoCoding extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, LatLng>>>{
        private Context context;
        public ReverseGeoCoding(Context context){
            this.context = context;
        }

        @Override
        protected ArrayList<HashMap<String, LatLng>> doInBackground(ArrayList<HashMap<String, String>>... params) {

            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            List<Address> address;
            int size = params[0].size();

            ArrayList<HashMap<String, LatLng>> arrayListLatLng = new ArrayList<>(size);

            for(int i  = 0; i < size; i++ ){
                String country = params[0].get(i).get("country");
                try {
                    address = geocoder.getFromLocationName(country, 1);
                    if(address != null && address.size() > 0){
                        Address location = address.get(0);
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

                        //Log.i("ROfl",arrayListLatLng.get(i).get("China").toString());
                        HashMap<String, LatLng> temp = new HashMap<>();
                        temp.put(country,point);

                        arrayListLatLng.add(temp);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return arrayListLatLng;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, LatLng>> arrayListLatLng) {
            super.onPostExecute(arrayListLatLng);
            if(arrayListLatLng != null && arrayListLatLng.size() > 0){
                placeMarker(arrayListLatLng);
            }
        }
    }
}
