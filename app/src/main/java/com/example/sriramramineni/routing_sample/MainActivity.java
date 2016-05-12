package com.example.sriramramineni.routing_sample;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    public static HashMap<String,LatLng> fenceList = new HashMap<String,LatLng>();
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private List<String> navList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assert (getActionBar() != null);
        moveDrawerToTop();
        initActionBar();
        initDrawer();
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Hidden History");
        final ConnectivityManager manager = (ConnectivityManager)
                getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        Boolean is3g = manager.getNetworkInfo(
        ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        Boolean isWifi = manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        if (isWifi||is3g) {

            new MyServerAccess().execute();
            CardView cardView = (CardView) findViewById(R.id.cv);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(v);
                }
            });
            CardView cardView1 = (CardView) findViewById(R.id.cv2);
            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage2(v);
                }
            });
            CardView cardView2 = (CardView) findViewById(R.id.cv3);
            cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage3(v);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("WIFI not enabled!")
                    .setMessage("Please enable your wifi or data and refresh activity to use the application")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        }
                    }).show();

        }
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
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        navList.add("Route Mode");
        navList.add("Explore Mode");
        navList.add("Tour Mode");
        navList.add("Refresh Activity");
        navList.add("Settings");
        ListAdapter adapter = (new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, navList));
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

    private void sendMessage(View v) {
        Intent intent = new Intent(this,Main_Helper.class);
        startActivity(intent);
    }

    private void sendMessage2(View v) {
        Intent intent = new Intent(this,Map_Activity2.class);
      // intent.putExtra(EXTRA_MESSAGE,message2 );
        startActivity(intent);
    }

    private void sendMessage3(View v) {
        Intent intent = new Intent(this,Tour_mode.class);
        // intent.putExtra(EXTRA_MESSAGE,message2 );
        startActivity(intent);
    }

    private class MyServerAccess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =  ProgressDialog.show(MainActivity.this,"","Loading Places Data...",true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            fenceList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri.Builder builder = new Uri.Builder();
            String responseString = null;
            builder.scheme("Http").authority("www.techfollowers.com").appendPath("sriram").appendPath("LocationParse.php").build();
            URL url = null;
            try {
                url = new URL(builder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainActivity.this, "Data Base Connection Failed \n" +
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
                                      Toast.makeText(MainActivity.this, "Data Base Connection Failed \n" +
                                              " Please try again", Toast.LENGTH_SHORT).show();
                                  }
                              }
                );
                return null;
            }
            try {
                String responseStringFinal = responseString.substring(responseString.indexOf("{"),responseString.lastIndexOf("}")+1);
                JSONObject jsonObject = new JSONObject(responseStringFinal);
                JSONArray jsonArray = jsonObject.optJSONArray("posts");
                Constants.placeTags.clear();
                fenceList.clear();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject subJsonObject = jsonArray.getJSONObject(i);
                    String name = subJsonObject.optString("Name").toString();
                    Double latitude = subJsonObject.optDouble("Latitude");
                    Double longitude = subJsonObject.optDouble("Longitude");
                    Constants.placeTags.put(name,subJsonObject.getString("Tag"));
                    fenceList.put(name, new LatLng(latitude, longitude));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainActivity.this, "Data Base Connection Failed \n Please try again", Toast.LENGTH_SHORT).show();
                                  }
                              }
                );
                return null;
            }
            return null;
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        else if(id==R.id.action_refresh){
            new MyServerAccess().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position ==0){
                mDrawerList.setItemChecked(position,true);
                Intent intent = new Intent(MainActivity.this,Main_Helper.class);
                startActivity(intent);
            }
            else if(position==1){
                mDrawerList.setItemChecked(position,true);
                Intent intent = new Intent(MainActivity.this,Map_Activity2.class);
                startActivity(intent);
            }
            else if(position==2){
                mDrawerList.setItemChecked(position,true);
                Intent intent = new Intent(MainActivity.this,Main_Helper.class);
                startActivity(intent);
            }
            else if(position==3){
                mDrawerList.setItemChecked(position,true);
               Intent intent = new Intent(MainActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                mDrawerList.setItemChecked(position,true);
                Intent intent = new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
            }
        }
    }

}
