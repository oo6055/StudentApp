package com.example.studentapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;



public class HelperDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbexam.db";
    private static final int DATABASE_VERSION = 14;
    String strCreate, strDelete;


    public HelperDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        strCreate="CREATE TABLE "+ Students.TABLE_STUDENTS;
        strCreate+=" ("+ Students.KEY_ID_STUDENT+" INTEGER PRIMARY KEY,";
        strCreate+=" "+Students.NAME+" TEXT,";
        strCreate+=" "+Students.CLASS+" TEXT,";
        strCreate+=" "+Students.ADDRESS+" TEXT,";
        strCreate+=" "+Students.ACTIVE+" INTEGER,"; // need to be boolean
        strCreate+=" "+Students.FATHER_NAME+" TEXT,";
        strCreate+=" "+Students.MOTHER_NAME+" TEXT,";
        strCreate+=" "+Students.FATHER_PHONE+" TEXT,";
        strCreate+=" "+Students.MOTHER_PHONE+" TEXT,";
        strCreate+=" "+Students.HOME_PHONE+" TEXT,";
        strCreate+=" "+Students.PRIVATE_PHONE+" TEXT";
        strCreate+=" "+Students.FATHER_NAME+" TEXT";
        strCreate+=");";
        sqLiteDatabase.execSQL(strCreate);


        strCreate="CREATE TABLE "+ Grades.TABLE_GRADES;
        strCreate+=" ("+Grades.STUDENT+" INTEGER,";
        strCreate+=" "+Grades.SUBJECT+" TEXT,";
        strCreate+=" "+Grades.GRADE+" REAL,";
        strCreate+=" "+Grades.GRADE_ID+" INTEGER PRIMARY KEY,";
        strCreate+=" "+Grades.RELEVANT+" INTEGER";
        strCreate+=");";
        sqLiteDatabase.execSQL(strCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        strDelete="DROP TABLE IF EXISTS "+Students.TABLE_STUDENTS;
        sqLiteDatabase.execSQL(strDelete);
        strDelete="DROP TABLE IF EXISTS "+Grades.TABLE_GRADES;
        sqLiteDatabase.execSQL(strDelete);

        onCreate(sqLiteDatabase);


    }
}

