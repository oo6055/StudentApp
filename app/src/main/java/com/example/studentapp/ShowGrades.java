package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class ShowGrades extends AppCompatActivity implements View.OnCreateContextMenuListener {
    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    ArrayAdapter<String> adp;
    Cursor crsr;
    ListView ls;
    ArrayList<Integer> idArr;

    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayList<String> grades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grades);

        students = (AutoCompleteTextView) findViewById(R.id.clsses);
        ls = (ListView) findViewById(R.id.grades);

        hlp = new HelperDB(this);
        getStudents();
        if(getIntent().getBooleanExtra("toDo",false))
        {
            students.setText(getIntent().getStringExtra("name"));
            search(ls);
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

    public void search(View view) {
        String name = students.getText().toString();

        // need checkes
        String studentId = getId(name);

        // query
        String[] columns = {Grades.SUBJECT,Grades.RELEVANT,Grades.GRADE,Grades.GRADE_ID};
        String selection = Grades.STUDENT + "=?";
        String[] selectionArgs = {studentId};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        String subject = "";
        String grade = "";
        String rel;
        grades = new ArrayList<>();
        idArr = new ArrayList<>();


        db = hlp.getWritableDatabase();

        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;

        while (!crsr.isAfterLast())
        {
            idIndex = crsr.getColumnIndex(Grades.SUBJECT);
            subject = crsr.getString(idIndex);

            idIndex = crsr.getColumnIndex(Grades.RELEVANT);
            rel = crsr.getString(idIndex);

            idIndex = crsr.getColumnIndex(Grades.GRADE);
            grade = crsr.getString(idIndex);

            idIndex = crsr.getColumnIndex(Grades.GRADE_ID);

            if (rel.equals("1"))
            {
                idArr.add(Integer.valueOf(crsr.getString(idIndex)));

                grades.add(subject + ":" + grade);
            }


            crsr.moveToNext();
        }

        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, grades);
        ls.setAdapter(adp);
        ls.setOnCreateContextMenuListener(this);
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
        inflater.inflate(R.menu.gradeoption, menu);
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
        String op = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int i = info.position;


        if (op.equals("change grade"))
        {
            si = new Intent(this,ChangeGrades.class);
            si.putExtra("graeId",idArr.get(i));
            si.putExtra("toDo",true);
            startActivity(si);
            search(ls);
        }

        else if (op.equals("delete grade"))
        {

            db = hlp.getWritableDatabase();

            // delete the grades
            ContentValues values = new ContentValues();
            values.put(Grades.RELEVANT,false); // the new ID
            db.update(Grades.TABLE_GRADES, values, "_id = ?", new String[]{String.valueOf(idArr.get(i))});

            search(ls);
            // need to change thew graeds to the new ID and to update the system
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

        if(whatClicked.equals("enter grade"))
        {
            si = new Intent(this,EnterGrades.class);
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