package com.example.kachalka;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.Hashtable;

public class Tablet extends AppCompatActivity {
    static final String ACCESS_MESSAGE="ACCESS_MESSAGE";
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
        setContentView(R.layout.activity_tablet);
        Spinner spinner = findViewById(R.id.spinner2);
        Button b1 = findViewById(R.id.button2);
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
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String info = CustomBtn.onClick(infoAboutExes, (String)spinner.getSelectedItem());
                TextView exeView = findViewById(R.id.textView);
                String desc = info;
                exeView.setText(desc);
            }
        });
        //Проверяем если портретный режим, то открываем портретную активити
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(Tablet.this, MainActivity.class);
            finish();
            mStartForResult.launch(intent);
        }
    }
}