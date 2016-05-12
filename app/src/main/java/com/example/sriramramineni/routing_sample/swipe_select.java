package com.example.sriramramineni.routing_sample;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.Bind;


public class swipe_select extends FragmentActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    LinearLayout linearLayout;
    String[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_select);
        Constants.intermediate.clear();
        assert (getActionBar()!=null);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Landscapes");
        moveDrawerToTop();
        initActionBar() ;
        initDrawer();
        linearLayout = (LinearLayout) findViewById(R.id.toolbar);
        Button restart = (Button) findViewById(R.id.restart);
        Button next = (Button) findViewById(R.id.next);
        final Button button = (Button) findViewById(R.id.route);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessageroute(v);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessagemap(v);
            }
        });
        Adapter adapter = new Adapter(getSupportFragmentManager() );
        ViewPager pager= (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> values =sharedPreferences.getStringSet("Category", null);
        array = new String[0];
        if (values != null) {
            array = values.toArray(new String[values.size()]);
        }
        for (String anArray : array) {
            Log.i("array", anArray);
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
        List<String> swList = new ArrayList<>();
        swList.add("Main Menu");
        swList.add("Select Another Route");
        swList.add("Restart Mode");
        swList.add("Refresh Activity");
        swList.add("Settings Activity");
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        ListAdapter adapter = (new MyCustomAdapter(this, android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.planets_array))));
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


    private void sendmessageroute(View v) {
        Intent intent = new Intent(this,Main_Helper.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void sendmessagemap(View v) {
        Intent intent = new Intent(this,Map_Activity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerToggle.syncState();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if(id == R.id.action_settings){
            startActivity(new Intent(this,Settings.class));
            return true;
        }
       else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if (id ==R.id.action_refresh){
            startActivity(new Intent(this,swipe_select.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position,true);
            if(position ==0){
                Intent intent = new Intent(swipe_select.this,MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==1){
                Intent intent = new Intent(swipe_select.this,Available_Routes.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if (position==2){
                Intent intent  = new Intent(swipe_select.this, Main_Helper.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position==3){
                Intent intent  = new Intent(swipe_select.this, swipe_select.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(position == 4){
                Intent intent = new Intent(swipe_select.this,Settings.class);
                startActivity(intent);
            }
        }
    }


    public class Adapter extends FragmentPagerAdapter{

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment select = new Select();
            Bundle args = new Bundle();
            if(position==0) {
                if(Arrays.asList(array).contains("1")) {
                    args.putString("Name", "HISTORIC");
                }
                else args.putString("Name","");
            }
            else if(position==1) {
                if(Arrays.asList(array).contains("3")) {
                    args.putString("Name", "SCENIC");
                }
                else
                    args.putString("Name","");
            }
            else {
                if(Arrays.asList(array).contains("2")) {
                    args.putString("Name", "CULTURAL");
                }
                else args.putString("Name","");
            }

            select.setArguments(args);
            return select;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return "HISTORIC";
            else if(position==1)
                return "SCENIC";
            else
                return "CULTURAL";
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
