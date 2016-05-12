package com.example.sriramramineni.routing_sample;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by sriram on 2/17/2016.
 */
public class myQueueAdapter extends ArrayAdapter {
    private List<String> objects;
    private Context context;

    public myQueueAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context= context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= super.getView(position, convertView, parent);
        String filename = objects.get(position);
        if(Constants.placeTags.get(filename).equals("Historic")){
           view.setBackgroundColor(context.getResources().getColor(R.color.historic));
        }
        else if(Constants.placeTags.get(filename).equals("Cultural")){
            view.setBackgroundColor(context.getResources().getColor(R.color.cultural));
        }
        else {
            view.setBackgroundColor(context.getResources().getColor(R.color.scenic));
        }
        return view;
    }
}
