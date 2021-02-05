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
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        samster = (TextView) findViewById(R.id.address);
        grades = (TextView) findViewById(R.id.grade);
        name = (TextView) findViewById(R.id.nameOfStudent);
        subject = (TextView) findViewById(R.id.subject);


        studentsName = new ArrayList<>();
        idArray = new ArrayList<>();
        hlp = new HelperDB(this);
        getStudents();

        gi = getIntent();
        if(gi.getBooleanExtra("toDo",false))
        {
            gradeId = gi.getIntExtra("graeId",0);
        }

        show(samster);


        subject.setOnLongClickListener(this);


        subject.setOnLongClickListener(this);
        grades.setOnLongClickListener(this);
        samster.setOnLongClickListener(this);
        name.setOnLongClickListener(this);

    }

    public void show(View view) {

        // query
        String[] columns = {Grades.SUBJECT,Grades.GRADE,Grades.SAMASTER,Grades.STUDENT};
        String selection = Grades.GRADE_ID + "=?";
        String[] selectionArgs = {String.valueOf(gradeId)};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;

        db = hlp.getWritableDatabase();

        crsr = db.query(Grades.TABLE_GRADES, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        crsr.moveToFirst();

        int idIndex = 0;


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

        //getId("ori");
    }

    @Override
    public boolean onLongClick(View view) {
        subject.setOnLongClickListener(this);
        grades.setOnLongClickListener(this);
        samster.setOnLongClickListener(this);
        name.setOnLongClickListener(this);
        TextView[] textVies= {name,grades,samster,subject};
        String[] dataToPut= {"name","value","samster","subject"};
        final EditText et = new EditText(this);
        int[] idies= {(R.id.nameOfStudent),(R.id.grade),(R.id.samaster),(R.id.subject)};


        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeGrades.this);
        builder.setTitle("eneter");

        if (findIndex(idies, view.getId()) == 1 || findIndex(idies, view.getId()) == 2 )
        {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        builder.setView(et);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {


                if(findIndex(idies, view.getId()) == 0 && getId(et.getText().toString()).equals("") )
                {
                    Toast.makeText(ChangeGrades.this, et.getText().toString() + " is not exsist", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }

                // I got it
                String previousId = String.valueOf(gradeId);

                // When the user click yes button
                // then app will close
                ContentValues values;
                values = new ContentValues();

                values.put(Grades.RELEVANT, false);

                db = hlp.getWritableDatabase();

                db.update(Grades.TABLE_GRADES, values, "_id = ?", new String[]{previousId});
                db.close();
                textVies[findIndex(idies, view.getId())].setText(dataToPut[findIndex(idies, view.getId())]+"= "+et.getText());

                values = new ContentValues();
                values.put(Grades.STUDENT, getId(name.getText().toString().substring(name.getText().toString().indexOf("= ")+2)));
                values.put(Grades.SUBJECT, subject.getText().toString().substring(subject.getText().toString().indexOf("= ")+2));
                values.put(Grades.SAMASTER, samster.getText().toString().substring(samster.getText().toString().indexOf("= ")+2));
                values.put(Grades.GRADE, grades.getText().toString().substring(grades.getText().toString().indexOf("= ")+2));
                values.put(Grades.RELEVANT, true);
                db = hlp.getWritableDatabase();


                db.insert(Grades.TABLE_GRADES, null,values);

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