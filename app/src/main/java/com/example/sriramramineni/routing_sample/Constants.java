package com.example.sriramramineni.routing_sample;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by Sri Ram Ramineni on 9/1/2015.
*/
public class Constants {
    static HashMap<String,String> placeTags = new HashMap<>();
    static ArrayList<String> myQueue = new ArrayList<>();
    static ArrayList<Integer> tempUsage= new ArrayList<Integer>();
    static HashMap<String, int[]> numbers = new HashMap<>();
    static int Choice = 0;
    static HashMap<String,LatLng> intermediate = new HashMap<>();
}
