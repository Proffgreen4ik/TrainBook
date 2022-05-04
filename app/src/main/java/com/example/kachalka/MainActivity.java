package com.example.kachalka;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {


    public static String EX_INFO = "EX_INFO";
    static final String ACCESS_MESSAGE="ACCESS_MESSAGE";
    private static final int REQUEST_ACCESS_TYPE = 1;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;


    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    TextView textView = findViewById(R.id.textView);
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        String accessMessage = intent.getStringExtra(ACCESS_MESSAGE);
                        textView.setText(accessMessage);
                    }
                    else{
                        textView.setText("Ошибка доступа");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.spinner);
        Button b1 = findViewById(R.id.button);
        String[] exes = new String[7];
        exes = getResources().getStringArray(R.array.exes);
        Dictionary<String, String> infoAboutExes = new Hashtable<String, String>();


        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();
        for (int i=0;i<7;i++) {

            //получаем данные из бд в виде курсора
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(i+1)});

            userCursor.moveToNext();
            infoAboutExes.put(exes[i], userCursor.getString(1));
        }
        db.close();
        userCursor.close();
        //Проверяем если планшетный режим, то открываем планшетную активити
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(MainActivity.this, Tablet.class);
            mStartForResult.launch(intent);
        }
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Exercises.class);
                String info = CustomBtn.onClick(infoAboutExes, (String)spinner.getSelectedItem());
                intent.putExtra(EX_INFO, info);
                mStartForResult.launch(intent);
            }
        });
    }

}

