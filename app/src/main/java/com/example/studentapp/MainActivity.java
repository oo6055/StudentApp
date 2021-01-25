package com.example.studentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.studentapp.Students.ACTIVE;
import static com.example.studentapp.Students.ADDRESS;
import static com.example.studentapp.Students.FATHER_NAME;
import static com.example.studentapp.Students.FATHER_PHONE;
import static com.example.studentapp.Students.HOME_PHONE;
import static com.example.studentapp.Students.MOTHER_NAME;
import static com.example.studentapp.Students.MOTHER_PHONE;
import static com.example.studentapp.Students.NAME;
import static com.example.studentapp.Students.PRIVATE_PHONE;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    HelperDB hlp;

    EditText name;
    EditText phone;
    EditText address;
    EditText homePhone;
    EditText motherName;
    EditText motherPhone;
    EditText fatherName;
    EditText fatherPhone;
    EditText id;
    Intent si;
    ContentValues values;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.personalPhone);
        address = (EditText) findViewById(R.id.address);
        homePhone = (EditText) findViewById(R.id.homePhone);
        motherName = (EditText) findViewById(R.id.motherName);
        motherPhone = (EditText) findViewById(R.id.motherPhone);
        fatherName = (EditText) findViewById(R.id.fatherName);
        fatherPhone = (EditText) findViewById(R.id.fatherPhone);
        id = (EditText) findViewById(R.id.Id);
        hlp = new HelperDB(this);
    }


    public void submit(View view) {

        boolean isFine = checkData();
        if(isFine)
        {
            values = new ContentValues();
            values.put(Students.NAME, name.getText().toString());
            values.put(Students.ADDRESS, address.getText().toString());
            values.put(Students.PRIVATE_PHONE, phone.getText().toString());
            values.put(Students.HOME_PHONE, homePhone.getText().toString());
            values.put(Students.MOTHER_NAME, motherName.getText().toString());
            values.put(Students.FATHER_NAME, fatherName.getText().toString());
            values.put(Students.MOTHER_PHONE, motherPhone.getText().toString());
            values.put(Students.FATHER_PHONE, fatherPhone.getText().toString());
            values.put(Students.KEY_ID_STUDENT, id.getText().toString());
            values.put(Students.ACTIVE, true);
            // Inserting Row
            hlp = new HelperDB(this);
            db = hlp.getWritableDatabase();
            db.insert(Students.TABLE_STUDENTS, null, values);
            db.close();
            name.setText("");
            phone.setText("");
            address.setText("");
            homePhone.setText("");
            motherName.setText("");
            motherPhone.setText("");
            fatherName.setText("");
            fatherPhone.setText("");
            id.setText("");
        }


    }
    public static final String KEY_ID = "_id";


    private boolean checkPhone(EditText e) {
        String text = e.getText().toString();
        if (text.length() == 10) // need to check the number of bickort
        {
            return true;
        }
        Toast.makeText(this, "Invalid Phone number",
                Toast.LENGTH_LONG).show();
        return false;
    }
    private boolean checkID(EditText e) {
        String text = e.getText().toString();
        if(text.length() == 9) // need to check the number of bickort
        {
            return true;
        }
        Toast.makeText(this, "Invalid ID",
                Toast.LENGTH_LONG).show();
        return false;

    }
    private boolean checkText(EditText e) {
        String text = e.getText().toString();
        if(!text.matches(""))
        {
            return true;
        }
        Toast.makeText(this, "fill everything",
                Toast.LENGTH_LONG).show();
        return false;
    }
    private boolean checkData() {
        return (checkText(name) && checkText(address) && checkText(motherName)&& checkID(id) && checkText(fatherName)&& checkPhone(fatherPhone)&& checkPhone(motherPhone)&& checkPhone(homePhone)&& checkPhone(phone));
    }

    /**
     * onCreateContextMenu
     * Short description.
     * onCreateContextMenu listener use for the ContextMenu
     * <p>
     *     ContextMenu menu
     *     View v
     *     ContextMenu.ContextMenuInfo menuInfo
     *
     * @param  menu - the object,v - the item that selected ,menuInfo - the info
     * @return	true if it success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.generalmenu, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * Short description.
     * what happen if an item was selected
     * <p>
     *     MenuItem item
     *
     * @param  item - the menuItem
     * @return	true if it success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String whatClicked = (String) item.getTitle();

        if(whatClicked.equals("enter grade"))
        {
            si = new Intent(this,EnterGrades.class);
            startActivity(si);
        }

        return  true;
    }
}