package com.example.studentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UpdateStudent extends AppCompatActivity implements View.OnCreateContextMenuListener {
    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    ArrayAdapter<String> adp;
    Cursor crsr;
    TextView nametv;
    TextView gradetv;
    TextView addresstv;
    TextView personalPhonetv;
    TextView homePhonetv;
    TextView motherNametv;
    TextView motherPhonetv;
    TextView fatherNametv;
    TextView fatherPhonetv;
    int id;


    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);



        students = (AutoCompleteTextView) findViewById(R.id.student);
        nametv = (TextView) findViewById(R.id.name);
        gradetv = (TextView) findViewById(R.id.grade);
        addresstv = (TextView) findViewById(R.id.address);
        personalPhonetv = (TextView) findViewById(R.id.personalPhone);
        homePhonetv = (TextView) findViewById(R.id.homePhone);
        motherNametv = (TextView) findViewById(R.id.motherName);
        fatherNametv = (TextView) findViewById(R.id.fatherName);
        motherPhonetv = (TextView) findViewById(R.id.motherPhone);
        fatherPhonetv = (TextView) findViewById(R.id.fatherPhone);

        TextView[] idies= {nametv,gradetv,addresstv,personalPhonetv,homePhonetv,motherNametv,
                fatherNametv,motherPhonetv,fatherPhonetv};

        hlp = new HelperDB(this);
        getStudents();


        for (int i = 0; i < idies.length; i++)
        {
            idies[i].setOnCreateContextMenuListener(this);
        }

        if(getIntent().getBooleanExtra("toDo",false))
        {
            students.setText(getIntent().getStringExtra("name"));
            show(students);
        }
    }

    public void getStudents() {
        String[] columns = {Students.NAME,Students.ACTIVE};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int nameIndex = crsr.getColumnIndex(Students.NAME);
        tbl.add("students");
        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);
            String rel = crsr.getString(nameIndex);
            if (rel.equals("1"))
            {
                nameIndex = crsr.getColumnIndex(Students.NAME);
                String name = crsr.getString(nameIndex);
                tbl.add(name);
            }

            crsr.moveToNext();
        }
        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tbl);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        students.setAdapter(adp);
        //getId("ori");
    }

    public void show(View view) {
        String name = students.getText().toString();

        // need checkes
        String studentId = getId(name);

        // query
        String[] columns = {Students.NAME,Students.ACTIVE,Students.CLASS,Students.FATHER_NAME,Students.FATHER_PHONE
                ,Students.MOTHER_PHONE,Students.MOTHER_NAME,Students.HOME_PHONE,Students.ADDRESS,Students.PRIVATE_PHONE};
        String selection = Students.KEY_ID_STUDENT + "=?";
        String[] selectionArgs = {studentId};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        String temp = "";
        String rel = "";


        db = hlp.getWritableDatabase();

        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;

        while (!crsr.isAfterLast())
        {
            idIndex = crsr.getColumnIndex(Students.ACTIVE);
            rel = crsr.getString(idIndex);
            if (rel.equals("1"))
            {
                idIndex = crsr.getColumnIndex(Students.NAME);
                temp = crsr.getString(idIndex);
                nametv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.CLASS);
                temp = crsr.getString(idIndex);
                gradetv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.FATHER_NAME);
                temp = crsr.getString(idIndex);
                fatherNametv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.FATHER_PHONE);
                temp = crsr.getString(idIndex);
                fatherPhonetv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.MOTHER_NAME);
                temp = crsr.getString(idIndex);
                motherNametv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.MOTHER_PHONE);
                temp = crsr.getString(idIndex);
                motherPhonetv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.ADDRESS);
                temp = crsr.getString(idIndex);
                addresstv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.HOME_PHONE);
                temp = crsr.getString(idIndex);
                homePhonetv.setText(temp);

                idIndex = crsr.getColumnIndex(Students.PRIVATE_PHONE);
                temp = crsr.getString(idIndex);
                personalPhonetv.setText(temp);
            }
            crsr.moveToNext();
        }



        crsr.close();
        db.close();
    }
    private String getId(String s) {
        String[] columns = {Students.KEY_ID_STUDENT ,Students.ACTIVE }; // I am here
        String selection = Students.NAME + "=?";
        String[] selectionArgs = {s};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        String idStud = "";
        int nameIndex;


        db = hlp.getWritableDatabase();

        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();
        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);

            String rel = crsr.getString(nameIndex);
            if(rel.equals("1"))
            {
                nameIndex = crsr.getColumnIndex(Students.KEY_ID_STUDENT);
                idStud = crsr.getString(nameIndex);
                return idStud;
            }
            crsr.moveToNext();

        }


        crsr.close();
        db.close();
        return idStud;
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
     * @return	none
     */
    //@Overrid
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("options");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.updatestudentoption, menu);
        id = v.getId();
    }

    /**
     * onContextItemSelected
     * Short description.
     * onContextItemSelected listener use for the ContextMenu
     * <p>
     *     MenuItem item
     *
     * @param  item - the item that selected
     * @return	true if it worked
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView[] textVies= {nametv,gradetv,addresstv,personalPhonetv,homePhonetv,motherNametv,
                fatherNametv,motherPhonetv,fatherPhonetv};
        int[] idies= {(R.id.name),(R.id.grade),(R.id.address),(R.id.personalPhone),(R.id.homePhone),(R.id.motherName),
                (R.id.fatherName),(R.id.motherPhone),(R.id.fatherPhone)};
        String[] student = {Students.NAME,Students.CLASS,Students.ADDRESS,Students.PRIVATE_PHONE,Students.HOME_PHONE
                ,Students.MOTHER_NAME,Students.FATHER_NAME,Students.MOTHER_PHONE
                ,Students.FATHER_PHONE};

        builder = new AlertDialog.Builder(UpdateStudent.this);
        builder.setTitle("eneter");
        final EditText et = new EditText(this);
        if (findIndex(idies, id) == 3 || findIndex(idies, id) == 4 || findIndex(idies, id) == 7 || findIndex(idies, id) == 8)
        {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        builder.setView(et);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

                // I got it
                String previousId = getId(nametv.getText().toString());

                // When the user click yes button
                // then app will close
                ContentValues values;
                values = new ContentValues();
                values.put(Students.NAME, nametv.getText().toString());
                values.put(Students.ADDRESS, addresstv.getText().toString());
                values.put(Students.PRIVATE_PHONE, personalPhonetv.getText().toString());
                values.put(Students.HOME_PHONE, homePhonetv.getText().toString());
                values.put(Students.MOTHER_NAME, motherNametv.getText().toString());
                values.put(Students.FATHER_NAME, fatherNametv.getText().toString());
                values.put(Students.MOTHER_PHONE, motherPhonetv.getText().toString());
                values.put(Students.FATHER_PHONE, fatherPhonetv.getText().toString());
                values.put(Students.CLASS, gradetv.getText().toString());
                values.put(Students.ACTIVE, false);

                db = hlp.getWritableDatabase();

                db.update(Students.TABLE_STUDENTS, values, "_id = ?", new String[]{getId(nametv.getText().toString())});
                db.close();
                textVies[findIndex(idies, id)].setText(et.getText());

                values = new ContentValues();
                values.put(Students.NAME, nametv.getText().toString());
                values.put(Students.ADDRESS, addresstv.getText().toString());
                values.put(Students.PRIVATE_PHONE, personalPhonetv.getText().toString());
                values.put(Students.HOME_PHONE, homePhonetv.getText().toString());
                values.put(Students.MOTHER_NAME, motherNametv.getText().toString());
                values.put(Students.FATHER_NAME, fatherNametv.getText().toString());
                values.put(Students.MOTHER_PHONE, motherPhonetv.getText().toString());
                values.put(Students.FATHER_PHONE, fatherPhonetv.getText().toString());
                values.put(Students.CLASS, gradetv.getText().toString());
                values.put(Students.ACTIVE, true);
                db = hlp.getWritableDatabase();


                db.insert(Students.TABLE_STUDENTS, null,values);

                values = new ContentValues();
                values.put(Grades.STUDENT,getId(nametv.getText().toString())); // the new ID
                db.update(Grades.TABLE_GRADES, values, "Student = ?", new String[]{previousId});
                db.close();

                db.close();

                // need to change thew graeds to the new ID and to update the system
                dialog.cancel();





            }
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();


        return true;
    }

    private int findIndex(int[] idies, int id) {
        for (int i = 0; i < idies.length; i++)
        {
            if(id == idies[i])
            {
                return (i);
            }
        }
        return 0;
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

        if(whatClicked.equals("add student"))
        {
            si = new Intent(this,MainActivity.class);
            startActivity(si);
        }

        return  true;
    }

}