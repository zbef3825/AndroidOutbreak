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

/**
 * Created by jinwooklee on 16-03-30.
 */
public class ApiConnection {
    private myApiCALLLisenter apiCALLLisenter;

    public ApiConnection(){
        //ignoring constructor call
        this.apiCALLLisenter = null;


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
        public void onRequest(String string);
        public void onDataLoaded(JSONObject jsonObject);
        public void onComplete();
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
                apiCALLLisenter.onRequest("URL Connection Established");
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

                //temprary 5 items instead of so many at once.
                //Usually it's data.length() for the limit
                for (int i = 0; i< 4; i++){
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
