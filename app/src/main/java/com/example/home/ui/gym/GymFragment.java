package com.example.home.ui.gym;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

        Button btnAdd = root.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dial1.show(MainActivity.fragmentManager,"");
                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(getContext());
                View dialogView = li.inflate(R.layout.dialog_add_gym_goal, null);

                Button addGoal = dialogView.findViewById(R.id.btn_add_gym_goal);
                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = dialogView.findViewById(R.id.etGoal);


                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(dialogView);

                //Создаем AlertDialog:
                final AlertDialog alertDialog = mDialogBuilder.create();
                //делаем невидимый слой для стандартного AlertDialog
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                addGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userInput.getText().toString().length()>0) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TableGymGoals.COLUMN_TEXT_GOAL, userInput.getText().toString());
                            contentValues.put(TableGymGoals.COLUMN_STATUS_GOALS,0);
                            MainActivity.dbHelper.open();
                            MainActivity.dbHelper.database.insert(TableGymGoals.TABLE_NAME,null, contentValues);
                            MainActivity.dbHelper.close();
                            alertDialog.dismiss();
                            addFragments();
                        }
                    }
                });
                //и отображаем его:
                alertDialog.show();
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
                        handler.postDelayed(runnable, 1000);

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