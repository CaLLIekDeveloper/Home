package com.example.home.ui.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.home.R;

public class GymFragment extends Fragment {

    private LinearLayout mainLayout;
    private GymViewModel gymViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        gymViewModel =
                ViewModelProviders.of(this).get(GymViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gym, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        mainLayout = root.findViewById(R.id.containerGoals);
        addFragments();
        gymViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    private void addFragments()
    {
        mainLayout.removeAllViews();
        for(int i=0; i<4; i++)
        {
            final  View view = getLayoutInflater().inflate(R.layout.fragment_goal, null);

            final CheckBox enableBox = view.findViewById(R.id.checkBox);
            enableBox.setText("Цель "+i);

            enableBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        mainLayout.removeView(view);
                    } else {
                    }
                }
            });

            mainLayout.addView(view);
        }
    }
}