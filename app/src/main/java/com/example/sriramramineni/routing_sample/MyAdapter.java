package com.example.sriramramineni.routing_sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sriram on 1/13/2016.
 */

class name {
    String place;
    boolean selected = false;

    public name(String place) {
        super();
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    void setPlace(String place){
        this.place = place;
    }
    boolean isSelected(){
        return selected;
    }

    void setSelected(boolean selected){
        this.selected = selected;
    }
}
public class MyAdapter extends ArrayAdapter<name>{

    List<name> nameList;
    Context context;

    public MyAdapter(List<name> nameList,Context context) {
        super(context, R.layout.listviewcreater,nameList);
        this.nameList = nameList;
        this.context = context;
    }

    private static class nameHolder{
        public TextView placeName;
        public CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        nameHolder nameholder = new nameHolder();
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.listviewcreater,null);
            nameholder.placeName = (TextView) v.findViewById(R.id.textofplace);
            nameholder.checkBox = (CheckBox)v.findViewById(R.id.checkbox);
            v.setTag(nameholder);
            nameholder.checkBox.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    CheckBox cb = (CheckBox) view;
                    name nameo = (name)cb.getTag();
                    nameo.setSelected(cb.isChecked());
                }
            });
        }
        else {
            nameholder = (nameHolder) v.getTag();
        }
        name place = nameList.get(position);
        nameholder.placeName.setText(place.getPlace());
        nameholder.checkBox.setChecked(place.isSelected());
        nameholder.checkBox.setTag(place );
        return v;
    }
}
