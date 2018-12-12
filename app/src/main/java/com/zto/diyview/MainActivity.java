package com.zto.diyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zto.diyview.db.DBTool;
import com.zto.diyview.db.StudentData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static SQLiteDatabase sqLiteDatabase;
    protected DiyDemo1 studentNumber,studentName,studentAge;
    protected Button addBtn,updateBtn,deleteBtn,selectBtn;
    protected ListView listView;
    protected InputMethodManager imm;
    private List<Student> mlist;
    private ListAdt listAdt;
    private int colorPosition=-1;
    SoundPool pool;
    Map<String,Integer> poolmap;
    boolean poolTrue=false;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    public void init(){
        Log.d(TAG, "init: 上传git成功");
        poolmap=new HashMap<String,Integer>();
        pool=new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        poolmap.put("scanok",pool.load(this,R.raw.scanok,1));
        poolmap.put("repeat",pool.load(this,R.raw.repeat,1));
        poolmap.put("warning",pool.load(this,R.raw.warning,1));
        pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId==poolmap.size()){
                    poolTrue=true;
                }
            }
        });


        Log.d(TAG, "init: "+ Build.MODEL);
        imm = ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE));

        studentNumber=findViewById(R.id.student_number);
        studentName=findViewById(R.id.student_name);
        studentAge=findViewById(R.id.student_age);
        studentAge.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        studentAge.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});


        addBtn=findViewById(R.id.add_button);
        addBtn.setOnClickListener(this);
        updateBtn=findViewById(R.id.update_button);
        updateBtn.setOnClickListener(this);
        deleteBtn=findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(this);
        selectBtn=findViewById(R.id.select_button);
        selectBtn.setOnClickListener(this);
        if (sqLiteDatabase==null){
            sqLiteDatabase=new DBTool(MainActivity.this).getReadableDatabase();
        }
        mlist= StudentData.selectAll();
        listAdt=new ListAdt();
        listView=findViewById(R.id.list_view);
        listView.setAdapter(listAdt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                colorPosition=parent.getCount()-position-1;
                changeColor();
                listAdt.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_button:
                if (check()){
                    Student student=StudentData.selectByNumber(studentNumber.getText());
                    if (colorPosition>-1&&colorPosition<mlist.size()){
                        mlist.get(colorPosition).setColor("白色");
                    }
                    if (student==null){
                        student=new Student();
                        student.setNumber(studentNumber.getText());
                        student.setName(studentName.getText());
                        student.setAge(studentAge.getText());
                        student.setColor("粉色");
                        if (StudentData.insert(student)){
                            pool.play(poolmap.get("scanok"), 1.0f, 1.0f, 0, 0, 1.0f);
                            if (mlist.size()==10){
                                mlist.remove(0);
                            }
                            mlist.add(student);

                        }else {
                            Toast.makeText(this, "插入失败", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        pool.play(poolmap.get("repeat"), 1.0f, 1.0f, 0, 0, 1.0f);
                        boolean isTrue=false;
                        int postion=0;
                        for (int i=0;i<mlist.size();i++){
                            if(student.getNumber().equals(mlist.get(i).getNumber())){
                                isTrue=true;
                                postion=i;
                                break;
                            }
                        }
                        if (isTrue){
                            mlist.remove(postion);
                            student.setColor("粉色");
                            mlist.add(student);
                        }else {
                            if (mlist.size()==10){
                                mlist.remove(0);
                            }
                            student.setColor("粉色");
                            mlist.add(student);
                        }
                    }
                    studentNumber.setText("");
                    studentName.setText("");
                    studentAge.setText("");
                    imm.hideSoftInputFromWindow(studentAge.editText.getWindowToken(), 0);
                    colorPosition=mlist.size()-1;
                    listAdt.notifyDataSetChanged();
                }
                break;
            case R.id.delete_button:
                if (colorPosition>=0&&colorPosition<mlist.size()){
                    new AlertDialog.Builder(this).setTitle("提示").setMessage("是否删除学生 "+mlist.get(colorPosition).getName()+" 的信息")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (StudentData.deleteByNumber(mlist.get(colorPosition).getNumber())){
                                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        mlist.remove(colorPosition);
                                        listAdt.notifyDataSetChanged();
                                        colorPosition=-1;
                                    }else {
                                        Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消",null).show();

                }else {
                    Toast.makeText(this, "请选择您要删除的项", Toast.LENGTH_SHORT).show();
                    pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
                }
                break;
            case R.id.update_button:
                if (colorPosition>-1&&colorPosition<mlist.size()){
                    dialog();
                }else {
                    Toast.makeText(this, "请选择你要修改的项", Toast.LENGTH_SHORT).show();
                    pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
                }
                
                break;
            case R.id.select_button:
                Intent intent=new Intent(MainActivity.this,SelectActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pool!=null){
            pool.release();
            pool=null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    Log.d(TAG, "onActivityResult: 1");
                    if (colorPosition>-1&&colorPosition<mlist.size()){
                        mlist.get(colorPosition).setColor("白色");
                    }
                    boolean isTrue=false;
                    int postion=0;
                    Student student = null;
                    for (int i=0;i<mlist.size();i++){
                        if(data.getStringExtra("number").equals(mlist.get(i).getNumber())){
                            isTrue=true;
                            postion=i;
                            student=mlist.get(i);
                            break;
                        }
                    }
                    if (isTrue){
                        mlist.remove(postion);
                        student.setColor("粉色");
                        mlist.add(student);
                    }else{
                        student=new Student();
                        student.setId(Integer.valueOf(data.getStringExtra("id")));
                        student.setNumber(data.getStringExtra("number"));
                        student.setName(data.getStringExtra("name"));
                        student.setAge(data.getStringExtra("age"));
                        student.setColor("粉色");
                        if (mlist.size()==10){
                            mlist.remove(0);
                        }
                        mlist.add(student);
                    }
                    colorPosition=mlist.size()-1;
                    changeColor();
                    listAdt.notifyDataSetChanged();
                    Log.d(TAG, "onActivityResult: 2");
                }
                break;
        }
    }

    protected void dialog() {
        final DiyDemo1 updateNumber,updateName,updateAge;
        Button returnBtn,updateBtn;
        View layout=LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update,null,false);
        updateNumber=layout.findViewById(R.id.update_number);
        updateNumber.setText(mlist.get(colorPosition).getNumber());
        updateName=layout.findViewById(R.id.update_name);
        updateName.setText(mlist.get(colorPosition).getName());
        updateAge=layout.findViewById(R.id.update_age);
        updateAge.setText(mlist.get(colorPosition).getAge());
        updateAge.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        updateAge.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        updateNumber.editText.setEnabled(false);
        returnBtn=layout.findViewById(R.id.return_button);
        updateBtn=layout.findViewById(R.id.update_button);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout).create();
        alertDialog.show();
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateName.getText().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                }else if (updateAge.getText().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "年龄不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onClick: "+updateName.getText());
                    Student student=mlist.get(colorPosition);
                    student.setName(updateName.getText());
                    student.setAge(updateAge.getText());
                    if (StudentData.updateByNumber(student)){
                        mlist.remove(colorPosition);
                        mlist.add(student);
                        colorPosition=mlist.size()-1;
                        changeColor();
                        listAdt.notifyDataSetChanged();
                    }else {
                        Toast.makeText(MainActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                    alertDialog.dismiss();
                }
            }
        });
    }


    public void changeColor(){
        for (int i=0;i<mlist.size();i++){
            if (colorPosition==i){
                mlist.get(i).setColor("粉色");
            }else {
                mlist.get(i).setColor("白色");
            }
        }
    }

    public boolean check(){
        if (studentNumber.getText().trim().isEmpty()){
            pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
            Toast.makeText(this, "学号不能为空", Toast.LENGTH_SHORT).show();
        }else if(studentName.getText().trim().isEmpty()){
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
        }else if(studentAge.getText().trim().isEmpty()){
            Toast.makeText(this, "年龄不能为空", Toast.LENGTH_SHORT).show();
            pool.play(poolmap.get("warning"), 1.0f, 1.0f, 0, 0, 1.0f);
        }else {
            return true;
        }
        return false;
    }

    class ListAdt extends BaseAdapter{
        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView==null){
                convertView= LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item,null,false);
                viewHolder=new ViewHolder();
                viewHolder.numberText=convertView.findViewById(R.id.number_text);
                viewHolder.nameText=convertView.findViewById(R.id.name_text);
                viewHolder.ageText=convertView.findViewById(R.id.age_text);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.numberText.setText(mlist.get(getCount()-position-1).getNumber());
            viewHolder.nameText.setText(mlist.get(getCount()-position-1).getName());
            viewHolder.ageText.setText(mlist.get(getCount()-position-1).getAge());
            if ("粉色".equals(mlist.get(getCount()-position-1).getColor())){
                convertView.setBackgroundResource(R.color.colorAccent);
            }else{
                convertView.setBackgroundResource(R.color.colorWhite);
            }
            return convertView;
        }
        class ViewHolder{
            TextView numberText;
            TextView nameText;
            TextView ageText;
        }
    }
}
