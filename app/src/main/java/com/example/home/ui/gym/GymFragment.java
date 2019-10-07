package com.example.home.ui.gym;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.home.MainActivity;
import com.example.home.R;
import com.example.home.SqlGymGoals;
import com.example.home.TableGymGoals;

public class GymFragment extends Fragment {

    private LinearLayout mainLayout;
    private GymViewModel gymViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        gymViewModel =
                ViewModelProviders.of(this).get(GymViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gym, container, false);
        final TextView textView = root.findViewById(R.id.title_gym);
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
        MainActivity.dbHelper.open();
        Cursor cursorGoals = MainActivity.dbHelper.database.rawQuery(SqlGymGoals.selectGoals(),null);

        if(cursorGoals.getCount()>0)cursorGoals.moveToFirst();
        for(int i=0; i<cursorGoals.getCount(); i++)
        {
            String textGoal = cursorGoals.getString(cursorGoals
                    .getColumnIndex(TableGymGoals.COLUMN_TEXT_GOAL));

            final  View view = getLayoutInflater().inflate(R.layout.fragment_goal, null);

            final CheckBox enableBox = view.findViewById(R.id.checkBox);
            enableBox.setText("Тренировка "+textGoal);

            enableBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.removeView(view);
                        }
                    };
                    //Если флажок установлен
                    if (isChecked) {
                        //делаем флажок невидимым
                        enableBox.setVisibility(View.INVISIBLE);
                        //находим кнопку с нужным айди
                        final Button btnCancel = view.findViewById(R.id.btn_cancel);
                        //делаем её видимлй
                        btnCancel.setVisibility(View.VISIBLE);
                        //запускаем таймер по окончанию которого удаляем вьюшку
                        handler.postDelayed(runnable, 1500);

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                handler.removeCallbacks(runnable);
                                enableBox.setChecked(false);
                                btnCancel.setVisibility(View.INVISIBLE);
                                enableBox.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        //not happened
                    }
                }
            });

            mainLayout.addView(view);
            cursorGoals.moveToNext();
        }

        cursorGoals.close();
        MainActivity.dbHelper.close();

    }
}