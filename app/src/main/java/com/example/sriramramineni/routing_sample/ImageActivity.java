package com.example.sriramramineni.routing_sample;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

public class ImageActivity extends Fragment{

    ViewPager viewPager;
    MyAdapter myAdapter;
    int count = Constants.numbers.get(Constants.myQueue.get(0))[0];

    String s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image, container, false);
        Log.i("Image","Active");
        Log.i(Constants.myQueue.get(0),String.valueOf(count));
        myAdapter = new MyAdapter(getChildFragmentManager() );
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(myAdapter);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment  = new ImageviewFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            if(Constants.numbers.get(Constants.myQueue.get(0))[0]>1){
                s = new StringBuilder().append(Constants.myQueue.get(0)).append(String.valueOf(position + 1)).toString();
            }
            else
                s = Constants.myQueue.get(0);
            args.putString("Image", s);
            args.putString("key",s);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return count;
        }

    }
}
