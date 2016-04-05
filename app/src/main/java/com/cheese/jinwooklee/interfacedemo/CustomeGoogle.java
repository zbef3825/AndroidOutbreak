package com.cheese.jinwooklee.interfacedemo;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jinwooklee on 16-03-30.
 */
public class CustomeGoogle implements OnMapReadyCallback{

    private GoogleMap mMap;
    private GoogleDataListener gl;
    private Context context;
    private Boolean clear;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        gl.mapLoaded();
    }

    protected void placeMarker(ArrayList<LatLng> s){
        if(clear){
            clearMarkers();
        }
        for(int i = s.size()-1; i >= 0; i--){
            mMap.addMarker(new MarkerOptions().position(s.get(i)).title("Point from Main Activity").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if(i == s.size()-1){
                cameraZOOM(s.get(i));
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

    public CustomeGoogle(Context context){
        this.gl = null;
        this.context = context;
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

    protected class ReverseGeoCoding extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<LatLng>>{
        private Context context;
        public ReverseGeoCoding(Context context){
            this.context = context;
        }

        @Override
        protected ArrayList<LatLng> doInBackground(ArrayList<HashMap<String, String>>... params) {
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            ArrayList<LatLng> arrayListLatLng = new ArrayList<>();
            List<Address> address;
            int size = params[0].size();

            for(int i  = 0; i < size; i++ ){
                String country = params[0].get(i).get("country");
                try {
                    address = geocoder.getFromLocationName(country, 1);
                    Address location = address.get(0);
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    arrayListLatLng.add(point);
                    //Log.i("Info", "Point added");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return arrayListLatLng;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> arrayListLatLng) {
            super.onPostExecute(arrayListLatLng);
            placeMarker(arrayListLatLng);
        }
    }
}
