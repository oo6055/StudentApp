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

/**
 * The EnterGrades activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 15/02/2021
 *  @version 1.0
 *  @since 15/02/2021
 *  sort description:
 *  this is the activty the implement the exercise that my teacher gave and in this activity I got the grades...
 */
public class EnterGrades extends AppCompatActivity {
    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    Spinner samasters;
    ArrayAdapter<String> adp;
    Cursor crsr;
    String[] samastersStr;
    EditText gradeValue;
    ContentValues values;

    AutoCompleteTextView subjects;
    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_grades);

        subjects = (AutoCompleteTextView) findViewById(R.id.subjects);
        gradeValue = (EditText) findViewById(R.id.grade);

        samasters = (Spinner) findViewById(R.id.samaster);
        students = (AutoCompleteTextView) findViewById(R.id.name);
        samastersStr = new String[]{"Samasters","1","2","3","4"};
        adp = new ArrayAdapter<String>(this
                ,R.layout.support_simple_spinner_dropdown_item,samastersStr);
        samasters.setAdapter(adp);
        hlp = new HelperDB(this);
        getStudents();
        getSubjects();
    }

    /**
     * getSubjects.
     * short dec: put the current subjects in the autoComplited
     *
     * @return	none
     */
    private void getSubjects() {
        String[] columns = {Grades.SUBJECT,Grades.RELEVANT};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        // do the query
        db = hlp.getWritableDatabase();
        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int nameIndex;
        tbl = new ArrayList<>();
        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Grades.RELEVANT);
            String rel = crsr.getString(nameIndex);

            nameIndex = crsr.getColumnIndex(Grades.SUBJECT);
            String grade = crsr.getString(nameIndex);

            // if the subject is relevewnt and it is not already exsist
            if (rel.equals("1") && tbl.indexOf(grade) == -1)
            {
                // add it the the tbl
                tbl.add(grade);
            }

            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        // put it (as a spinner)
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tbl);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        subjects.setAdapter(adp);
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

        int nameIndex;

        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);
            String rel = crsr.getString(nameIndex);

            // if he is active
            if (rel.equals("1"))
            {
                // add the name the table
                nameIndex = crsr.getColumnIndex(Students.NAME);
                String name = crsr.getString(nameIndex);
                tbl.add(name);
            }

            crsr.moveToNext();
        }
        crsr.close();
        db.close();

        // put the tbl in the auto complited
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tbl);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        students.setAdapter(adp);
    }

    /**
     * checkText.
     * short dec: check if it is fine
     *
     * <p>
     *      EditText e
     * @param	e - the et that we wanna check
     * @return	none
     */
    private boolean checkText(EditText e) {
        String text = e.getText().toString();
        if (!text.matches("")) {
            return true;
        }
        Toast.makeText(this, "fill everything",
                Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     * addGrade.
     * short dec: do a very specail action with colors and allow to the user to change the color as he wish
     *
     * <p>
     *      View view
     * @param	view - see which button pressed
     * @return	none
     */
    public void addGrade(View view) {

        boolean result = !subjects.getText().toString().equals("") && checkText(gradeValue) && Integer.valueOf(gradeValue.getText().toString()) > 0 && checkSpinner(samasters)&& checkText(students);

        // if all the data is in
        if (result)
        {
            // if the student is not exsist
            if(getId((String) students.getText().toString()) == "")
            {
                Toast.makeText(this,  students.getText().toString()+" is not exsist", Toast.LENGTH_SHORT).show();
                return;
            }

            // enter a new grade
            values = new ContentValues();
            values.put(Grades.GRADE, gradeValue.getText().toString());
            values.put(Grades.RELEVANT, true);
            values.put(Grades.SUBJECT , subjects.getText().toString());
            values.put(Grades.SAMASTER , samastersStr[samasters.getSelectedItemPosition()]);
            values.put(Grades.STUDENT, Integer.valueOf(getId((String) students.getText().toString())));

            // Inserting Row
            hlp = new HelperDB(this);
            db = hlp.getWritableDatabase();
            db.insert(Grades.TABLE_GRADES, null, values);
            db.close();

            // zero it
            gradeValue.setText("");
            subjects.setText("");
            samasters.setSelection(0);
            students.setText("");

            // update the subjects
            getSubjects();
        }
    }

    /**
     * getId.
     * short dec: get the ID by the name
     *
     * <p>
     *      String s
     * @param	s - the name
     * @return	the id
     */
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
        Cursor temp;

        // make query
        db = hlp.getWritableDatabase();
        temp = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        temp.moveToFirst();

        //start the scan
        while (!temp.isAfterLast())
        {
            nameIndex = temp.getColumnIndex(Students.ACTIVE);

            String rel = temp.getString(nameIndex);
            // if he is active
            if(rel.equals("1"))
            {
                //return the id
                nameIndex = temp.getColumnIndex(Students.KEY_ID_STUDENT);
                idStud = temp.getString(nameIndex);
                temp.close();
                db.close();
                return idStud;
            }
            temp.moveToNext();
        }
        // close and return ""
        temp.close();
        db.close();
        return idStud;
    }

    /**
     * checkSpinner.
     * short dec: check if we chose the spinner
     *
     * <p>
     *      Spinner s
     * @param	s - the spinner
     * @return	if it was chosen
     */
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
        else if (whatClicked.equals("show students by classes"))
        {
            si = new Intent(this,showStudentsByGrades.class);
            startActivity(si);
        }
        else if (whatClicked.equals("change students details"))
        {
            si = new Intent(this,UpdateStudent.class);
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