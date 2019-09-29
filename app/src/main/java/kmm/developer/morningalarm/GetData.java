package kmm.developer.morningalarm;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_WEEK;

/**
 * Created by Kevin on 8/7/2017.
 */

public class GetData extends AsyncTask<Void, Void, ArrayList>  {
    private String topArticle, secondArticle, thirdArticle, weatherCondition, surfInfoSpot1, surfInfoSpot2;
    private int windSpeed, airTemp;
    private int waterTemp;
    private double surfSizeSpot1, surfSizeSpot2;
    //pismo beach 162
    int firstSpotId = 162;

    //morro bay 163
    int secondSpotId = 163;

    protected ArrayList doInBackground(Void... urls) {
        //String email = emailText.getText().toString();
        // Do some validation here
        ArrayList<String> outputArray = new ArrayList<>();
        try {

            String API_URL = "https://newsapi.org/v1/articles?source=cnn&sortBy=top";
            String API_KEY = "bb67b75c0918495bbc46e5b65c38271e";

            /*I used to want to use gps location, but this is risky becaouse sometimes you can't find the gps signal
            to get coordinates indoors*/
            //String location = "(" + MainActivity.latitude + "," + MainActivity.longitude + ")";
            //String locationForApixu = MainActivity.latitude + "," + MainActivity.longitude;
            //String YahooEndpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));
            //String WeatherBackUp = "http://api.apixu.com/v1/current.json?key=9a46bba093da424ab28210855160412&q=" + locationForApixu;

            String spitcastSurf = "http://api.spitcast.com/api/spot/forecast/"+firstSpotId+"/";
            String spitcastSurf2 = "http://api.spitcast.com/api/spot/forecast/"+secondSpotId+"/";
            String spitcastWaterTemp = "http://api.spitcast.com/api/county/water-temperature/"+"san-luis-obispo"+"/";
            String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='" + "f" + "'", "san-luis-obispo");
            String YahooEndpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));
            String WeatherBackUp = "http://api.apixu.com/v1/current.json?key=9a46bba093da424ab28210855160412&q=" + "san-luis-obispo";

            URL YAHOO_WEATHER_URL = new URL(YahooEndpoint);
            URL url = new URL(API_URL + "&apiKey=" + API_KEY);
            URL backupWeather = new URL(WeatherBackUp);
            URL spitcastWaterTempURL = new URL(spitcastWaterTemp);
            URL spitcastSurfURL = new URL(spitcastSurf);
            URL spitcastSurfURL2 = new URL(spitcastSurf2);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            HttpURLConnection urlConnection2 = (HttpURLConnection) YAHOO_WEATHER_URL.openConnection();
            HttpURLConnection urlConnection3 = (HttpURLConnection) backupWeather.openConnection();
            HttpURLConnection urlConnection4 = (HttpURLConnection) spitcastWaterTempURL.openConnection();
            HttpURLConnection urlConnectionSurf1 = (HttpURLConnection) spitcastSurfURL.openConnection();
            HttpURLConnection urlConnectionSurf2 = (HttpURLConnection) spitcastSurfURL2.openConnection();

            String return1 = null;
            String return2 = null;
            String return3 = null;
            String return4 = null;
            String returnSurf1 = null;
            String returnSurf2 = null;
                //using news

                try {
                    //news----------------------
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder1 = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder1.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return1 = stringBuilder1.toString();
                } finally {
                    urlConnection.disconnect();
                }

            try{
                //weather-------------------
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
                StringBuilder stringBuilder2 = new StringBuilder();
                String line2;
                while ((line2 = bufferedReader2.readLine()) != null) {
                    stringBuilder2.append(line2).append("\n");
                }
                bufferedReader2.close();
                return2 = stringBuilder2.toString();

                if(return2.contains("\"results:null\"")){
                    Log.e("ERROR", "Results will be null");
                }

                //Pair<String, String> returnVal = new Pair<String, String>(stringBuilder1.toString(), stringBuilder2.toString());
            }
            finally{
                urlConnection2.disconnect();
            }
            try{
                //use backup weather
                BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(urlConnection3.getInputStream()));

                //weather-------------------
                StringBuilder stringBuilder2 = new StringBuilder();
                String line2;
                while ((line2 = bufferedReader3.readLine()) != null) {
                    stringBuilder2.append(line2).append("\n");
                }
                bufferedReader3.close();
                return3 = stringBuilder2.toString();

            }
            finally{
                urlConnection3.disconnect();
            }
            try{
                //get surf information (water temp)
                BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(urlConnection4.getInputStream()));

                //weather-------------------
                StringBuilder stringBuilder2 = new StringBuilder();
                String line2;
                while ((line2 = bufferedReader3.readLine()) != null) {
                    stringBuilder2.append(line2).append("\n");
                }
                bufferedReader3.close();
                return4 = stringBuilder2.toString();

            }
            finally{
                urlConnection4.disconnect();
            }

            try{
                //get surf information (surfsize)
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnectionSurf1.getInputStream()));

                //surf size-------------------
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                returnSurf1 = stringBuilder.toString();

            }
            finally{
                urlConnectionSurf1.disconnect();
            }
            try{
                //get surf information (surfsize)
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnectionSurf2.getInputStream()));

                //surf size-------------------
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                returnSurf2 = stringBuilder.toString();

            }
            finally{
                urlConnectionSurf2.disconnect();
            }
            outputArray.add(return1);
            outputArray.add(return2);
            outputArray.add(return4);
            outputArray.add(return3);
            outputArray.add(returnSurf1);
            outputArray.add(returnSurf2);

            return outputArray;
            //return new Pair<>(return1, return2);

        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(ArrayList input) {
        String response = null;
        String response2 = null;
        String response3 = null;
        String response4 = null;
        String responseSurf1 = null;
        String responseSurf2 = null;


        if(input!=null){
            if(input.get(0)!=null){
                response = input.get(0).toString();
            }
            if(input.get(1)!=null){
                response2 = input.get(1).toString();
            }
            if(input.get(2)!=null){
                response3 = input.get(2).toString();
            }
            if(input.get(3)!=null){
                response4 = input.get(3).toString();
            }
            if(input.get(4)!=null){
                responseSurf1 = input.get(4).toString();
            }
            if(input.get(5)!=null){
                responseSurf2 = input.get(5).toString();
            }
        }

        //if response == null, news is not being used

        //progressBar.setVisibility(View.GONE);
        Log.i("INFO response", response + " ");
        Log.i("INFO response2", response2 + " ");
        Log.i("INFO response3", response3 + " ");


        //responseView.setText(response);
        try {
            if(response!=null){
                try {
                    //if news is being found,
                    JSONObject json = new JSONObject(response);
                    JSONObject j;

                    JSONArray jsonArray = json.getJSONArray("articles");
                    j = jsonArray.getJSONObject(0);
                    topArticle = j.getString("title");

                    j = jsonArray.getJSONObject(1);
                    secondArticle = j.getString("title");

                    j = jsonArray.getJSONObject(2);
                    thirdArticle = j.getString("title");

                }
                catch(Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
            else{
                topArticle=null;
                secondArticle =null;
                thirdArticle = null;
            }

            //if found weather data,
            try {
                if(response2!=null){
                    JSONObject json = new JSONObject(response2);
                    JSONObject channel = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel");

                    windSpeed = channel.getJSONObject("wind").getInt("speed");
                    airTemp = channel.getJSONObject("item").getJSONObject("condition").getInt("temp");
                    weatherCondition = channel.getJSONObject("item").getJSONObject("condition").getString("text");
                    //unitSpeed = channel.getJSONObject("units").getString("speed");
                    //unitTemp = channel.getJSONObject("units").getString("temperature");
                }
                else if (response4!=null){
                    //use backup weather. sometimes yahoos a bitch and twos better than one i guess idk
                    JSONObject json = new JSONObject(response4);
                    JSONObject channel = json.getJSONObject("current");

                    windSpeed = channel.getInt("wind_mph");
                    airTemp = channel.getInt("temp_f");
                    weatherCondition = channel.getJSONObject("condition").getString("text");

                }
            }
            catch(JSONException e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            //if found surf temp data,
            try {
                JSONObject json = new JSONObject(response3);
                //JSONObject channel = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel");

                waterTemp = json.getInt("fahrenheit");
            }
            catch(JSONException e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            //if found surf data,
            try {
                JSONArray jsonArray1 = new JSONArray(responseSurf1);
                JSONArray jsonArray2 = new JSONArray(responseSurf2);

                JSONObject json1 = jsonArray1.getJSONObject(0);
                JSONObject json2 = jsonArray2.getJSONObject(0);

                //JSONObject channel = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel");

                surfSizeSpot1 = json1.getDouble("size_ft");
                surfSizeSpot2 = json2.getDouble("size_ft");

                surfInfoSpot1 = json1.getString("shape_full");
                surfInfoSpot2 = json2.getString("shape_full");

            }
            catch(JSONException e) {
                Log.e("ERROR", e.getMessage(), e);
            }


            MainActivity instance = MainActivity.instance();
            Log.d("GetData", "working");
            instance.setDisplayText(windSpeed, airTemp, weatherCondition, waterTemp, topArticle, secondArticle, thirdArticle, surfSizeSpot1, surfSizeSpot2, surfInfoSpot1, surfInfoSpot2);


        } catch(Exception e){
            Log.e("ERROR", e.getMessage(), e);
            //couldn't get information
            //MainActivity instance = MainActivity.instance();
            //instance.setAlarmText();
        }

    }
}
