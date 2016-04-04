package com.cheese.jinwooklee.interfacedemo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jinwooklee on 16-03-30.
 */
public class ApiConnection {
    private myApiCALLLisenter apiCALLLisenter;

    public ApiConnection(){
        //ignoring constructor call
        this.apiCALLLisenter = null;

        //check for last row comparison
        lastrow task = new lastrow();
        task.execute("http://afternoon-garden-52459.herokuapp.com/api/get/lastrow");
    }
    public void downloadContent(){
        //Do the downloadtask in here
        DownloadTask task = new DownloadTask();
        task.execute("http://afternoon-garden-52459.herokuapp.com/api/get/all");
    }

    public void setApiCALL(myApiCALLLisenter apiCALL) {
        //parents class should call this in order to make onRequested and on DataLoaded
        this.apiCALLLisenter = apiCALL;
    }

    public interface myApiCALLLisenter {
        //going to pass string to connect with the server
        void onLastRow(ArrayList<HashMap<String, String>> lastrow);
        void onDataLoaded(JSONObject jsonObject);
        void onComplete();
    }

    public class lastrow extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String virusdata = jsonObject.getString("list");

                JSONArray data = new JSONArray(virusdata);
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                HashMap<String, String> hashMap = new HashMap<>();
                for (int i = 0; i< data.length(); i++){
                    JSONObject jsonpart = data.getJSONObject(i);
                    hashMap.put("virusname", jsonpart.getString("virusname"));
                    hashMap.put("country", jsonpart.getString("country"));
                    hashMap.put("lastupdated", jsonpart.getString("lastupdated"));
                    arrayList.add(hashMap);
                }
                apiCALLLisenter.onLastRow(arrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);


                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String virusdata = jsonObject.getString("list");

                JSONArray data = new JSONArray(virusdata);

                for (int i = 0; i< data.length(); i++){
                    JSONObject jsonpart = data.getJSONObject(i);
                    apiCALLLisenter.onDataLoaded(jsonpart);
                }

                apiCALLLisenter.onComplete();



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
