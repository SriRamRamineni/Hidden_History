package com.example.sriramramineni.routing_sample;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageviewFragment extends Fragment {

    public ImageviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_image_fragment, container, false);
        Bundle args = getArguments();
        if(Constants.placeTags.get(args.getString("key")).equals("Historic"))
            rootView.findViewById(R.id.old).setBackgroundColor(getResources().getColor(R.color.historic));
        else if(Constants.placeTags.get(args.getString("key")).equals("Cultural"))
            rootView.findViewById(R.id.old).setBackgroundColor(getResources().getColor(R.color.cultural));
        else
            rootView.findViewById(R.id.old).setBackgroundColor(getResources().getColor(R.color.scenic));
        try
        {
            // get input stream
            InputStream ims = getActivity().getAssets().open(new StringBuilder().append("Images/").append(args.getString("Image")).append(".PNG").toString());
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            ((ImageView) rootView.findViewById(R.id.old)).setImageDrawable(d);
            //ims.close();
        }
        catch(IOException ex)
        {
           ex.printStackTrace();
            Toast.makeText(getActivity(), "Images are not available \n" +
                    " Please try again", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }
}
