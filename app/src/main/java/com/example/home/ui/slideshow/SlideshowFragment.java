package com.example.home.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.home.R;
import com.example.home.ui.gym.GymViewModel;

public class SlideshowFragment extends Fragment {

    private LinearLayout hookahMainLayout;
    private GymViewModel gymViewModel;

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        hookahMainLayout = root.findViewById(R.id.hookah_main_layout);
        //final TextView textView = root.findViewById(R.id.text_slideshow);

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
        hookahMainLayout.addView(view);
    }
}