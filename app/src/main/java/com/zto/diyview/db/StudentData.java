package com.zto.diyview.db;

import android.database.Cursor;
import android.util.Log;

import com.zto.diyview.MainActivity;
import com.zto.diyview.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjy on 2018/12/11.
 */

public class StudentData {
    private static final String TAG = "StudentData";
    public static synchronized List<Student> selectByName(String name){
        Log.d(TAG, "selectByName: "+name);
        List<Student> list=new ArrayList<>();
        String sql="select id,number,name,age from student where name like '%"+name+"%' order by name desc limit 0,9";
        Log.d(TAG, "selectByName: "+sql);
        Cursor cursor=null;
        try {
            cursor=MainActivity.sqLiteDatabase.rawQuery(sql,null);
            while (cursor.moveToNext()){

                Student student=new Student();
                student.setId(cursor.getInt(0));
                student.setNumber(cursor.getString(1));
                student.setName(cursor.getString(2));
                student.setAge(cursor.getString(3));
                Log.d(TAG, "selectByName: "+student.getNumber());
                list.add(student);
            }
            return list;
        }catch (Exception e){
            return null;
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

    }

    public static synchronized boolean updateByNumber(Student student){
        String sql="update student set name=?,age=? where number=? and id=?";
        try {
            MainActivity.sqLiteDatabase.execSQL(sql,new String[]{student.getName(),student.getAge(),student.getNumber(), String.valueOf(student.getId())});
            return true;
        }catch (Exception e){
            return false;
        }


    }
    public static synchronized boolean deleteByNumber(String s){
        String sql="delete from student where number=?";
        try {
            MainActivity.sqLiteDatabase.execSQL(sql,new String[]{s});
            return true;
        }catch (Exception e){
            return false;
        }


    }
    public static synchronized boolean insert(Student student){
        String sql="insert into student(number,name,age) values(?,?,?)";
        try {
            MainActivity.sqLiteDatabase.execSQL(sql,new Object[]{student.getNumber(),student.getName(),student.getAge()});
            return true;
        }catch (Exception e){
            return false;
        }


    }
    public static synchronized List<Student> selectAll(){
        List<Student> list=new ArrayList<>();
        String sql="select id,number,name,age from student limit 0,9";
        Cursor cursor=null;
        try {
            cursor=MainActivity.sqLiteDatabase.rawQuery(sql,null);
            while (cursor.moveToNext()){
                Student student=new Student();
                student.setId(cursor.getInt(0));
                student.setNumber(cursor.getString(1));
                student.setName(cursor.getString(2));
                student.setAge(cursor.getString(3));
                list.add(student);
            }
            return list;
        }catch (Exception e){
            return null;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }

    }
    public static synchronized Student selectByNumber(String number){
        Student student=null;
        String sql="select id,number,name,age from student where number=?";
        Cursor cursor=null;
        try {
            cursor=MainActivity.sqLiteDatabase.rawQuery(sql,new String[]{number});
            while (cursor.moveToNext()){
                student=new Student();
                student.setId(cursor.getInt(0));
                student.setNumber(cursor.getString(1));
                student.setName(cursor.getString(2));
                student.setAge(cursor.getString(3));
            }
            return student;
        }catch (Exception e){
            return null;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }

    }
}
