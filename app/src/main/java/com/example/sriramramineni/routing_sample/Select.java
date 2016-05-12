package com.example.sriramramineni.routing_sample;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.OnItemClick;

public class Select extends ListFragment {

    ListView lv ;
    List<String> nameArrayList = new ArrayList<String>();
    ArrayAdapter myAdapter;
    Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_select, container, false);
        args = getArguments();
        for(Map.Entry<String, com.mapbox.mapboxsdk.geometry.LatLng> entry :  Available_Routes.locations.entrySet()){
            if(Constants.placeTags.get(entry.getKey()).toUpperCase().equals(args.getString("Name"))){
                nameArrayList.add(entry.getKey());
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv = getListView();
        myAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,nameArrayList);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (args.getString("Name").equals("HISTORIC")) {
                    if(Constants.intermediate.containsKey(nameArrayList.get(position))){
                        view.setBackgroundColor(getResources().getColor(R.color.normal));
                        Constants.intermediate.remove(nameArrayList.get(position));
                    }
                    else {
                        view.setBackgroundColor(getResources().getColor(R.color.historic));
                        Constants.intermediate.put(nameArrayList.get(position), Available_Routes.locations.get(nameArrayList.get(position)));
                    }
                }
                else if (args.getString("Name").equals("SCENIC")) {
                    if (Constants.intermediate.containsKey(nameArrayList.get(position))) {
                        view.setBackgroundColor(getResources().getColor(R.color.normal));
                        Constants.intermediate.remove(nameArrayList.get(position));
                    } else {
                        view.setBackgroundColor(getResources().getColor(R.color.scenic));
                        Constants.intermediate.put(nameArrayList.get(position), Available_Routes.locations.get(nameArrayList.get(position)));
                    }
                }
                else {
                    if(Constants.intermediate.containsKey(nameArrayList.get(position))){
                        view.setBackgroundColor(getResources().getColor(R.color.normal));
                        Constants.intermediate.remove(nameArrayList.get(position));
                    }
                    else{
                        view.setBackgroundColor(getResources().getColor(R.color.cultural));
                        Constants.intermediate.put(nameArrayList.get(position),Available_Routes.locations.get(nameArrayList.get(position)));
                    }
                }
                Log.i("Adding", nameArrayList.get(position));
            }
        });
    }

}
