package com.example.home.ui.slideshow;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.home.MyTime;
import com.example.home.R;
import com.example.home.ui.gym.GymViewModel;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private LinearLayout hookahMainLayout;
    private GymViewModel gymViewModel;

    private int lastId = 0;
    private SlideshowViewModel slideshowViewModel;

    private ArrayList<View> listHookahSmokers = new ArrayList<>();

    private ArrayList<Chronometer> listChronometers = new ArrayList<>();

    private ArrayList<MyTime> listHookahTimes = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        hookahMainLayout = root.findViewById(R.id.hookah_main_layout);
        //final TextView textView = root.findViewById(R.id.text_slideshow);

        Button btnStopTimers = root.findViewById(R.id.hookah_btn_stop_timers);

        btnStopTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i< listChronometers.size(); i++)
                {
                    listChronometers.get(i).stop();
                }
            }
        });

        addLayout();

        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    private void addLayout()
    {
        View view = getLayoutInflater().inflate(R.layout.fragment_line_hookah_smokers, null);
        View hookahSmokerView1 = view.findViewById(R.id.hookah_fragment_smoker_1);

        Chronometer chronometer1 = hookahSmokerView1.findViewById(R.id.hookah_time);

        hookahSmokerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 3;
                for(int i=0; i<listHookahSmokers.size(); i++)
                {
                    if (listHookahSmokers.get(i)==view)id = i;
                    listHookahSmokers.get(i).setBackgroundResource(0);
                    listChronometers.get(i).stop();
                            //setBackgroundColor(getResources().getColor(R.color.white));
                }
                Log.e("ID",""+id);
                Chronometer chronometer = view.findViewById(R.id.hookah_time);
                chronometer.setBase(SystemClock.elapsedRealtime()-chronometer.getBase());
                chronometer.start();
                view.setBackgroundResource(R.drawable.style_btn_green);
            }
        });


        listHookahSmokers.add(hookahSmokerView1);
        listChronometers.add(chronometer1);
        listHookahTimes.add(new MyTime());



        View hookahSmokerView2 = view.findViewById(R.id.hookah_fragment_smoker_2);

        Chronometer chronometer2 = hookahSmokerView2.findViewById(R.id.hookah_time);

        hookahSmokerView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 3;
                for(int i=0; i<listHookahSmokers.size(); i++)
                {
                    if (listHookahSmokers.get(i)==view)id = i;
                    listHookahSmokers.get(i).setBackgroundResource(0);
                    listChronometers.get(i).stop();
                }
                long elapsedTime = SystemClock.elapsedRealtime();
                listHookahTimes.get(id).startTime = elapsedTime;

                Log.e("ID",""+id);

                Chronometer chronometer = view.findViewById(R.id.hookah_time);
                chronometer.setBase(chronometer.getBase());
                chronometer.start();
                view.setBackgroundResource(R.drawable.style_btn_green);
            }
        });
        listHookahSmokers.add(hookahSmokerView2);
        listChronometers.add(chronometer2);
        listHookahTimes.add(new MyTime());
        hookahMainLayout.addView(view);

    }
}