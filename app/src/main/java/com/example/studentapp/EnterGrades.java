package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class EnterGrades extends AppCompatActivity {

    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    Spinner samasters;
    ArrayAdapter<String> adp;
    Cursor crsr;
    String[] samastersStr;
    EditText gradeValue;
    EditText subject;
    ContentValues values;

    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_grades);

        subject = (EditText) findViewById(R.id.subject);
        gradeValue = (EditText) findViewById(R.id.grade);

        samasters = (Spinner) findViewById(R.id.samaster);
        students = (AutoCompleteTextView) findViewById(R.id.clsses);
        samastersStr = new String[]{"Samasters","1","2","3","4"};
        adp = new ArrayAdapter<String>(this
                ,R.layout.support_simple_spinner_dropdown_item,samastersStr);
        samasters.setAdapter(adp);
        hlp = new HelperDB(this);
        getStudents();



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

    private boolean checkText(EditText e) {
        String text = e.getText().toString();
        if (!text.matches("")) {
            return true;
        }
        Toast.makeText(this, "fill everything",
                Toast.LENGTH_LONG).show();
        return false;
    }

    public void addGrade(View view) {
        boolean result = checkText(subject) && checkText(gradeValue) && Integer.valueOf(gradeValue.getText().toString()) > 0 && checkSpinner(samasters)&& checkText(students);


        if (result)
        {
            values = new ContentValues();
            values.put(Grades.GRADE, gradeValue.getText().toString());
            values.put(Grades.RELEVANT, true);
            values.put(Grades.SUBJECT , subject.getText().toString());
            values.put(Grades.SAMASTER , samastersStr[samasters.getSelectedItemPosition()]);

            //values.put(Grades.GRADE_ID , getGradeId());
            values.put(Grades.STUDENT, Integer.valueOf(getId((String) students.getText().toString())));
            // Inserting Row
            hlp = new HelperDB(this);
            db = hlp.getWritableDatabase();
            db.insert(Grades.TABLE_GRADES, null, values);
            db.close();
            gradeValue.setText("");
            subject.setText("");
            samasters.setSelection(0);
            students.setText("");
        }
    }

    private int getGradeId() {
        String[] columns = {Grades.GRADE_ID};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = Grades.GRADE_ID+" DESC";;
        String limit = null;
        String id = "";


        db = hlp.getWritableDatabase();

        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = crsr.getColumnIndex(Grades.GRADE_ID);

        while (!crsr.isAfterLast())
        {
            id = crsr.getString(idIndex);
            crsr.moveToNext();
        }

        crsr.close();
        db.close();
        if (id == "") {
            return 0;
        }
        return (Integer.valueOf(id)+1);
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

    private boolean checkSpinner(Spinner s) {
        if(s.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "choose in the spinner",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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

        if(whatClicked.equals("show grades"))
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
        else if (whatClicked.equals("change students details"))
        {
            si = new Intent(this,UpdateStudent.class);
            startActivity(si);
        }
        if(whatClicked.equals("add student"))
        {
            si = new Intent(this,MainActivity.class);
            startActivity(si);
        }

        return  true;
    }


}