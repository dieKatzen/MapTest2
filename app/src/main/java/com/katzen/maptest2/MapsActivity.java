package com.katzen.maptest2;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
//        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
//        GoogleMap.OnMapClickListener,
//        GoogleMap.OnMarkerClickListener
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private String desc;

    public void setCountryData(Country[] countryData) {
        this.countryData = countryData;
    }

    private Country [] countryData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapLongClickListener(this);//must set Listeners during Oncreate
        CountryDataCollector countries = new CountryDataCollector();    //Async thing
        try {
            String stall= countries.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        LatLng canP = new LatLng(58.473099, -98.310674);
            for(int cnt = 0;cnt<201;cnt++) {
                if(!countryData[cnt].getName().equals("World") && countryData[cnt].getLatLng().longitude != 0 && countryData[cnt].getLatLng().latitude != 0){
                    drawCircle(countryData[cnt].getLatLng(), countryData[cnt].getCurrentPopulation());
                }
            }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

//    private void setUpMapIfNeeded() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//    }

    @Override
    public void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

               @Override
               public void onLocationChanged(Location location) {
                   handleNewLocation(location);
               }
    public void handleNewLocation(Location location){
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude,currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void drawCircle( LatLng location, Double pop ) {
        CircleOptions options = new CircleOptions();
        options.center(location);
        //Radius in meters
        options.radius(Math.sqrt(pop)*20);
        options.fillColor(0x40ff0000);
        options.strokeColor(Color.YELLOW);
        options.strokeWidth((3));
        mMap.addCircle(options);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.defaultMarker());

        mMap.addMarker(options);
    }

            private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
            return address;
        }
                return address;
    }

            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(getAddressFromLatLng(latLng));

                options.icon( BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource( getResources(),
                                R.drawable.heart295 ) ) );

                mMap.addMarker(options);

            }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.katzen.maptest2/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.katzen.maptest2/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    public class CountryDataCollector extends AsyncTask<Void,Void,String> {


        private Exception exception;
        JSONArray countryList;
        JSONArray countryPopulation;
        Calendar date = Calendar.getInstance();
        int countryLength;
        String totalPopulation= "total_population";

//    public void arrayMaker(){
//        for(int cnt = 0;cnt<201;cnt++){
//                countryData[cnt].setName(countryList.getString(cnt));
//                Log.d("check", "c");
//            }
//            Log.d("COUNTRIES0", countryData[23].getName());
//    }

        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(Void... params) {
            String jsonStr = "";
            String jsonStr2 = "";
            try {
                //Pull's list of country names
                URL urlCountries = new URL("http://api.population.io:80/1.0/countries");
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlCountries.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String countriesContent;
                StringBuilder stringBuilder = new StringBuilder();
                while ((countriesContent = bufferedReader.readLine()) != null) {
                    stringBuilder.append(countriesContent).append("\n");
                }
                bufferedReader.close();
                jsonStr = stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    countryList = jsonObject.getJSONArray("countries");
                    Log.d("COUNTRIES", countryList.toString());
                    Log.d("COUNTRIES0", countryList.getString(0));
                    countryLength = countryList.length();
                    Log.d("check", "a");
                    countryData = new Country[countryLength];
                    for (int cnt = 0; cnt < countryLength; cnt++) {
                        countryData[cnt] = new Country();
                    }
                    String i = "" + countryLength;
                    Log.d("check", i);
                    for (int cnt = 0; cnt < countryLength; cnt++) {
                        countryData[cnt].setName(countryList.getString(cnt));
                        Log.d("check", "c");
                    }
                    Log.d("COUNTRIES0", countryData[23].getName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int cnt = 0; cnt < countryData.length; cnt++) {
                try {
                    //pull's list of countries population
                    URL urlPopulation = new URL("http://api.population.io/1.0/population/" + countryData[cnt].getName() + "/today-and-tomorrow/");
                    Log.d("check", "test1");
                    HttpURLConnection httpURLConnection1 = (HttpURLConnection) urlPopulation.openConnection();

                    // pull's from geocode for lat long
                    URL urlGeocode = new URL("https://maps.googleapis.com/maps/api/geocode/json?&address=" + countryData[cnt].getName().replaceAll(" ", "%20")); //"&key=AIzaSyBtHngE-QOXdpjrJjZcZX2_nPQraDlETvY"
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) urlGeocode.openConnection();

                    BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(httpURLConnection1.getInputStream()));
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpURLConnection2.getInputStream()));
                    String countriesContent;
                    String latlngContent = "";
                    StringBuilder stringBuilder1 = new StringBuilder();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    while ((countriesContent = bufferedReader1.readLine()) != null) {
                        stringBuilder1.append(countriesContent).append("\n");
                    }
                    while ((latlngContent = bufferedReader2.readLine()) != null) {
                        stringBuilder2.append(latlngContent).append("\n");
                    }
                    bufferedReader1.close();
                    bufferedReader2.close();
                    jsonStr = stringBuilder1.toString();
                    jsonStr2 = stringBuilder2.toString();
                    Log.d("check", "test");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (jsonStr != null) {
                    String countryPopulation;
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonStr);
                        countryPopulation = jsonObject1.getJSONArray(totalPopulation).getJSONObject(1).getString("population");
                        Double pop = Double.parseDouble(countryPopulation);
                        countryData[cnt].setCurrentPopulation(pop);
                        Log.d("check", countryPopulation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                    if (jsonStr2 != null) {
                        Log.d("pass", "pass");
                        try {
                            JSONObject jsonObject2 = new JSONObject(jsonStr2);
                            double lat = Double.parseDouble(jsonObject2.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat"));
                            double lng = Double.parseDouble(jsonObject2.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng"));

                            countryData[cnt].setLatLng(lat, lng);
                            Log.d("pass", "lat = " + lat + "lng = " + lng);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

//                try {
//                    URL urlGeocode = new URL("https://maps.googleapis.com/maps/api/geocode/json?&address=" + countryData[cnt].getName().replaceAll(" ","&20") + "&key=AIzaSyBtHngE-QOXdpjrJjZcZX2_nPQraDlETvY");
//                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) urlGeocode.openConnection();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Scanner scanner = null;

                // previous latitude retreival method from text file
//            try {
//                InputStream in = getResources().getAssets().open("latlng_b.txt");
//                scanner = new Scanner(in);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int cnt = 0;
//            while(scanner.hasNext()){
//                countryData[cnt].setLatLng(scanner.nextDouble(), scanner.nextDouble());
//                Log.d("checkLong", "" + countryData[cnt].getLatLng().latitude + "" + countryData[cnt].getLatLng().longitude + " " + countryData[cnt].getName() + " " + countryData[cnt].getCurrentPopulation());
//                cnt++;
//            }
//            scanner.close();

//            for (int cnt1 = 0;cnt1<cnt;cnt1++) {
//                drawCircle(countryData[cnt].getLatLng(),countryData[cnt].getCurrentPopulation()/10);
            }
                return "";
            }


        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            MapsActivity.this.setCountryData(countryData);

//            readUrl(httpURLConnection1){

        }
//        protected void parseLatitudeLongitude() throws FileNotFoundException {
//            Scanner scanner = new Scanner (new FileInputStream("latlng_a.txt"));
//            int cnt = 0;
//            while(scanner.hasNext()){
//                countryData[cnt].setLatLng(scanner.nextDouble(),scanner.nextDouble());
//                Log.d("checkLong",  ""+countryData[cnt].getLatLng().longitude+""+countryData[cnt].getLatLng().latitude);
//                cnt++;
//            }scanner.close();
//        }
        }}