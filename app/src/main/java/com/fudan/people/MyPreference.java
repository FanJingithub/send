package com.fudan.people;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fudan.helper.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FanJin on 2017/1/20.
 */

public class MyPreference extends BaseActivity {
    Button back;
    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<>();
    private Button con;
    TextView callTV,msgTV;
    private Spannable str1,str2;
    private Switch callChoice,msgChoice;
    SharedPreferences myPreference ;
    SharedPreferences.Editor editor;
    private boolean msg;
    private RelativeLayout view;
    private Button nameEdit,msgEdit;
    public static TextView nameListTV,msgBodyTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >=21){
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMain));
        }

        setContentView(R.layout.my_preference);
        myPreference = getSharedPreferences("myPreference",MODE_PRIVATE);
        editor = myPreference.edit();
        back = (Button) findViewById(R.id.back_preference);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmResult();
            }
        });

        callTV = (TextView) findViewById(R.id.call_tv);
        msgTV = (TextView) findViewById(R.id.msg_tv);
        nameListTV= (TextView)findViewById(R.id.name_list);
        msgBodyTV = (TextView)findViewById(R.id.msg_body);

        StyleSpan styleSpan_B  = new StyleSpan(Typeface.BOLD);
        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1.5f);

        str1=new SpannableString(getResources().getString(R.string.call_120));
        str1.setSpan(styleSpan_B, 0, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str1.setSpan(sizeSpan01, 0, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        callTV.setText(str1);

        str2=new SpannableString(getResources().getString(R.string.send_msg));
        str2.setSpan(styleSpan_B, 0, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str2.setSpan(sizeSpan01, 0, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        msgTV.setText(str2);

        callChoice = (Switch) findViewById(R.id.call_choice);
        msgChoice = (Switch) findViewById(R.id.msg_choice);
        nameEdit = (Button) findViewById(R.id.name_edit);
        msgEdit = (Button) findViewById(R.id.msg_edit);
        view = (RelativeLayout)findViewById(R.id.msg_main);
        msg = myPreference.getBoolean("msgChoice",false);
        if (msg){
            setClickable(true);
        }else {
            setClickable(false);
        }

        String str3 = myPreference.getString("nameList","");
        nameListTV.setText(str3);
        str3 = myPreference.getString("msgBody","");
        msgBodyTV.setText(str3);

        boolean bb = myPreference.getBoolean("callChoice",false);
        callChoice.setChecked(bb);
        bb= myPreference.getBoolean("msgChoice",false);
        msgChoice.setChecked(bb);

        callChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    editor.putBoolean("callChoice",true);
                    editor.apply();
                }else {
                    editor.putBoolean("callChoice",false);
                    editor.apply();
                }
            }
        });

        msgChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    editor.putBoolean("msgChoice",true);
                    editor.apply();
                    setClickable(true);
                }else {
                    editor.putBoolean("msgChoice",false);
                    editor.apply();
                    setClickable(false);
                }
            }
        });

        nameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(MyPreference.this,ReadContacts.class);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        msgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage();
            }
        });


    }

    /**
     * set the layout to be untouchable
     * @param b
     */
    private void setClickable(boolean b){
        msg = b;
        nameEdit.setClickable(b);
        msgEdit.setClickable(b);
        if (b){
            view.setAlpha((float)1.0);
        } else {
            view.setAlpha((float)0.3);
        }
    }

    private void confirmResult(){
        if ( ! msg){
            finish();
        } else {
            int count = myPreference.getInt("count",0);
            String msgBody = myPreference.getString("msgBody","");
            if (count==0){
                new AlertDialog.Builder(MyPreference.this)
                        .setMessage("已开启自动发送紧急短信功能，请填写联系人")
                        .setPositiveButton("确定",null )
                        .setCancelable(false)
                        .create()
                        .show();
            } else if (msgBody==""){
                new AlertDialog.Builder(MyPreference.this)
                        .setMessage("已开启自动发送紧急短信功能，请填写短信内容")
                        .setPositiveButton("确定",null )
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                finish();
            }
        }
    }

    private void setMessage(){
        final EditText inputServer = new EditText(MyPreference.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPreference.this);
        builder.setTitle("请输入短信内容")
                .setView(inputServer)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String str = inputServer.getText().toString();
                        editor.putString("msgBody",str);
                        editor.apply();
                        msgBodyTV.setText(str);
                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        //super.onBackPressed();
        confirmResult();
    }

}
