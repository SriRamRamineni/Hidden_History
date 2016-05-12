package com.example.sriramramineni.routing_sample;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bowyer.app.fabtransitionlayout.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class Map_Activity2 extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener ,ResultCallback<Status> {
    boolean mapReady = false;
    GoogleMap rMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LatLng myLocation  = new LatLng(41.7409,111.8141);
    Polyline line;
    List<LatLng> routePoints  = new ArrayList<LatLng>();
    PolylineOptions lineOptions = new PolylineOptions().width(25).color(Color.RED);
    long count = 0 ;
    protected  ArrayList<Geofence> geofenceArrayList;
    private PendingIntent mGeofencePendingIntent;
    IntentFilter intentFilter;
    FrameLayout frameLayout ;
    private int perRange;
    @Bind(R.id.list_menu)
    ListView listView ;
    @Bind(R.id.bottom_sheet)
    BottomSheetLayout bottomSheetLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.maxHeight)
    LinearLayout linearLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    List<String> NavList = new ArrayList<>();
    boolean isthereGeofence = false;
    List<String> newarray = new ArrayList<String>();
    private TextView textView1;
    private LatLng markerlocation;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.fourthTheme);
        setContentView(R.layout.activity_map_2);
        Constants.myQueue.clear();
        moveDrawerToTop();
        initDrawer();
       // getBroadcastReciever();
        ButterKnife.bind(this);
        ImageButton imageButton = (ImageButton) findViewById(R.id.drawerbutton);
        button = (Button) findViewById(R.id.recenter);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
         //set floating button to FabToolbar
        bottomSheetLayout.setFab(mFab);
        Constants.myQueue.clear();
        geofenceArrayList = new ArrayList<Geofence>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        perRange = sharedPreferences.getInt("distance_key",500);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        // assigning map fragment to a variable.
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Item = Constants.myQueue.get(position);
                Constants.myQueue.remove(position);
                Constants.myQueue.add(0, Item);
                CongruentSlider congruentSlider = new CongruentSlider();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.imageHolder, congruentSlider);
                ft.addToBackStack(null);
                ft.commit();
                bottomSheetLayout.contractFab();
            }
        });
        intentFilter = new IntentFilter("BroadCast");
       // frameLayout = (FrameLayout) findViewById(R.id.imageHolder);
       // LocalBroadcastManager.getInstance(this).registerReceiver(mbroadcastReceiver , intentFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
                rMap.animateCamera(cameraUpdate);
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


    private void initDrawer() {
        NavList.add("ALL");
        NavList.add("HISTORIC");
        NavList.add("CULTURAL");
        NavList.add("SCENIC");
        NavList.add("Main Menu");
        NavList.add("Refresh Activity");
        NavList.add("Settings");
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        ListAdapter adapter = (new MyCustomAdapter(this, android.R.layout.simple_list_item_1, NavList));
        mDrawerList.setAdapter(adapter);
        mDrawerList.setItemChecked(Constants.Choice,true);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position,true);
            if(position ==0){
                if(Constants.Choice!=0) {
                    Constants.Choice = 0;
                    Intent intent = new Intent(Map_Activity2.this,Map_Activity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            else if (position==1){
                if(Constants.Choice!=1) {
                    Constants.Choice = 1;
                    Intent intent = new Intent(Map_Activity2.this,Map_Activity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            else if (position==2){
                if(Constants.Choice!=2) {
                    Constants.Choice = 2;
                    Intent intent = new Intent(Map_Activity2.this,Map_Activity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            else if (position==3){
                if(Constants.Choice!=3) {
                    Constants.Choice = 3;
                    Intent intent = new Intent(Map_Activity2.this,Map_Activity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            else if(position==4){
                Intent intent = new Intent(Map_Activity2.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 5){
                Intent intent = new Intent(Map_Activity2.this,Map_Activity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 6){
                Intent intent = new Intent(Map_Activity2.this,Settings.class);
                startActivity(intent);
            }
        }
    }


    private  BroadcastReceiver mbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getExtras().getString("Current GeoFence");
            String[] arrays = new String[0];
            if (s != null) {
                arrays = s.split(":");
            }
            Log.i(String.valueOf(arrays.length), s);
            if (Constants.myQueue.size() <= 10) {
                newarray.clear();
                for (int i = 0; i < arrays.length; i++) {
                    if (!Constants.myQueue.contains(arrays[i])) {
                        Constants.myQueue.add(arrays[i]);
                        newarray.add(arrays[i]);
                    }
                }
            } else {
                newarray.clear();
                for(int i =0;i<arrays.length;i++){
                    if(!Constants.myQueue.contains(arrays[i])){
                        newarray.add(arrays[i]);
                    }
                }
                if(newarray.size()>=1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Map_Activity2.this);
                    builder.setMessage("Queue is full would you like to Remove Previous places ?")
                            .setItems(newarray.toArray(new String[newarray.size()]),null)
                            .setTitle("Queue OverLoad")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Constants.tempUsage.clear();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Map_Activity2.this);
                                    builder1.setTitle("Queue")
                                            .setMultiChoiceItems(Constants.myQueue.toArray(new String[Constants.myQueue.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                    if (isChecked) {
                                                        Constants.tempUsage.add(which);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    for (int i : Constants.tempUsage)
                                                        Constants.myQueue.remove(i);
                                                    Log.i(String.valueOf(Constants.myQueue.size()), Constants.myQueue.toString());
                                                }
                                            }).show();
                                }
                            })
                            .setNegativeButton("No", null).show();
                    for(int i = 0;i<Constants.tempUsage.size();i++)
                        Constants.myQueue.add(arrays[i]);
                }
            }
            try {
                new DatabaseAccess().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Map_Activity2.this);
            String value = sharedPreferences.getString("Mode_key","1");
            if(value.equals("1")) {
                CongruentSlider congruentSliders = (CongruentSlider) getSupportFragmentManager().findFragmentByTag("TAG");
                if (congruentSliders == null) {
                    CongruentSlider congruentSlider = new CongruentSlider();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.imageHolder, congruentSlider, "TAG");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
            else if(value.equals("2")){
                LayoutInflater inflater = getLayoutInflater();
                View view=inflater.inflate(R.layout.customtitlebar, null);
                AlertDialog.Builder build = new AlertDialog.Builder(Map_Activity2.this);
                build.setCustomTitle(view)
                        .setItems(newarray.toArray(new String[newarray.size()]),null)
                        .setPositiveButton("Visit Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CongruentSlider congruentSliders = (CongruentSlider) getSupportFragmentManager().findFragmentByTag("TAG");
                                if (congruentSliders == null) {
                                    CongruentSlider congruentSlider = new CongruentSlider();
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.add(R.id.imageHolder, congruentSlider, "TAG");
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }
                        })
                        .setNegativeButton("Visit Later",null).show();
            }
        }
    };


    private void setScroll() {
        ArrayAdapter<String> arrayAdapter = new myQueueAdapter(this,android.R.layout.simple_list_item_1,Constants.myQueue);
        listView.setAdapter(arrayAdapter);
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        setScroll();
        bottomSheetLayout.expandFab();
    }


    private class DatabaseAccess extends AsyncTask<Void,Void,Boolean> {
        ProgressDialog progressDialog;
        int[] array = new int[3];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.i("Queue Contents", Constants.myQueue.toString());
            Log.i("Internet Connection","Started");
            progressDialog =  ProgressDialog.show(Map_Activity2.this, "", "Loading Fragments...", true);
        }

        @Override
        protected Boolean doInBackground(Void...params) {
            for(int i =0;i<Constants.myQueue.size();i++) {
                Uri.Builder builder = new Uri.Builder();
                String responseString = null;
                builder.scheme("Http").authority("www.techfollowers.com").appendPath("sriram").appendPath("CountParameter.php").appendQueryParameter("parameter", new StringBuilder().append("\"").append(Constants.myQueue.get(i)).append("\"").toString()).build();
                URL url = null;
                try {
                    url = new URL(builder.toString());
                    Log.i("URL", url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(Map_Activity2.this, "Data Base Connection Failed \n" +
                                                  " Please try again", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                    );
                    return null;
                }
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                    responseString = readStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(Map_Activity2.this, "Data Base Connection Failed \n" +
                                                  " Please try again", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                    );
                    return null;
                }
                try {
                    String responseStringFinal = responseString.substring(responseString.indexOf("{"), responseString.lastIndexOf("}") + 1);
                    JSONObject jsonObject = new JSONObject(responseStringFinal);
                    JSONObject subJsonObject = jsonObject.getJSONObject("posts");
                    //Log.i("json Array",subJsonObject.toString());
                    array[0] = subJsonObject.getInt("ImageCount");
                    array[1] = subJsonObject.getInt("AudioCount");
                    array[2] = subJsonObject.getInt("TextCount");
                    Constants.numbers.put(Constants.myQueue.get(i), array);
                    Log.i(Constants.myQueue.get(i), String.valueOf(array[0]) + String.valueOf(array[1]) + String.valueOf(array[2]));
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(Map_Activity2.this, "Data Base Connection Failed \n Please try again", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                    );
                    return null;
                }
            }
            return Boolean.TRUE;
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
        protected void onPostExecute(Boolean aVoid) {
            progressDialog.dismiss();
            Log.i("Internet Completed","Ended");
            super.onPostExecute(aVoid);
        }
    }


    //  Populating the GeofencingList with LatLng elements from Query list.
    private void populateGeoFenceList() {
        if(MainActivity.fenceList.size()!=0){
            for(Map.Entry<String,LatLng> entry :  MainActivity.fenceList.entrySet()) {
                if(Constants.Choice==2||Constants.Choice==3||Constants.Choice==1){
                    Log.i("choice",String.valueOf(Constants.placeTags.get(entry.getKey()).equals("Historic")));
                    Log.i("Choice",String.valueOf(Constants.Choice==1));
                    if(Constants.Choice==1&&Constants.placeTags.get(entry.getKey()).equals("Historic")){
                        isthereGeofence  = true;
                        // marker for every geofence population.
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.brownmarker)).title(entry.getKey()));
                        // Creating the Geofences with the LatLng elements from the constant class and adding them to GeofenceList
                        geofenceArrayList.add(
                                new Geofence.Builder().setRequestId(entry.getKey())
                                        .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, perRange)
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                    }
                    else if(Constants.Choice==2&&Constants.placeTags.get(entry.getKey()).equals("Cultural")){
                        isthereGeofence = true;
                        // marker for every geofence population.
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker)).title(entry.getKey()));
                        // Creating the Geofences with the LatLng elements from the constant class and adding them to GeofenceList
                        geofenceArrayList.add(
                                new Geofence.Builder().setRequestId(entry.getKey())
                                        .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, perRange)
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                    }
                    else if(Constants.Choice ==3 && Constants.placeTags.get(entry.getKey()).equals("Scenic")){
                        isthereGeofence = true;
                        // marker for every geofence population.
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenmarker)).title(entry.getKey()));
                        // Creating the Geofences with the LatLng elements from the constant class and adding them to GeofenceList
                        geofenceArrayList.add(
                                new Geofence.Builder().setRequestId(entry.getKey())
                                        .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, perRange)
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                    }
                }
                else {
                    isthereGeofence = true;
                    Log.i("Choice","out");
                    // marker for every geofence population.
                    if(Constants.placeTags.get(entry.getKey()).equals("Historic"))
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.brownmarker)).title(entry.getKey()));
                    else if(Constants.placeTags.get(entry.getKey()).equals("Scenic") )
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenmarker)).title(entry.getKey()));
                    else
                        rMap.addMarker(new MarkerOptions().position(new LatLng(entry.getValue().latitude, entry.getValue().longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker)).title(entry.getKey()));
                    // Creating the Geofences with the LatLng elements from the constant class and adding them to GeofenceList
                    geofenceArrayList.add(
                            new Geofence.Builder().setRequestId(entry.getKey())
                                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, perRange)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                            Geofence.GEOFENCE_TRANSITION_EXIT)
                                    .build());
                }
            }
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        try {
            LocalBroadcastManager.getInstance(Map_Activity2.this).unregisterReceiver(mbroadcastReceiver);
        }
        catch (IllegalArgumentException ex){
            Log.i("Receiver","Not Registered");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
       // LocalBroadcastManager.getInstance(Map_Activity2.this).registerReceiver(mbroadcastReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map__activity2, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mapReady)
            populateGeoFenceList();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("Connected", "ram");
        try {
            if(MainActivity.fenceList.size()!=0&&isthereGeofence) {
                mGeofencePendingIntent = getGeofencePendingIntent();
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), mGeofencePendingIntent).setResultCallback(this);
            }
        }catch (SecurityException securityException){
            Log.e("Security Exception","Geofences creation failed");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection Suspended", "ram");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("location changed", "yes");
        myLocation = new LatLng(location.getLatitude(),location.getLongitude());
        if(count != 0 ) {
            Log.i("I am here ","Ram");
            routePoints = line.getPoints();
            routePoints.add(myLocation);
            line.setPoints(routePoints);
        }
        else {
            routePoints.add(myLocation);
            rMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_historyy)).title("Start Position"));
            line.setPoints(routePoints);
        }
        count++;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("failed", "connection");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        rMap = googleMap;
        rMap.setInfoWindowAdapter(new CustomWindow());
        rMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (Constants.myQueue.contains(marker.getTitle())) {
                    int foo = Constants.myQueue.indexOf(marker.getTitle());
                    String item = Constants.myQueue.get(foo);
                    Constants.myQueue.remove(foo);
                    Constants.myQueue.add(0,item);
                }
                else {
                    Constants.myQueue.add(0, marker.getTitle());
                    Constants.numbers.put(marker.getTitle(),new int[]{1,1,1});
                }
                CongruentSlider congruentSlider = new CongruentSlider();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.imageHolder, congruentSlider);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        rMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        rMap.setBuildingsEnabled(true);
        rMap.getUiSettings().setZoomGesturesEnabled(true);
        rMap.getUiSettings().setCompassEnabled(true);
        rMap.getUiSettings().setZoomControlsEnabled(true);
        rMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        line = rMap.addPolyline(lineOptions);

    }
    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(geofenceArrayList);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent("com.example.sriramramineni.routing_sample.ACTION_GEOFENCE_RECEIVE");
        //Intent intent = new Intent(this, GeoFenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()

        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


    @Override
    public void onResult(Status status) {

        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());

            Log.e("Error",errorMessage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(Map_Activity2.this).registerReceiver(mbroadcastReceiver, intentFilter);

    }

    @Override
    public void onBackPressed() {
        if(bottomSheetLayout.isFabExpanded()){
            bottomSheetLayout.contractFab();
        }
        else
        super.onBackPressed();
    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(Map_Activity2.this).unregisterReceiver(mbroadcastReceiver);
        super.onPause();
    }

    private class CustomWindow implements GoogleMap.InfoWindowAdapter {
        private View view;

        CustomWindow(){
            view = getLayoutInflater().inflate(R.layout.customwindow,null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView textView = (TextView) view.findViewById(R.id.textview);
            textView.setText(marker.getTitle());
            textView1 = (TextView)view.findViewById(R.id.dist);
            markerlocation = marker.getPosition();
            if(myLocation!=null && markerlocation!=null){
                Location location1  = new Location("");
                location1.setLatitude(myLocation.latitude);
                location1.setLongitude(myLocation.longitude);
                Location location2 = new Location("");
                location2.setLatitude(markerlocation.latitude);
                location2.setLongitude(markerlocation.longitude);
                float distance = location1.distanceTo(location2);
                textView1.setText(String.valueOf(distance)+" "+"Meters");
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.container);
            InputStream ims = null;
            try {
                ims = getApplicationContext().getAssets().open(new StringBuilder().append("Images/").append(marker.getTitle()).append(".PNG").toString());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Image Not Available!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Drawable d = Drawable.createFromStream(ims, null);
            imageView.setImageDrawable(d);
            return view;
        }
    }

}


