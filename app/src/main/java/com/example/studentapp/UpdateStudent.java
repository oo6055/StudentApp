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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * The UpdateStudent activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 15/02/2021
 *  @version 1.0
 *  @since 15/02/2021
 *  sort description:
 *  this is the activty the implement the exercise that my teacher gave and in this activity I update the details of the student
 */
public class UpdateStudent extends AppCompatActivity implements View.OnLongClickListener {
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
    boolean thereIsSomeOne;
    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        students = (AutoCompleteTextView) findViewById(R.id.student);
        nametv = (TextView) findViewById(R.id.nameOfStudent);
        gradetv = (TextView) findViewById(R.id.grade);
        addresstv = (TextView) findViewById(R.id.samster);
        personalPhonetv = (TextView) findViewById(R.id.personalPhone);
        homePhonetv = (TextView) findViewById(R.id.homePhone);
        motherNametv = (TextView) findViewById(R.id.motherName);
        fatherNametv = (TextView) findViewById(R.id.fatherName);
        motherPhonetv = (TextView) findViewById(R.id.motherPhone);
        fatherPhonetv = (TextView) findViewById(R.id.fatherPhone);

        thereIsSomeOne = false;

        TextView[] idies= {nametv,gradetv,addresstv,personalPhonetv,homePhonetv,motherNametv,
                fatherNametv,motherPhonetv,fatherPhonetv};

        hlp = new HelperDB(this);
        hlp = new HelperDB(this);
        getStudents();

        // I am so Lazy (:
        for (int i = 0; i < idies.length; i++)
        {
            idies[i].setOnLongClickListener(this);
        }

        // if we got a command from other activity
        if(getIntent().getBooleanExtra("toDo",false))
        {
            students.setText(getIntent().getStringExtra("name"));
            show(students);
        }
    }

    /**
     * getStudents.
     * short dec: get the current student and put it in the autoComplited
     *
     * @return	none
     */
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

        tbl = new ArrayList<>();

        int nameIndex = crsr.getColumnIndex(Students.NAME);
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
    }

    /**
     * show.
     * short dec: show the details
     *
     * <p>
     *      View view
     * @param	view - see which button pressed
     * @return	none
     */
    public void show(View view) {
        String name = students.getText().toString();

        // need checkes
        String studentId = getId(name);

        // if the student is not exsist
        if(studentId.equals(""))
        {
            // zero the edit texts
            TextView[] idies= {nametv,gradetv,addresstv,personalPhonetv,homePhonetv,motherNametv,
                    fatherNametv,motherPhonetv,fatherPhonetv};
            String[] des = { "name", "class", "address" ,"personal Phone","home Phone","mother Name","father Name","mother Phone","father Phone"};

            // I am so Lazy (:
            for (int i = 0; i < idies.length; i++)
            {
                idies[i].setText(des[i]);
            }

            Toast.makeText(this, name + " is not exsist", Toast.LENGTH_SHORT).show();
            thereIsSomeOne = false;
            return;
        }

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

        // do the query
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;

        while (!crsr.isAfterLast())
        {
            idIndex = crsr.getColumnIndex(Students.ACTIVE);
            rel = crsr.getString(idIndex);

            // if the student is active
            if (rel.equals("1"))
            {
                // put the data into it
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
                thereIsSomeOne = true;
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

        // do the query
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
     * onLongClick.
     * short dec: take teh data and change the db
     *
     * <p>
     *      View view
     * @param	view - see which button pressed
     * @return	none
     */
    @Override
    public boolean onLongClick(View view) {
        TextView[] textVies= {nametv,gradetv,addresstv,personalPhonetv,homePhonetv,motherNametv,
                fatherNametv,motherPhonetv,fatherPhonetv};
        int[] idies= {(R.id.nameOfStudent),(R.id.grade),(R.id.samster),(R.id.personalPhone),(R.id.homePhone),(R.id.motherName),
                (R.id.fatherName),(R.id.motherPhone),(R.id.fatherPhone)};
        String[] student = {Students.NAME,Students.CLASS,Students.ADDRESS,Students.PRIVATE_PHONE,Students.HOME_PHONE
                ,Students.MOTHER_NAME,Students.FATHER_NAME,Students.MOTHER_PHONE
                ,Students.FATHER_PHONE};
        String[] des = { "name", "class", "address" ,"personal Phone","home Phone","mother Name","father Name","mother Phone","father Phone"};


        if (thereIsSomeOne)
        {
            // create alert
            builder = new AlertDialog.Builder(UpdateStudent.this);
            builder.setTitle("enter " + des[findIndex(idies, view.getId())]);
            final EditText et = new EditText(this);

            // if it is phone number
            if (findIndex(idies, view.getId()) == 3 || findIndex(idies, view.getId()) == 4 || findIndex(idies, view.getId()) == 7 || findIndex(idies, view.getId()) == 8)
            {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            builder.setView(et);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {

                    // get the ID
                    String previousId = getId(nametv.getText().toString());

                    ContentValues values;
                    values = new ContentValues();

                    values.put(Students.ACTIVE, false);

                    db = hlp.getWritableDatabase();

                    // change the active to false in the old student
                    db.update(Students.TABLE_STUDENTS, values, "_id = ?", new String[]{getId(nametv.getText().toString())});
                    db.close();
                    textVies[findIndex(idies, view.getId())].setText(et.getText());

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
        }


        return true;
    }

    /**
     * findIndex.
     * short dec: return the index of the id (-1 not found)
     *
     * <p>
     *      int[] idies
     *      int id
     * @param	id - the id that we wanna find, idies - the arr of the idies
     * @return	none
     */
    private int findIndex(int[] idies, int id) {
        for (int i = 0; i < idies.length; i++)
        {
            if(id == idies[i])
            {
                return (i);
            }
        }
        return -1;
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
        else if(whatClicked.equals("show grades"))
        {
            si = new Intent(this,ShowGrades.class);
            si.putExtra("toDo",false);
            startActivity(si);
        }
        else if (whatClicked.equals("show students By classes"))
        {
            si = new Intent(this,showStudentsByGrades.class);
            startActivity(si);
        }
        else if (whatClicked.equals("show students By classes"))
        {
            si = new Intent(this,showStudentsByGrades.class);
            startActivity(si);
        }
        else if(whatClicked.equals("add student"))
        {
            si = new Intent(this,GetStudent.class);
            startActivity(si);
        }
        else if(whatClicked.equals("credits"))
        {
            si = new Intent(this,Credits.class);
            startActivity(si);
        }

        return  true;
    }
}