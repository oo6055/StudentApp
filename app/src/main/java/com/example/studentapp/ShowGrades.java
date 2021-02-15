package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowGrades extends AppCompatActivity implements View.OnCreateContextMenuListener {
    SQLiteDatabase db;
    HelperDB hlp;
    Intent si;
    ArrayAdapter<String> adp;
    Cursor crsr;
    ListView ls;
    ArrayList<Integer> idArr;
    boolean cond;
    Switch sc;
    ArrayList<String> sortedString;

    AutoCompleteTextView students;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayList<String> grades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grades);

        cond = true;

        sc = (Switch) findViewById(R.id.switch1);
        students = (AutoCompleteTextView) findViewById(R.id.name);
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

        tbl = new ArrayList<>();

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
        if (cond)
        {
            String name = students.getText().toString();

            // need checkes
            String studentId = getId(name);
            if(studentId == "")
            {
                Toast.makeText(this, name + " is not found", Toast.LENGTH_SHORT).show();
                return;
            }

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
            sortedString = new ArrayList<>();


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

                    sortedString.add(subject + ":" + grade);
                    grades.add(subject + ":" + grade);
                }


                crsr.moveToNext();
            }

        }
        else
        {
            String subject = students.getText().toString();

            // query
            String[] columns = {Grades.STUDENT,Grades.RELEVANT,Grades.GRADE,Grades.GRADE_ID};
            String selection = Grades.SUBJECT + "=?";
            String[] selectionArgs = {subject};
            String groupBy = null;
            String having = null;
            String orderBy = null;
            String limit = null;
            String grade = "";
            String rel;
            String name;
            grades = new ArrayList<>();
            idArr = new ArrayList<>();
            sortedString = new ArrayList<>();

            db = hlp.getWritableDatabase();

            crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            crsr.moveToFirst();

            int idIndex = 0;

            while (!crsr.isAfterLast())
            {
                idIndex = crsr.getColumnIndex(Grades.RELEVANT);
                rel = crsr.getString(idIndex);

                idIndex = crsr.getColumnIndex(Grades.GRADE);
                grade = crsr.getString(idIndex);

                idIndex = crsr.getColumnIndex(Grades.STUDENT);
                name = crsr.getString(idIndex);

                idIndex = crsr.getColumnIndex(Grades.GRADE_ID);

                if (rel.equals("1"))
                {
                    idArr.add(Integer.valueOf(crsr.getString(idIndex)));

                    sortedString.add(getName(name) + ":" + grade);
                    grades.add(getName(name) + ":" + grade);
                }


                crsr.moveToNext();
            }
        }

        crsr.close();
        db.close();

        Collections.sort(sortedString);
        adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sortedString);
        ls.setAdapter(adp);
        ls.setOnCreateContextMenuListener(this);

    }
    private String getName(String id) {
        String[] columns = {Students.NAME ,Students.ACTIVE };
        String selection = Students.KEY_ID_STUDENT + "=?";
        String[] selectionArgs = {id};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        String name = "";
        Cursor temp;
        int nameIndex;


        db = hlp.getWritableDatabase();

        temp = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        temp.moveToFirst();
        while (!temp.isAfterLast())
        {
            nameIndex = temp.getColumnIndex(Students.ACTIVE);

            String rel = temp.getString(nameIndex);
            if(rel.equals("1"))
            {
                nameIndex = temp.getColumnIndex(Students.NAME);
                name = temp.getString(nameIndex);
                temp.close();
                db.close();
                return name;

            }
            temp.moveToNext();

        }


        temp.close();
        db.close();
        return name;
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
        Cursor temp;


        db = hlp.getWritableDatabase();
        temp = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        temp.moveToFirst();
        while (!temp.isAfterLast())
        {
            nameIndex = temp.getColumnIndex(Students.ACTIVE);

            String rel = temp.getString(nameIndex);
            if(rel.equals("1"))
            {
                nameIndex = temp.getColumnIndex(Students.KEY_ID_STUDENT);
                idStud = temp.getString(nameIndex);
                temp.close();
                db.close();
                return idStud;
            }
            temp.moveToNext();

        }


        temp.close();
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
            si.putExtra("graeId",idArr.get(grades.indexOf(sortedString.get(i))));
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
            db.update(Grades.TABLE_GRADES, values, "_id = ?", new String[]{String.valueOf(idArr.get(grades.indexOf(sortedString.get(i))))});

            search(ls);
            // need to change thew graeds to the new ID and to update the system
        }

        return true;
    }

    public void changeCond(View view) {
        cond = !cond;
        ls.setAdapter(null);
        students.setText("");

        if(cond)
        {
            students.setHint("Student Name");
            getStudents();
        }
        else
        {
            students.setHint("Subjects Name");
            getSubjects();
        }
    }

    private void getSubjects() {
        String[] columns = {Grades.SUBJECT,Grades.RELEVANT};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

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
            if (rel.equals("1") && tbl.indexOf(grade) == -1)
            {

                tbl.add(grade);
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
        else if(whatClicked.equals("add student"))
        {
            si = new Intent(this,MainActivity.class);
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