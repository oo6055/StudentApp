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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * The ChangeGrades activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 15/02/2021
 *  @version 1.0
 *  @since 15/02/2021
 *  sort description:
 *  this is the activty the implement the exercise that my teacher gave and in this activity I change the grades...
 */
public class ChangeGrades extends AppCompatActivity implements OnLongClickListener  {
    Intent gi;
    int gradeId;
    SQLiteDatabase db;
    Cursor crsr;
    HelperDB hlp;
    ArrayList<String> studentsName;
    ArrayList<Integer> idArray;
    TextView samster;
    TextView grades;
    TextView name;
    TextView subject;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_grades);

        samster = (TextView) findViewById(R.id.samster);
        grades = (TextView) findViewById(R.id.grade);
        name = (TextView) findViewById(R.id.nameOfStudent);
        subject = (TextView) findViewById(R.id.subject);

        studentsName = new ArrayList<>();
        idArray = new ArrayList<>();
        hlp = new HelperDB(this);
        getStudents();

        gi = getIntent();

        // we touch the grade by the grade id
        if(gi.getBooleanExtra("toDo",false))
        {
            gradeId = gi.getIntExtra("graeId",0);
        }

        // show the details
        show();

        // put the lisners
        subject.setOnLongClickListener(this);
        subject.setOnLongClickListener(this);
        grades.setOnLongClickListener(this);
        samster.setOnLongClickListener(this);
        name.setOnLongClickListener(this);
    }

    /**
     * show.
     * short dec: show the grade's detainls
     *
     * @return	none
     */
    public void show() {
        String[] columns = {Grades.SUBJECT,Grades.GRADE,Grades.SAMASTER,Grades.STUDENT};
        String selection = Grades.GRADE_ID + "=?";
        String[] selectionArgs = {String.valueOf(gradeId)};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        // query
        db = hlp.getWritableDatabase();

        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;

        //get the grade details and put them on the tvs
        idIndex = crsr.getColumnIndex(Grades.SUBJECT);
        subject.setText("subject = "+crsr.getString(idIndex));

        idIndex = crsr.getColumnIndex(Grades.GRADE);
        grades.setText("value = "+crsr.getString(idIndex));

        idIndex = crsr.getColumnIndex(Grades.SAMASTER);
        samster.setText("samster = "+crsr.getString(idIndex));

        idIndex = crsr.getColumnIndex(Grades.STUDENT);
        name.setText("name = "+studentsName.get(idArray.indexOf(Integer.valueOf(crsr.getString(idIndex)))));


        crsr.close();
        db.close();
    }


    /**
     * getSubjects.
     * short dec: put the current subjects in the autoComplited
     *
     * @return	none
     */
    public void getStudents() {
        String[] columns = {Students.NAME,Students.ACTIVE,Students.KEY_ID_STUDENT};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        int gradeId;

        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int nameIndex = crsr.getColumnIndex(Students.NAME);

        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);
            String rel = crsr.getString(nameIndex);
            if (rel.equals("1"))
            {
                nameIndex = crsr.getColumnIndex(Students.NAME);
                String name = crsr.getString(nameIndex);
                studentsName.add(name);

                nameIndex = crsr.getColumnIndex(Students.KEY_ID_STUDENT);
                gradeId = Integer.valueOf(crsr.getString(nameIndex));
                idArray.add(gradeId);
            }

            crsr.moveToNext();
        }
        crsr.close();
        db.close();
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
        final int SAMASTER_INDEX = 2;
        final int GRADE_INDEX = 2;
        TextView[] textVies= {name,grades,samster,subject};
        String[] dataToPut= {"name","value","samster","subject"};
        final EditText et = new EditText(this);
        int[] idies= {(R.id.nameOfStudent),(R.id.grade),(R.id.samster),(R.id.subject)};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGrades.this);
        builder.setTitle("enter data");

        // if we choose grades or samster
        if (findIndex(idies, view.getId()) == GRADE_INDEX || findIndex(idies, view.getId()) == SAMASTER_INDEX )
        {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

                // if it is empty
                if (et.getText().toString().equals(""))
                {
                    Toast.makeText(ChangeGrades.this, "please enter data!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }
                // if we chose the spinner we need to check input
                if(findIndex(idies, view.getId()) == SAMASTER_INDEX && (Integer.valueOf(et.getText().toString()) < 1 || Integer.valueOf(et.getText().toString()) > 4 ))
                {
                    Toast.makeText(ChangeGrades.this, "samaster need to be 1-4!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }

                // if we choose the name and the user is not exsist
                if(findIndex(idies, view.getId()) == 0 && getId(et.getText().toString()).equals("") )
                {
                    Toast.makeText(ChangeGrades.this, et.getText().toString() + " is not exsist", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }

                // I got it
                String previousId = String.valueOf(gradeId);

                // When the user click yes button
                ContentValues values;
                values = new ContentValues();
                values.put(Grades.RELEVANT, false);

                // make the previous grade to not relevent
                db = hlp.getWritableDatabase();
                db.update(Grades.TABLE_GRADES, values, "_id = ?", new String[]{previousId});
                db.close();

                // change the tv
                textVies[findIndex(idies, view.getId())].setText(dataToPut[findIndex(idies, view.getId())]+"= "+et.getText());

                // enter a new grade
                values = new ContentValues();
                values.put(Grades.STUDENT, getId(name.getText().toString().substring(name.getText().toString().indexOf("= ")+2)));
                values.put(Grades.SUBJECT, subject.getText().toString().substring(subject.getText().toString().indexOf("= ")+2));
                values.put(Grades.SAMASTER, samster.getText().toString().substring(samster.getText().toString().indexOf("= ")+2));
                values.put(Grades.GRADE, grades.getText().toString().substring(grades.getText().toString().indexOf("= ")+2));
                values.put(Grades.RELEVANT, true);

                // put it into
                db = hlp.getWritableDatabase();
                gradeId =(int)db.insert(Grades.TABLE_GRADES, null,values); // get the new ID
                db.close();

                dialog.cancel();
            }
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();

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
     * getId.
     * short dec: return the index of the id (-1 not found)
     *
     * <p>
     *      String s
     * @param	s - the name
     * @return	the id of the student
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

        // create the query
        db = hlp.getWritableDatabase();
        crsr = db.query(Students.TABLE_STUDENTS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        while (!crsr.isAfterLast())
        {
            nameIndex = crsr.getColumnIndex(Students.ACTIVE);

            String rel = crsr.getString(nameIndex);
            // if we arrived to it
            if(rel.equals("1"))
            {
                nameIndex = crsr.getColumnIndex(Students.KEY_ID_STUDENT);
                idStud = crsr.getString(nameIndex);
                crsr.close();
                db.close();
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

        Intent si;
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