package com.example.sriramramineni.routing_sample;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class Main_Helper extends Activity implements LocationListener {

    private static final String EXTRA_MESSAGE = "com.example.sriramramineni.routing_sample";
    String message;
    private LocationManager mLocationManager;
    InstantAutoComplete editText1;
    InstantAutoComplete editText2;
    boolean flag = false;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    static int range = 0;
    static boolean isCustom = false;
    com.pavelsikun.seekbarpreference.MaterialSeekBarView material;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    ProgressDialog progressDialog;
    private int MY_PERMISSION_CODE;
    RadioButton radioButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__helper);
        assert (getActionBar() != null);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("ROUTE");
        moveDrawerToTop();
        initActionBar();
        initDrawer();
        String[] array = {"YOUR GPS LOCATION"};
        editText1 = (InstantAutoComplete) findViewById(R.id.nameoffromplace);
        editText2 = (InstantAutoComplete) findViewById(R.id.nameoftoplace);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main_Helper.this,android.R.layout.simple_list_item_1,array);
        editText1.setAdapter(arrayAdapter);
        editText2.setAdapter(arrayAdapter);
        editText1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog=ProgressDialog.show(Main_Helper.this,"","Getting Your Location...",true);
                locationupdater(view);
            }
        });
        editText2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog=ProgressDialog.show(Main_Helper.this,"","Getting Your Location...",true);
                flag = true;
                locationupdater(view);
            }
        });

        ImageButton button = (ImageButton) findViewById(R.id.add_button2);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText1.getText().toString().trim().length() == 0 && editText2.getText().toString().trim().length() == 0) {
                            Context context = getApplicationContext();
                            CharSequence text = "Enter From Place and To Place";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else if (editText2.getText().toString().trim().length() == 0) {
                            Context context = getApplicationContext();
                            CharSequence text = "Enter To Place";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else if (editText1.getText().toString().trim().length() == 0) {
                            Context context = getApplicationContext();
                            CharSequence text = "Enter From Place";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            message = editText1.getText().toString() + "," + editText2.getText().toString();
                            Log.i("message", message);
                            sendMessage(v);
                        }
                    }
                }
        );
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiog);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                material = (com.pavelsikun.seekbarpreference.MaterialSeekBarView) findViewById(R.id.optional);
                if (checkedId == R.id.radio2) {
                    isCustom = true;
                    material.setVisibility(View.VISIBLE);
                    range = material.getCurrentValue();
                } else if (checkedId == R.id.radio1) {
                    isCustom = false;
                    range = 0;
                    material.setVisibility(View.GONE);
                }
            }
        });
        RadioButton radioButton = (RadioButton) findViewById(R.id.radio1);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustom = false;
                range = 0;
                material.setVisibility(View.GONE);
            }
        });
         radioButton1 = (RadioButton) findViewById(R.id.radio2);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustom = true;
                material.setVisibility(View.VISIBLE);
                range = material.getCurrentValue();
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

    private void sendMessage(View v) {
        Intent intent = new Intent(this, Available_Routes.class);
        if (isCustom) {
            range = material.getCurrentValue();
        }
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main__helper, menu);
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

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if (id == R.id.action_refresh){
            Intent intent = new Intent(Main_Helper.this,Main_Helper.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        actionBar = getActionBar();
        assert (actionBar != null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        ArrayList<String> halperlist = new ArrayList<>();
        halperlist.add("Main Menu");
        halperlist.add("Customize Settings");
        halperlist.add("Refresh Activity");
        halperlist.add("Settings");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        ListAdapter adapter = (new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,halperlist));
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


    private class reverseGeocoder extends AsyncTask<LatLng, Void, String> {

        List<Address> Addresses;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(LatLng... params) {
            LatLng latLng = params[0];
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                Addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder str = new StringBuilder();
            Address returnAdress = Addresses.get(0);
            str.append(returnAdress.getLocality());
            str.append(" ");
            str.append(returnAdress.getAddressLine(0));
            return str.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!flag) {
                Log.i("edit-1","usage");
                editText1.setText(s);
            } else {
                editText2.setText(s);
                flag = false;
            }
            progressDialog.dismiss();
        }
    }


    public void locationupdater(View v) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
                return;
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
                return;
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            new reverseGeocoder().execute(myLocation);
        } else {
            Log.i("I am here", "ram");
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.i("using", "NetworkProvider");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i("using", "GpsProvider");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                Toast.makeText(getApplicationContext(), "Location is Unavailable Now!", Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location Changed", location.getLatitude() + " and " + location.getLongitude());
        new reverseGeocoder().execute(new LatLng(location.getLatitude(), location.getLongitude()));
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
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Provider","Status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Provider","Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Provider","Disabled");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position,true);
            if(position ==0){
                Intent intent = new Intent(Main_Helper.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==1){
                radioButton1.setChecked(true);
                radioButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isCustom = true;
                        material.setVisibility(View.VISIBLE);
                        range = material.getCurrentValue();
                    }
                });
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
            else if(position==2){
                Intent intent = new Intent(Main_Helper.this, Main_Helper.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 3){
                mDrawerList.setItemChecked(position,true);
                Intent intent = new Intent(Main_Helper.this,Settings.class);
                startActivity(intent);
            }
        }
    }
}
