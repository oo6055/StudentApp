package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class showStudentsByGrades extends AppCompatActivity implements View.OnCreateContextMenuListener
{

    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    Spinner samasters;
    ArrayAdapter<String> adp;
    Cursor crsr;
    ListView ls;
    ContentValues values;


    AutoCompleteTextView classes;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_students_by_grades);
        classes = (AutoCompleteTextView) findViewById(R.id.clsses);
        ls = (ListView) findViewById(R.id.students);

        hlp = new HelperDB(this);
        getClasses();
        ls.setOnCreateContextMenuListener(this);
    }

    public void getClasses() {
        String[] columns = {Students.CLASS,Students.ACTIVE};
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
        tbl.add("students");
        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);
            String rel = crsr.getString(nameIndex);

            nameIndex = crsr.getColumnIndex(Students.CLASS);
            String name = crsr.getString(nameIndex);
            if(!tbl.contains(name) && rel.equals("1"))
            {
                tbl.add(name);
            }
            crsr.moveToNext();
        }
        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tbl);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        classes.setAdapter(adp);
        //getId("ori");
    }


    public void search(View view) {
        String numOfclass = classes.getText().toString();

        // query
        String[] columns = {Students.NAME,Students.ACTIVE};
        String selection = Students.CLASS + "=?";
        String[] selectionArgs = {numOfclass};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        String name = "";
        String rel = "";
        students = new ArrayList<>();


        db = hlp.getWritableDatabase();

        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;

        while (!crsr.isAfterLast())
        {
            idIndex = crsr.getColumnIndex(Students.NAME);
            name = crsr.getString(idIndex);

            idIndex = crsr.getColumnIndex(Students.ACTIVE);
            rel = crsr.getString(idIndex);

            if(rel.equals("1"))
            {
                students.add(name);
            }


            crsr.moveToNext();
        }

        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, students);
        ls.setAdapter(adp);
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
        inflater.inflate(R.menu.options, menu);
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

        if (op.equals("show grades"))
        {
            si = new Intent(this,ShowGrades.class);
            si.putExtra("name",students.get(i));
            si.putExtra("toDo",true);
            startActivity(si);
        }
        else if (op.equals("change details"))
        {
            si = new Intent(this,UpdateStudent.class);
            si.putExtra("name",students.get(i));
            si.putExtra("toDo",true);
            startActivity(si);
        }
        else if (op.equals("delete student"))
        {

            db = hlp.getWritableDatabase();

            // delete the grades
            values = new ContentValues();
            values.put(Grades.RELEVANT,false); // the new ID
            db.update(Grades.TABLE_GRADES, values, "Student = ?", new String[]{getId(students.get(i))});

            // delete the student
            values = new ContentValues();

            values.put(Students.ACTIVE, false);
            db = hlp.getWritableDatabase();

            db.update(Students.TABLE_STUDENTS, values, "_id = ?", new String[]{getId(students.get(i))});


            db.close();


            classes.setText(getIntent().getStringExtra("name"));
            search(ls);

            // need to change thew graeds to the new ID and to update the system
        }
        return true;
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

}