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
import java.util.List;
import java.util.Locale;

/**
 * Created by jinwooklee on 16-03-30.
 */
public class CustomeGoogle implements OnMapReadyCallback{

    private GoogleMap mMap;
    private GoogleDataListener gl;
    private Context context;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        gl.mapLoaded();
    }

    protected void placeMarker(LatLng s){
        mMap.addMarker(new MarkerOptions().position(s).title("Point from Main Activity").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s, 2));
        gl.onJobComplete("Done");
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

    public void startReverse(String s){
        ReverseGeoCoding RG = new ReverseGeoCoding(context);
        RG.execute(s);
    }

    protected class ReverseGeoCoding extends AsyncTask<String, Void, LatLng>{

        private Context context;
        public ReverseGeoCoding(Context context){
            this.context = context;
        }

        @Override
        protected LatLng doInBackground(String... s) {
            List<Address> address;
            LatLng point = null;
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            try {
                address = geocoder.getFromLocationName(s[0],1);
                //Log.i("Location typed:", s[0]);
                Address location = address.get(0);
                point = new LatLng(location.getLatitude(), location.getLongitude());
                //Log.i("Location reverse:", String.valueOf(location.getLatitude()));
                //Log.i("Location reverse:", String.valueOf(location.getLongitude()));
                //Log.i("Info", "Got Location!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return point;
        }

        @Override
        protected void onPostExecute(LatLng point) {
            super.onPostExecute(point);
            if (point != null){
                //Log.i("info", "Placing Marker");
                placeMarker(point);
            }
            else {
                //Log.i("Err", "Point is empty btw");
            }
        }
    }
}
