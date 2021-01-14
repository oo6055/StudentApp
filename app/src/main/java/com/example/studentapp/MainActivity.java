package com.example.studentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    //SQLiteDatabase db;
    HelperDB hlp;
    ListView details;

    AlertDialog.Builder builder;
    ArrayAdapter<String> adp;
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void zeroArr(String[] arr)
    {
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = "";
        }
    }
    public void update(ListView ls,String[] arr)
    {

        adp = new ArrayAdapter<String>(this
        ,R.layout.support_simple_spinner_dropdown_item,arr);
        ls.setAdapter(adp);
    }

    public void getText(String poprpose) {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(poprpose);
        final EditText et = new EditText(this);
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

                // When the user click yes button
                // then app will close
                temp = et.getText().toString();
                dialog.cancel();
            }
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();


        // Show the Alert Dialog box
        alertDialog.show();

    }
}