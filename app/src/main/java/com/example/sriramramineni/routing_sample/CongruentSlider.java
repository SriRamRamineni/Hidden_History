package com.example.sriramramineni.routing_sample;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;


public class CongruentSlider extends Fragment  {

    int count ;
    MediaPlayer mediaPlayer = new MediaPlayer();
    double finalTime=0.00,startTime=0.00;
    android.os.Handler myHandler = new android.os.Handler() ;
    SeekBar seekbar;
    TextView tx2;
    TextView tx1;
    Button bp;
    AssetFileDescriptor descriptor = null;
    BufferedReader br;
    StringBuilder sb;
    String filename  ;
    TextView textView;
    String s="";
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = new ContextThemeWrapper(getActivity(),R.style.fourthTheme);
        LayoutInflater localinflator = inflater.cloneInContext(context);
        final View view = localinflator.inflate(R.layout.activity_congruent_slider, container, false);
        count = Constants.myQueue.size();
        seekbar= (SeekBar)view.findViewById(R.id.seekbar);
        tx2 = (TextView)view.findViewById(R.id.totaltime);
        tx1 = (TextView)view.findViewById(R.id.currenttime);
        bp = (Button) view.findViewById(R.id.play);
        textView = (TextView) view.findViewById(R.id.textofplace);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(Constants.myQueue.get(0));
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.action_back);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftimager = getChildFragmentManager().beginTransaction();
                ftimager.remove(CongruentSlider.this);
                ftimager.commit();
                getFragmentManager().popBackStack();
            }
        });
//        textname = (TextView)view.findViewById(R.id.textname);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.ic_action_forward);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.myQueue.size() > 1) {
                    Constants.myQueue.remove(0);
                    filename = Constants.myQueue.get(0);
                    Log.i(Constants.placeTags.get(filename),"Historic");
                    if(Constants.placeTags.get(filename).equals("Historic")){
                        Log.i("This","on");
                        view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.historic));
                        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.historic));
                        imageButton.setBackgroundColor(getResources().getColor(R.color.historic));
                        imageButton1.setBackgroundColor(getResources().getColor(R.color.historic));
                    }
                    else if(Constants.placeTags.get(filename).equals("Cultural")){
                        view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.cultural));
                        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.cultural));
                        imageButton.setBackgroundColor(getResources().getColor(R.color.cultural));
                        imageButton1.setBackgroundColor(getResources().getColor(R.color.cultural));
                    }
                    else {
                        Log.i("This 2","on");
                        view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.scenic));
                        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.scenic));
                        imageButton.setBackgroundColor(getResources().getColor(R.color.scenic));
                        imageButton1.setBackgroundColor(getResources().getColor(R.color.scenic));
                    }
                 /*       textname.setText(Constants.myQueue.get(0));*/
                    collapsingToolbarLayout.setTitle(Constants.myQueue.get(0));
                    Log.i("File", new StringBuilder("Text/").append(filename).append(".txt").toString());
                    try {
                        br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(new StringBuilder("Text/").append(filename).append(".txt").toString())));
                        sb = new StringBuilder();
                        s = br.readLine();
                        while (s != null) {
                            sb.append(s);
                            s = br.readLine();
                        }
                        textView.setText(sb.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "File Not Found \n" + "Please Try Again!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageActivity imageActivity1 = new ImageActivity();
                    FragmentTransaction ftimager = getChildFragmentManager().beginTransaction();
                    ftimager.replace(R.id.imagefragContainer, imageActivity1, "TAG");
                    ftimager.commit();
                    try {
                        descriptor = getActivity().getAssets().openFd(new StringBuilder("Audio/").append(Constants.myQueue.get(0)).append(".m4a").toString());
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        descriptor.close();
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    FragmentTransaction ftimager = getChildFragmentManager().beginTransaction();
                    ftimager.remove(CongruentSlider.this);
                    ftimager.commit();
                    getFragmentManager().popBackStack();
                }
            }
        });
        filename = Constants.myQueue.get(0);
        if(Constants.placeTags.get(filename).equals("Historic")){
            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.historic));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.historic));
            imageButton.setBackgroundColor(getResources().getColor(R.color.historic));
            imageButton1.setBackgroundColor(getResources().getColor(R.color.historic));
        }
        else if(Constants.placeTags.get(filename).equals("Cultural")){
            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.cultural));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.cultural));
            imageButton.setBackgroundColor(getResources().getColor(R.color.cultural));
            imageButton1.setBackgroundColor(getResources().getColor(R.color.cultural));
        }
        else {
            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.scenic));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.scenic));
            imageButton.setBackgroundColor(getResources().getColor(R.color.scenic));
            imageButton1.setBackgroundColor(getResources().getColor(R.color.scenic));
        }
        Log.i("File", new StringBuilder("Text/").append(filename).append(".txt").toString());
        try {
            br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(new StringBuilder("Text/").append(filename).append(".txt").toString())));
            sb = new StringBuilder();
            s = br.readLine();
            while (s != null) {
                sb.append(s);
                s = br.readLine();
            }
            textView.setText(sb.toString());
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"File Not Found \n"+"Please Try Again!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageActivity imageActivity = new ImageActivity();
        FragmentTransaction ftimage = getChildFragmentManager().beginTransaction();
        ftimage.add(R.id.imagefragContainer,imageActivity,"TAG");
        ftimage.commit();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            descriptor = getActivity().getAssets().openFd(new StringBuilder("Audio/").append(Constants.myQueue.get(0)).append(".m4a").toString());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            seekbar.setMax((int) finalTime);
            seekbar.setProgress(0);
            tx2.setText(String.format("%d.%d min",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            );

            tx1.setText(String.format("%d.%d min",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        seekBar.setProgress(progress);
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekbar.setProgress((int) startTime);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (Constants.myQueue.size() > 1) {
                        Constants.myQueue.remove(0);
                        filename = Constants.myQueue.get(0);
                        if(Constants.placeTags.get(filename).equals("Historic")){
                            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.historic));
                            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.historic));
                            imageButton.setBackgroundColor(getResources().getColor(R.color.historic));
                            imageButton1.setBackgroundColor(getResources().getColor(R.color.historic));
                        }
                        else if(Constants.placeTags.get(filename).equals("Cultural")){
                            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.cultural));
                            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.cultural));
                            imageButton.setBackgroundColor(getResources().getColor(R.color.cultural));
                            imageButton1.setBackgroundColor(getResources().getColor(R.color.cultural));
                        }
                        else {
                            view.findViewById(R.id.imageRelative).setBackgroundColor(getResources().getColor(R.color.scenic));
                            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.scenic));
                            imageButton.setBackgroundColor(getResources().getColor(R.color.scenic));
                            imageButton1.setBackgroundColor(getResources().getColor(R.color.scenic));
                        }
                 /*       textname.setText(Constants.myQueue.get(0));*/
                        collapsingToolbarLayout.setTitle(Constants.myQueue.get(0));
                        Log.i("File", new StringBuilder("Text/").append(filename).append(".txt").toString());
                        try {
                            br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(new StringBuilder("Text/").append(filename).append(".txt").toString())));
                            sb = new StringBuilder();
                            s = br.readLine();
                            while (s != null) {
                                sb.append(s);
                                s = br.readLine();
                            }
                            textView.setText(sb.toString());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "File Not Found \n" + "Please Try Again!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ImageActivity imageActivity1 = new ImageActivity();
                        FragmentTransaction ftimager = getChildFragmentManager().beginTransaction();
                        ftimager.replace(R.id.imagefragContainer, imageActivity1, "TAG");
                        ftimager.commit();
                        try {
                            descriptor = getActivity().getAssets().openFd(new StringBuilder("Audio/").append(Constants.myQueue.get(0)).append(".m4a").toString());
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                            descriptor.close();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        imageButton.setVisibility(View.GONE);
                        FragmentTransaction ftimager = getChildFragmentManager().beginTransaction();
                        ftimager.remove(CongruentSlider.this);
                        ftimager.commit();
                        getFragmentManager().popBackStack();
                    }
                }
            });
            myHandler.postDelayed(UpdateSongTime, 100);
            bp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bp.getText() == "Play") {
                        Toast.makeText(getActivity(), "Playing sound", Toast.LENGTH_SHORT).show();
                        bp.setText("Pause");
                        mediaPlayer.start();
                        finalTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(0);
                        tx2.setText(String.format("%d.%d min",
                                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                        );

                        tx1.setText(String.format("%d.%d min",
                                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                        );
                        seekbar.setProgress((int) startTime);
                        myHandler.postDelayed(UpdateSongTime, 100);
                    } else {
                        Toast.makeText(getActivity(), "Pausing sound", Toast.LENGTH_SHORT).show();
                        bp.setText("Play");
                        mediaPlayer.pause();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Audio Files are Not Available!",Toast.LENGTH_SHORT).show();
        }
//        Log.i("Count", String.valueOf(count));

        return view;
    }



    @Override
    public void onPause() {
        mediaPlayer.pause();
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.stop();
        seekbar.setProgress((int) 0.00);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d.%d min",

                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


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


    }

