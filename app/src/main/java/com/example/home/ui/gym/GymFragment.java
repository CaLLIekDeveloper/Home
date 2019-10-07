package com.example.home.ui.gym;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        registerForContextMenu(btnAdd);
        return root;
    }
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    private void addFragments()
    {
        mainLayout.removeAllViews();
        MainActivity.dbHelper.open();
        Cursor cursorGoals = MainActivity.dbHelper.database.rawQuery(SqlGymGoals.selectGoals(),null);

        //Если курсор не нул
        if(cursorGoals.moveToFirst()) {
            Cursor cursorNotCheckedGoals = MainActivity.dbHelper.database.rawQuery(SqlGymGoals.selectNotCheckedGoals(), null);
            if (cursorNotCheckedGoals.moveToFirst()) {
                for (int i = 0; i < cursorNotCheckedGoals.getCount(); i++) {
                    ////Изменить нижние строки если нужно будет)
                    String textGoal = cursorNotCheckedGoals.getString(cursorNotCheckedGoals
                            .getColumnIndex(TableGymGoals.COLUMN_TEXT_GOAL));
                    int id = cursorNotCheckedGoals.getInt(cursorNotCheckedGoals.getColumnIndex(TableGymGoals.COLUMN_ID));
                    final View view = getLayoutInflater().inflate(R.layout.fragment_goal, null);
                    view.setId(id);
                    ////

                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            setPosition(view.getId());
                            return false;
                        }
                    });

                    registerForContextMenu(view);

                    final CheckBox enableBox = view.findViewById(R.id.checkBox);
                    enableBox.setText("Тренировка " + textGoal);

                    enableBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {

                                    MainActivity.dbHelper.open();
                                    Log.e("id", "" + view.getId());
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(TableGymGoals.COLUMN_STATUS_GOALS, 1);

                                    MainActivity.dbHelper.database.update(TableGymGoals.TABLE_NAME, contentValues, TableGymGoals.COLUMN_ID + "= ?", new String[]{"" + view.getId()});
                                    MainActivity.dbHelper.close();

                                    mainLayout.removeView(view);
                                    if(mainLayout.getChildCount()==0)addFragments();
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
                    cursorNotCheckedGoals.moveToNext();
                }
                cursorNotCheckedGoals.close();
            }
            else
            {
                for(int i=0; i<cursorGoals.getCount(); i++)
                {
                    int id = cursorGoals.getInt(cursorGoals.getColumnIndex(TableGymGoals.COLUMN_ID));

                    MainActivity.dbHelper.open();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TableGymGoals.COLUMN_STATUS_GOALS, 0);
                    MainActivity.dbHelper.database.update(TableGymGoals.TABLE_NAME, contentValues, TableGymGoals.COLUMN_ID + "= ?", new String[]{"" + id});
                    MainActivity.dbHelper.close();
                    cursorGoals.moveToNext();
                }
                cursorGoals.close();
                MainActivity.dbHelper.close();
                addFragments();
            }
        }

        cursorGoals.close();
        MainActivity.dbHelper.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = MainActivity.menuInflater;
        inflater.inflate(R.menu.context_menu_gym_goals, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Log.e("Edit",""+position);

                LayoutInflater li = LayoutInflater.from(getContext());
                View dialogView = li.inflate(R.layout.dialog_edit_gym_goal, null);

                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(dialogView);

                //Создаем AlertDialog:
                final AlertDialog alertDialog = mDialogBuilder.create();
                //делаем невидимый слой для стандартного AlertDialog
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final EditText userInput = dialogView.findViewById(R.id.etGoalNew);

                MainActivity.dbHelper.open();
                Cursor cursor = MainActivity.dbHelper.database.rawQuery("SELECT * FROM " + TableGymGoals.TABLE_NAME +" WHERE id = "+position,null);
                cursor.moveToFirst();

                ((EditText)dialogView.findViewById(R.id.etGoalOld)).setText(cursor.getString(cursor.getColumnIndex(TableGymGoals.COLUMN_TEXT_GOAL)));
                cursor.close();
                MainActivity.dbHelper.close();
                Button btnYes = dialogView.findViewById(R.id.btn_gym_edit_ok);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userInput.length()>0) {
                            MainActivity.dbHelper.open();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TableGymGoals.COLUMN_TEXT_GOAL,userInput.getText().toString());
                            MainActivity.dbHelper.database.update(TableGymGoals.TABLE_NAME,contentValues, TableGymGoals.COLUMN_ID + "= ?", new String[]{"" + position});
                            MainActivity.dbHelper.close();
                            addFragments();
                            alertDialog.dismiss();
                        }
                        Toast.makeText(getContext(),R.string.fill_new_name,Toast.LENGTH_SHORT).show();

                    }
                });

                ((Button) dialogView.findViewById(R.id.btn_gym_edit_cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                //и отображаем его:
                alertDialog.show();
                return true;




            case R.id.delete:
                Log.e("Delete",""+position);
            ///Кусок кода с alerdDialog
                View dialogView1 =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_gym_goal, null);

                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder1 = new AlertDialog.Builder(getContext());

                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder1.setView(dialogView1);

                //Создаем AlertDialog:
                final AlertDialog alertDialog1 = mDialogBuilder1.create();
                //делаем невидимый слой для стандартного AlertDialog
                alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button btnYes1 = dialogView1.findViewById(R.id.btn_gym_delete_yes);
                btnYes1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            MainActivity.dbHelper.open();
                            MainActivity.dbHelper.database.delete(TableGymGoals.TABLE_NAME, TableGymGoals.COLUMN_ID + "= ?", new String[]{"" + position});
                            MainActivity.dbHelper.close();
                            addFragments();
                            alertDialog1.dismiss();

                    }
                });

                ((Button) dialogView1.findViewById(R.id.btn_gym_delete_no)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog1.dismiss();
                    }
                });

                //и отображаем его:
                alertDialog1.show();
             ///Конец кода с alerdDialog
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}