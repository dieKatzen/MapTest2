//package com.katzen.maptest2;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Calendar;
//import java.util.Date;
//
///**
// * Created by Katzen on 2016-01-08.
// * Tutorial for this practice is from http://www.androidauthority.com/use-remote-web-api-within-android-app-617869/
// */
//public class CountryDataCollector extends AsyncTask<Void,Void,String> {
//
//
//    private Exception exception;
//    JSONArray countryList;
//    JSONArray countryPopulation;
//    Calendar date = Calendar.getInstance();
//    Country[] countryData;
//    int countryLength;
//    String totalPopulation= "total_population";
//
////    public void arrayMaker(){
////        for(int cnt = 0;cnt<201;cnt++){
////                countryData[cnt].setName(countryList.getString(cnt));
////                Log.d("check", "c");
////            }
////            Log.d("COUNTRIES0", countryData[23].getName());
////    }
//
//    protected void onPreExecute(){
//
//    }
//
//    @Override
//    protected String doInBackground(Void... params) {
//        String jsonStr = "";
//        try {
//            //Pull's list of country names
//            URL urlCountries = new URL ("http://api.population.io:80/1.0/countries");
//            HttpURLConnection httpURLConnection = (HttpURLConnection) urlCountries.openConnection();
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//            String countriesContent;
//            StringBuilder stringBuilder = new StringBuilder();
//            while((countriesContent = bufferedReader.readLine()) != null){
//                stringBuilder.append(countriesContent).append("\n");
//            }
//            bufferedReader.close();
//           jsonStr = stringBuilder.toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    if(jsonStr != null) {
//        try {
//            JSONObject jsonObject = new JSONObject(jsonStr);
//            countryList = jsonObject.getJSONArray("countries");
//            Log.d("COUNTRIES", countryList.toString());
//            Log.d("COUNTRIES0", countryList.getString(0));
//            countryLength = countryList.length();
//            Log.d("check", "a");
//            countryData = new Country[countryLength];
//            for (int cnt=0;cnt<countryLength;cnt++){
//                countryData[cnt]= new Country();
//            }
//            String i = ""+countryLength;
//            Log.d("check", i);
//            for(int cnt = 0;cnt<countryLength;cnt++){
//                countryData[cnt].setName(countryList.getString(cnt));
//                Log.d("check", "c");
//            }
//            Log.d("COUNTRIES0", countryData[23].getName());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//        for(int cnt=0;cnt<countryData.length;cnt++) {
//            try {
//                //pull's list of countries population
//                String popString = "http://api.population.io/1.0/population/"+countryData[cnt].getName()+"/today-and-tomorrow/";
//                Log.d("check",countryData[cnt].getName());
//                URL urlPopulation = new URL(popString);
//                Log.d("check", "test1");
//                HttpURLConnection httpURLConnection1 = (HttpURLConnection) urlPopulation.openConnection();
//
//                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(httpURLConnection1.getInputStream()));
//                String countriesContent;
//                StringBuilder stringBuilder1 = new StringBuilder();
//                while ((countriesContent = bufferedReader1.readLine()) != null) {
//                    stringBuilder1.append(countriesContent).append("\n");
//                }
//                bufferedReader1.close();
//                jsonStr = stringBuilder1.toString();
//                Log.d("check", "test");
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (jsonStr != null) {
//                String countryPopulation;
//                try {
//                    JSONObject jsonObject1 = new JSONObject(jsonStr);
//                    countryPopulation = jsonObject1.getJSONArray(totalPopulation).getJSONObject(1).getString("population");
//                    Double pop = Double.parseDouble(countryPopulation);
//                    countryData[cnt].setCurrentPopulation(pop);
//                    Log.d("check", countryPopulation);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return "";
//    }
//
//    protected void onPostExecute(String response) {
//        if(response == null) {
//            response = "THERE WAS AN ERROR";
//        }
//
//        MapsActivity.this.setCountryData(countryData);
//
////            readUrl(httpURLConnection1){
//
//    }
//
//
//
////        int cl = countryList.length();
////            for(int cnt = 0;cnt<cl;cnt++){
////                try {
////                    countryData[cnt].setName(countryList.getString(cnt));
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////                Log.d("check", "c");
////            }
////        for(int cnt = 0;cnt<3;cnt++){
////                response.Log.d("check", "c");
////            }
//
//    }
//
//
////    {
////        for(int cnt=0; cnt< countryList.length();cnt++){
////            String country = "";
////            String httpLink;
////            try {
////                country = countryList.getString(cnt);
////                countryData[cnt].setName(country);
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////
////            URL clink = null;
////            try {
////                clink = new URL ("http://api.population.io:80/1.0/population/"+country+"/"+date.get(Calendar.YEAR)+"-"+date.get(Calendar.MONTH)+"-"+date.get(Calendar.DAY_OF_MONTH)+"/");
////                Log.d("DATE", "http://api.population.io:80/1.0/population/"+country+"/"+date.get(Calendar.YEAR)+"-"+date.get(Calendar.MONTH)+"-"+date.get(Calendar.DAY_OF_MONTH)+"/");
////                HttpURLConnection httpURLConnection = (HttpURLConnection) clink.openConnection();
////            } catch (MalformedURLException e) {
////                e.printStackTrace();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////
////        }
////    }
//
//
//
//
