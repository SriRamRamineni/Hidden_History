package com.example.sriramramineni.routing_sample;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sriram on 2/12/2016.
 */
public class MyCustomAdapter extends ArrayAdapter<String> {


    public MyCustomAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if(position==1)
            view.setBackgroundColor(Color.argb(255,121,85,72));
        else if(position==2)
            view.setBackgroundColor(Color.argb(255,96,125,139));
        else if(position==3)
            view.setBackgroundColor(Color.argb(255,205,220,57));
        else
            view.setBackgroundColor(Color.blue(5));
        return view;
    }
}
