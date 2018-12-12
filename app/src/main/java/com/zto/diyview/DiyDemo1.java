package com.zto.diyview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by wjy on 2018/12/10.
 */

public class DiyDemo1 extends RelativeLayout {
    TextView textView;
    EditText editText;
    private static final String TAG = "DiyDemo1";
    Context mContext;
    public DiyDemo1(Context context) {
        super(context);
        mContext=context;
        Log.d(TAG, "DiyDemo1: 1111");
        init();
    }
    public DiyDemo1(Context context,AttributeSet attrs){
        super(context,attrs);
        mContext=context;
        Log.d(TAG, "DiyDemo1: 2222");
        init();
        TypedArray array=mContext.obtainStyledAttributes(attrs,R.styleable.DiyDemo1);
        String text=array.getString(R.styleable.DiyDemo1_view_text);

        String hint=array.getString(R.styleable.DiyDemo1_edit_hint);
        Log.d(TAG, "DiyDemo1: "+text+"  ");
        textView.setText(text);
        editText.setHint(hint);
    }
    public void init(){
        inflate(mContext,R.layout.diy_view_demo1,this);
        Log.d(TAG, "init: 33333");

        textView=this.findViewById(R.id.demo1_text_view);
        editText=this.findViewById(R.id.demo1_edit);
        //addView(view);
        //TypedArray array=mContext.obtainStyledAttributes(attrs,)
    }
    public String getText(){
        return editText.getText().toString().trim();
    }
    public void setText(String s){
        editText.setText(s);
    }
}
