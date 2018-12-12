package com.zto.diyview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zto.diyview.db.StudentData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectActivity extends AppCompatActivity {
    protected DiyDemo1 diyDemo1;
    protected Button selectBtn;
    protected ListView listView;
    protected List<Student> mlist;
    private static final String TAG = "SelectActivity";
    protected ListAdt listAdt;
    int colorPosition=-1;
    protected TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            new NameTask().execute(s+"");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        init();
    }
    public void init(){
        diyDemo1=findViewById(R.id.select_name_view);
        diyDemo1.editText.addTextChangedListener(textWatcher);
        selectBtn=findViewById(R.id.select_view_button);
        listView=findViewById(R.id.select_view_list_view);
        mlist= StudentData.selectAll();
        listAdt=new ListAdt();
        listView.setAdapter(listAdt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getCount()-position-1==colorPosition){
                    Log.d(TAG, "onItemClick: 11");
                    Intent intent=new Intent();
                    intent.putExtra("id",mlist.get(colorPosition).getId()+"");
                    intent.putExtra("number",mlist.get(colorPosition).getNumber());
                    intent.putExtra("name",mlist.get(colorPosition).getName());
                    intent.putExtra("age",mlist.get(colorPosition).getAge());
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    colorPosition=parent.getCount()-position-1;
                    changeColor();
                    listAdt.notifyDataSetChanged();
                    Log.d(TAG, "onItemClick: "+mlist.get(colorPosition).getName());
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
    class NameTask extends AsyncTask<String,Void,List<Student>>{


        @Override
        protected List<Student> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");
            return StudentData.selectByName(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Student> students) {
            super.onPostExecute(students);

            if (students!=null){
                mlist.clear();
                Collections.addAll(mlist,students.toArray(new Student[]{}));
                listAdt.notifyDataSetChanged();
            }

        }
    }
    class ListAdt extends BaseAdapter {
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
                convertView= LayoutInflater.from(SelectActivity.this).inflate(R.layout.list_item,null,false);
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
