package com.example.sriramramineni.routing_sample;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.views.MapView;

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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Map_Activity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener ,ResultCallback<Status> {


    com.mapbox.mapboxsdk.annotations.PolylineOptions lineOptions = new com.mapbox.mapboxsdk.annotations.PolylineOptions().width(15).color(Color.argb(100, 50, 0, 250));
    com.mapbox.mapboxsdk.annotations.Polyline line;
    private ArrayList<Geofence> geofenceArrayList;
    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private com.mapbox.mapboxsdk.geometry.LatLng myLocation;
//    BroadcastReceiver mbroadcastReceiver;
    MapView mapView;
    boolean mapready= false;
    private IntentFilter intentFilter;
    private PendingIntent mGeofencePendingIntent;
    int perRange=0;
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
    List<String> newarray = new ArrayList<String>();
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.fourthTheme);
        setContentView(R.layout.activity_map_);
        ButterKnife.bind(this);
        //set floating button to FabToolbar
        moveDrawerToTop();
        initDrawer();
        ImageButton imageButton = (ImageButton) findViewById(R.id.drawerbutton);
         button = (Button) findViewById(R.id.recenter);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        bottomSheetLayout.setFab(mFab);
        Constants.myQueue.clear();
        geofenceArrayList = new ArrayList<Geofence>();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mapView = (MapView) findViewById(R.id.map);
        mapView.setAccessToken(getString(R.string.accessToken));
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.setZoomLevel(11);
        mapView.onCreate(savedInstanceState);
        mapView.setInfoWindowAdapter(new CustomAdapter());
        mapView.setOnInfoWindowClickListener(new MapView.OnInfoWindowClickListener() {
            @Override
            public boolean onMarkerClick(com.mapbox.mapboxsdk.annotations.Marker marker) {
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
                ft.add(R.id.imageHolder1, congruentSlider);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Item = Constants.myQueue.get(position);
                Constants.myQueue.remove(position);
                Constants.myQueue.add(0,Item);
                CongruentSlider congruentSlider = new CongruentSlider();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.imageHolder1, congruentSlider);
                ft.addToBackStack(null);
                ft.commit();
                bottomSheetLayout.contractFab();
            }
        });
        if(Main_Helper.isCustom)
            perRange = Main_Helper.range;
        else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            perRange = sharedPreferences.getInt("distance_key",500);
        }
        intentFilter = new IntentFilter("BroadCast");;
        //LocalBroadcastManager.getInstance(this).registerReceiver( mbroadcastReceiver , intentFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition(myLocation,15,0,0);
                com.mapbox.mapboxsdk.camera.CameraUpdate cameraUpdate = com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newCameraPosition(cameraPosition);
                mapView.animateCamera(cameraUpdate);
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
        List<String> dlist = new ArrayList<>();
        dlist.add("Main Menu");
        dlist.add("Route Mode Menu");
        dlist.add("Landscapes");
        dlist.add("Refresh Activity");
        dlist.add("Settings");
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,dlist);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position,true);
            if(position ==0){
                Intent intent = new Intent(Map_Activity.this,MainActivity.class);
                startActivity(intent);
            }
            else if(position ==1){
                Intent intent = new Intent(Map_Activity.this,Main_Helper.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==2){
                onBackPressed();
            }
            else if(position==3){
                Intent intent = new Intent(Map_Activity.this,Map_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 4){
                Intent intent = new Intent(Map_Activity.this,Settings.class);
                startActivity(intent);
            }
        }
    }

    private BroadcastReceiver mbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getExtras().getString("Current GeoFence");
            String[] arrays = new String[0];
            if (s != null) {
                arrays = s.split(":");
            }
            if (Constants.myQueue.size() <= 10) {
                newarray.clear();
                for (int i = 0; i < arrays.length; i++)
                    if (!Constants.myQueue.contains(arrays[i])) {
                        Constants.myQueue.add(arrays[i]);
                        newarray.add(arrays[i]);
                    }
            } else {
                 newarray.clear();
                for (int i = 0; i < arrays.length; i++) {
                    if (!Constants.myQueue.contains(arrays[i])) {
                        newarray.add(arrays[i]);
                    }
                }
                if (newarray.size() >= 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Map_Activity.this);
                    builder.setMessage("Queue is full would you like to Remove Previous places ?")
                            .setItems(newarray.toArray(new String[newarray.size()]), null)
                            .setTitle("Queue OverLoad")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Map_Activity.this);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Map_Activity.this);
            String value = sharedPreferences.getString("Mode_key","1");
            if(value.equals("1")) {
                CongruentSlider congruentSliders = (CongruentSlider) getSupportFragmentManager().findFragmentByTag("TAG");
                if (congruentSliders == null) {
                    CongruentSlider congruentSlider = new CongruentSlider();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.imageHolder1, congruentSlider, "TAG");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
            else if(value.equals("2")){
                LayoutInflater inflater = getLayoutInflater();
                View view=inflater.inflate(R.layout.customtitlebar, null);
                AlertDialog.Builder build = new AlertDialog.Builder(Map_Activity.this);
                build.setCustomTitle(view)
                        .setItems(newarray.toArray(new String[newarray.size()]),null)
                        .setPositiveButton("Visit Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CongruentSlider congruentSliders = (CongruentSlider) getSupportFragmentManager().findFragmentByTag("TAG");
                                if (congruentSliders == null) {
                                    CongruentSlider congruentSlider = new CongruentSlider();
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.add(R.id.imageHolder1, congruentSlider, "TAG");
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }
                        })
                        .setNegativeButton("No",null).show();
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
            progressDialog =  ProgressDialog.show(Map_Activity.this, "", "Loading Fragments...", true);
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
                                          Toast.makeText(Map_Activity.this, "Data Base Connection Failed \n" +
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
                                          Toast.makeText(Map_Activity.this, "Data Base Connection Failed \n" +
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(Map_Activity.this, "Data Base Connection Failed \n Please try again", Toast.LENGTH_SHORT).show();
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


    //  Populating the GeofencingList with LatLng elements from Constants list.
    private void populateGeoFenceList() {
        if(Available_Routes.locations.size()!=0) {
            for (Map.Entry<String, com.mapbox.mapboxsdk.geometry.LatLng> entry :Constants.intermediate.entrySet()) {
                // marker for every geofence population.\
//                BitmapDescriptorFactory.fromResource(Map_Activity.this.getResources().getIdentifier(sb.toString().toLowerCase(), "drawable", Map_Activity.this.getPackageName()))
                Drawable mIconDrawable;
                IconFactory mIconFactory = IconFactory.getInstance(Map_Activity.this);
                if(Constants.placeTags.get(entry.getKey()).equals("Historic"))
                    mIconDrawable = ContextCompat.getDrawable(Map_Activity.this, R.drawable.brownmarker);
                else if(Constants.placeTags.get(entry.getKey()).equals("Scenic") )
                    mIconDrawable = ContextCompat.getDrawable(Map_Activity.this, R.drawable.greenmarker);
                else
                    mIconDrawable = ContextCompat.getDrawable(Map_Activity.this, R.drawable.bluemarker);
                Icon icon = mIconFactory.fromDrawable(mIconDrawable);
                // Add the custom icon marker to the map
                mapView.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(new com.mapbox.mapboxsdk.geometry.LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude())).icon(icon).title(entry.getKey()));
                // Creating the Geofences with the LatLng elements from the constant class and adding them to GeofenceList
                Log.i("Range",String.valueOf(perRange));
                geofenceArrayList.add(
                        new Geofence.Builder().setRequestId(entry.getKey())
                                .setCircularRegion(entry.getValue().getLatitude(), entry.getValue().getLongitude(), perRange)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                        Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build());
            }
        }
    }


    // Unregistering Broadcast Receiver when the application is paused.
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(Map_Activity.this).unregisterReceiver(mbroadcastReceiver);
        mapView.onResume();
        super.onPause();
    }
    // Reregistering the Receiver when the application resumes.
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(Map_Activity.this).registerReceiver(mbroadcastReceiver, intentFilter);
    }

    // Geo-fencing request is created  and is returned to the calling function
    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(geofenceArrayList);

        return builder.build();
    }

    // Pending intent is created and a broadcast service is initiated.
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
            String errorMessage = GeofenceErrorMessages.getErrorString(this,status.getStatusCode());
            Log.e("Error",errorMessage);
        }

    }

    @Override
    public void onBackPressed() {
        if(bottomSheetLayout.isFabExpanded()) {
            bottomSheetLayout.contractFab();
        }
        else
        super.onBackPressed();
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

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapready = true;
        mGoogleApiClient.connect();
        mapView.onStart();
        line = mapView.addPolyline(lineOptions.add((Available_Routes.latLngs.subList(2,Available_Routes.latLngs.size()-1)).toArray(new com.mapbox.mapboxsdk.geometry.LatLng[Available_Routes.latLngs.size()-2])));
        CameraPosition cameraPosition = new CameraPosition(Available_Routes.latLngs.get(0),15,0,0);
        mapView.addMarker(new MarkerOptions().position(Available_Routes.latLngs.get(0)).title("my location"));
        mapView.addMarker(new MarkerOptions().position(Available_Routes.latLngs.get(1)).title("Destination"));
        com.mapbox.mapboxsdk.camera.CameraUpdate cameraUpdate = com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newCameraPosition(cameraPosition);
        mapView.animateCamera(cameraUpdate);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_, menu);
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
        if(mapready)
            populateGeoFenceList();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
        Log.i("Connected", "ram");
        try {
            if(Constants.intermediate.size()>0)
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        }catch (SecurityException securityException){
            Log.e("Security Exception","Geofences creation failed");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("location changed", "yes");
        myLocation = new com.mapbox.mapboxsdk.geometry.LatLng(location.getLatitude(),location.getLongitude());
        new Routing().execute();
        mapView.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(myLocation).title("My Position"));
    }

    private class Routing extends AsyncTask<Void,String,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            for(final Map.Entry<com.mapbox.mapboxsdk.geometry.LatLng,String> entry : Available_Routes.instructions.entrySet()){
                Location sLocation  = new Location(entry.getKey().toString());
                Location dLocation = new Location(myLocation.toString());
                if(sLocation.distanceTo(dLocation)< 500) {
                    publishProgress(entry.getValue());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Snackbar.make(findViewById(R.id.snackbar),values[0],Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection","Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Connection","Failed");
    }

    private class CustomAdapter implements MapView.InfoWindowAdapter {
        @Nullable
        @Override
        public View getInfoWindow(com.mapbox.mapboxsdk.annotations.Marker marker) {
            View view = getLayoutInflater().inflate(R.layout.customwindow,null);
            TextView textView = (TextView) view.findViewById(R.id.textview);
            textView.setText(marker.getTitle());
            TextView textView1 = (TextView)view.findViewById(R.id.dist);
            com.mapbox.mapboxsdk.geometry.LatLng markerlocation = marker.getPosition();
            if(myLocation!=null && markerlocation!=null){
                Location location1  = new Location("");
                location1.setLatitude(myLocation.getLatitude());
                location1.setLongitude(myLocation.getLongitude());
                Location location2 = new Location("");
                location2.setLatitude(markerlocation.getLatitude());
                location2.setLongitude(markerlocation.getLongitude());
                float distance = location1.distanceTo(location2);
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMaximumFractionDigits(2);
                numberFormat.setMinimumFractionDigits(2);
                String number = numberFormat.format(distance);
                textView1.setText(number+" "+"Meters");
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
