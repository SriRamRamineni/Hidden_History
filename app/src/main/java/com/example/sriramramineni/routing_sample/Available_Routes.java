package com.example.sriramramineni.routing_sample;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;
import com.mapbox.geocoder.service.models.GeocoderResponse;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.CoordinateBounds;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Available_Routes extends Activity {
    String responseString;
    static HashMap<String, com.mapbox.mapboxsdk.geometry.LatLng> locations = new HashMap<>();
    private ProgressDialog progressDialog;
    com.mapbox.mapboxsdk.annotations.Marker sourceMarker;
    com.mapbox.mapboxsdk.annotations.Marker destinationMarker;
    com.mapbox.mapboxsdk.annotations.PolylineOptions lineOptions = new com.mapbox.mapboxsdk.annotations.PolylineOptions().width(25).color(Color.argb(100, 50, 0, 250));
    static List<com.mapbox.mapboxsdk.geometry.LatLng> latLngs = new ArrayList<>();
    MapView mapView;
    static  HashMap<com.mapbox.mapboxsdk.geometry.LatLng,String> instructions = new HashMap<>();
    int perRange = 0;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available__routes);
        assert(getActionBar()!=null);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Available Routes");
        moveDrawerToTop();
        initActionBar() ;
        initDrawer();
        instructions.clear();
        Intent intent  = getIntent();
        String place = intent.getStringExtra("com.example.sriramramineni.routing_sample");
        new MyTask().execute(place);
        mapView = (MapView) findViewById(R.id.maplite);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setZoomLevel(11);
        mapView.onCreate(savedInstanceState);
        if(Main_Helper.isCustom)
            perRange = Main_Helper.range;
        else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            perRange = sharedPreferences.getInt("distance_key",500);
        }
        Button button = (Button)findViewById(R.id.route1select);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage(v);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.drawer).setPadding(0, getStatusBarHeight(), 0, 0);

        // Make the drawer replace the first child
        decor.addView(drawer);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initActionBar() {
        actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        List<String> avList = new ArrayList<>();
        avList.add("Main Menu");
        avList.add("Restart Mode");
        avList.add("Refresh Activity");
        avList.add("Settings");
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        ListAdapter adapter = (new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, avList));
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private DrawerLayout.DrawerListener createDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int state) {
            }
        };
        return mDrawerToggle;
    }


    private void sendmessage(View v) {
        Intent intent1 = new Intent(this,swipe_select.class);
        startActivity(intent1);
    }


    // Another worker thread is created to get the route points from the Open Street Routing Machine.
    private class MyTask extends AsyncTask<String,Integer,List<com.mapbox.mapboxsdk.geometry.LatLng>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =  ProgressDialog.show(Available_Routes.this, "", "Getting Route Data...", true);
        }

        @Override
        protected List<com.mapbox.mapboxsdk.geometry.LatLng> doInBackground(String... params) {
            String[] adrs = params[0].split(",");
            Log.i(adrs[0], adrs[1]);
            LatLng adrs0 = null;
            LatLng adrs1 = null;
            try {
                adrs0 = geocoding(adrs[0]);
                adrs1 = geocoding(adrs[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(adrs0.toString(),adrs1.toString());
            latLngs.clear();
            latLngs.add(new com.mapbox.mapboxsdk.geometry.LatLng(adrs0.latitude,adrs0.longitude));
            latLngs.add(new com.mapbox.mapboxsdk.geometry.LatLng(adrs1.latitude,adrs1.longitude));
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("api.mapbox.com").appendPath("v4").appendPath("directions").appendPath("mapbox.driving")
                    .appendPath( String.valueOf(latLngs.get(0).getLongitude()) + "," + String.valueOf(latLngs.get(0).getLatitude())+";"+String.valueOf(latLngs.get(1).getLongitude())+","+String.valueOf(latLngs.get(1).getLatitude())+".json").appendQueryParameter("instructions", "text")
                    .appendQueryParameter("geometry","geojson").appendQueryParameter("steps","true").appendQueryParameter("alternatives","true").appendQueryParameter("access_token","pk.eyJ1Ijoic3JpcmFtcmFtaW5lbmkiLCJhIjoiY2lqYWE4amE0MDA1M3VpbHgxOTd0bDEyMCJ9.OjpBRJ8laYMsqaM4el6BHQ");
            URL url = null;
            try {
                url = new URL( builder.build().toString());
                Log.i("URL",url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                assert url != null;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                responseString = readStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            responseString =  responseString.substring(responseString.indexOf("{"),responseString.lastIndexOf("}")+1);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.optJSONArray("routes");
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("geometry");
                    JSONArray jsonArray1 = jsonObject2.getJSONArray("coordinates");
                    JSONArray jsonArray2 = jsonObject1.getJSONArray("steps");
                    for(int j =0;j<jsonArray1.length();j++) {
                        JSONArray subJsonArray = jsonArray1.optJSONArray(j);
                        latLngs.add(new com.mapbox.mapboxsdk.geometry.LatLng(subJsonArray.getDouble(1),subJsonArray.getDouble(0)));
                    }
                    for(int k=0;k<jsonArray2.length();k++){
                        JSONObject jsonObject3 = jsonArray2.optJSONObject(k);
                        JSONObject jsonObject4 = jsonObject3.getJSONObject("maneuver");
                        JSONObject jsonObject5 =jsonObject4.getJSONObject("location");
                        JSONArray jsonArray3 = jsonObject5.getJSONArray("coordinates");
                        instructions.put(new com.mapbox.mapboxsdk.geometry.LatLng(jsonArray3.getDouble(1),jsonArray3.getDouble(0)),jsonObject4.getString("instruction"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(Map.Entry<String,LatLng> entry : MainActivity.fenceList.entrySet()){
                Location sLocation  = new Location("A");
                sLocation.setLatitude(entry.getValue().latitude);
                sLocation.setLongitude(entry.getValue().longitude);
                for(com.mapbox.mapboxsdk.geometry.LatLng lng:latLngs){
                    Location dLocation = new Location("B");
                    dLocation.setLatitude(lng.getLatitude());
                    dLocation.setLongitude(lng.getLongitude());
                    if(sLocation.distanceTo(dLocation)<= perRange) {
                        locations.put(entry.getKey(), new com.mapbox.mapboxsdk.geometry.LatLng(sLocation.getLatitude(),sLocation.getLongitude()));
                    }
                }
            }
            return latLngs;
        }

        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute( List<com.mapbox.mapboxsdk.geometry.LatLng> latLngs) {
            super.onPostExecute(latLngs);
            com.mapbox.mapboxsdk.geometry.LatLng latlngsadrs = latLngs.get(0);
            com.mapbox.mapboxsdk.geometry.LatLng latlngdadrs = latLngs.get(1);
            sourceMarker =mapView.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latlngsadrs).title("Start Position"));
            destinationMarker =mapView.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latlngdadrs).title("End Position"));
            CoordinateBounds boundingBox = new CoordinateBounds(sourceMarker.getPosition(),destinationMarker.getPosition());
            mapView.setVisibleCoordinateBounds(boundingBox);
            mapView.addPolyline(lineOptions.add(latLngs.subList(2,latLngs.size()-1).toArray(new com.mapbox.mapboxsdk.geometry.LatLng[latLngs.size()-2])));
            progressDialog.dismiss();
        }

        // Street address is converted to Lat Lng position.
        private LatLng geocoding(String place) throws IOException {
            LatLng newList = new LatLng(0,0);
            AndroidGeocoder geocoder = new AndroidGeocoder(Available_Routes.this, Locale.getDefault());
            geocoder.setAccessToken("pk.eyJ1Ijoic3JpcmFtcmFtaW5lbmkiLCJhIjoiY2lqYWE4amE0MDA1M3VpbHgxOTd0bDEyMCJ9.OjpBRJ8laYMsqaM4el6BHQ");
            geocoder.getFromLocationName(place,1);
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(place, 1);
                Log.i("Address",addresses.toString());
                if(addresses.size() > 0) {
                    Log.i("lat lngs",String.valueOf(addresses.get(0).getLatitude())+" "+String.valueOf(addresses.get(0).getLongitude()));
                    newList= new LatLng( addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return newList;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_available__routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        mDrawerToggle.syncState();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
            return true;
        }
        else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position,true);
            if(position ==0){
                Intent intent = new Intent(Available_Routes.this,MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==1){
                Intent intent = new Intent(Available_Routes.this,Main_Helper.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==2){
                Intent intent = new Intent(Available_Routes.this,Available_Routes.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 3){
                Intent intent = new Intent(Available_Routes.this,Settings.class);
                startActivity(intent);
            }
        }
    }

}
