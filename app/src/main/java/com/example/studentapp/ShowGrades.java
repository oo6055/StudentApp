package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ShowGrades activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 15/02/2021
 *  @version 1.0
 *  @since 15/02/2021
 *  sort description:
 *  this is the activty the implement the exercise that my teacher gave and in this activity I show the grades...
 */
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
    CustomAdapter adp1;

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

        // query the students
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        tbl = new ArrayList<>();

        int nameIndex = 0;
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
    }

    /**
     * search.
     * short dec: show the grades
     *
     * <p>
     *      View view
     * @param	view - see which button pressed
     * @return	none
     */
    public void search(View view) {
        if (cond) // search by student
        {
            String name = students.getText().toString();

            // need checkes
            String studentId = getId(name);
            if(studentId == "")
            {
                ls.setAdapter(null);
                Toast.makeText(this, name + " is not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // query
            String[] columns = {Grades.SUBJECT,Grades.RELEVANT,Grades.GRADE,Grades.GRADE_ID, Grades.SAMASTER};
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
            // do query
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

                // if the grade is relevent
                if (rel.equals("1"))
                {
                    idArr.add(Integer.valueOf(crsr.getString(idIndex)));

                    idIndex = crsr.getColumnIndex(Grades.SAMASTER);

                    //add it to the sorted
                    sortedString.add(subject + ':' + grade + ':' + crsr.getString(idIndex));
                    grades.add(subject + ':' + grade + ':' + crsr.getString(idIndex));


                }
                crsr.moveToNext();
            }

        }
        else // search by subject
        {
            String subject = students.getText().toString();
            String[] columns = {Grades.STUDENT,Grades.RELEVANT,Grades.GRADE,Grades.GRADE_ID,Grades.SAMASTER};
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

            // query from the db
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

                // if he's relevant
                if (rel.equals("1"))
                {
                    idArr.add(Integer.valueOf(crsr.getString(idIndex)));

                    idIndex = crsr.getColumnIndex(Grades.SAMASTER);
                    //add it to the sorted
                    sortedString.add(getName(name) +':' + grade + ':' + crsr.getString(idIndex));
                    grades.add(getName(name)  +':' + grade + ':' + crsr.getString(idIndex));
                }

                crsr.moveToNext();
            }
        }

        crsr.close();
        db.close();

        // sort it
        Collections.sort(sortedString);
        adp1 = new CustomAdapter(getApplicationContext(),sortedString);
        ls.setAdapter(adp1);
        ls.setOnCreateContextMenuListener(this);

    }

    /**
     * getName.
     * short dec: get the name
     *
     * <p>
     *     String id
     * @param	id - the id of it
     * @return	the name of it
     */
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

        // do query
        db = hlp.getWritableDatabase();
        temp = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        temp.moveToFirst();

        while (!temp.isAfterLast())
        {
            nameIndex = temp.getColumnIndex(Students.ACTIVE);

            String rel = temp.getString(nameIndex);

            // if he is active
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

    /**
     * getId.
     * short dec: get the id
     *
     * <p>
     *     String s
     * @param	s - the name
     * @return	the id of the name
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

    /**
     * changeCond.
     * change the sort condition
     *
     * <p>
     *      View view
     * @param	view - see which button pressed
     * @return	none
     */
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

    /**
     * changeCond.
     * put the subjects into the autoComplited
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

/**
 * The CustomAdapter activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 23/02/2021
 *  @version 1.0
 *  @since 15/02/2021
 *  sort description:
 *  temp for the customAdp
 */
class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> data;
    LayoutInflater inflter;

    /**
     * CustomAdapter
     * Short description.
     * the constuctor
     *
     * <p>
     *     Context applicationContext
     *     ArrayList<String> data
     * @param  data - the data in this format subject:grade:samster or name:grade:samster, applicationContext - the app contance
     * @return	true if it success
     */
    public CustomAdapter(Context applicationContext, ArrayList<String> data) {
        this.context = context;
        this.data = data;
        inflter = (LayoutInflater.from(applicationContext));
    }

    /**
     * getCount
     * Short description.
     * get the number of the grades
     *
     * @return the number of the elements
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * getItem
     * Short description.
     * I need to do it
     * <p>
     *    int i
     *
     * @param  i - the number of the object
     * @return null
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * getItemId
     * Short description.
     * I need to do it
     * <p>
     *    int i
     *
     * @param  i - the number of the object
     * @return null
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * getView
     * Short description.
     * in oder to create the view
     * <p>
     *    int i
     *    View view
     *    ViewGroup viewGroup
     *
     * @param  i - the number of the object,view - the view , viewGroup - the viewGroup
     * @return null
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String[] dataGrade = data.get(i).split(":");
        view = inflter.inflate(R.layout.custom_lv_layout, null);
        TextView subject = (TextView) view.findViewById(R.id.tV);
        TextView samster = (TextView) view.findViewById(R.id.samasterListView);
        TextView grade = (TextView) view.findViewById(R.id.gradePlace);

        samster.setTextColor(Color.BLACK);
        subject.setTextColor(Color.BLACK);

        subject.setText(dataGrade[0]);
        samster.setText(dataGrade[2]);
        if (Integer.valueOf(dataGrade[1]) < 56)
        {
            grade.setTextColor(Color.RED);
        }
        else if (Integer.valueOf(dataGrade[1]) > 90)
        {
            grade.setTextColor(Color.BLUE);
        }
        else
        {
            grade.setTextColor(Color.BLACK);
        }

        grade.setText(dataGrade[1]);
        return view;
    }
}
