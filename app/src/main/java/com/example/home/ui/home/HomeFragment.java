package com.example.home.ui.home;

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

public class HomeFragment extends Fragment {
    private LinearLayout mainLayout;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mainLayout = root.findViewById(R.id.container_home_goals);

        addIndications();
        return root;
    }

    private void addIndications()
    {
        //Если число месяца == 1 добавить эту вьюшку и не убирать до заполнения
        final View view = getLayoutInflater().inflate(R.layout.fragment_home_goal, null);
        mainLayout.addView(view);
    }
}