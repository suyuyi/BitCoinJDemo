package com.example.bitcoinjdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class load_config extends AppCompatActivity {
    private Button load;
    private Switch mulsig,network;
    private EditText load_name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_wallet);
        init_window();
    }
    protected void init_window(){
        load=(Button)findViewById(R.id.load_wallet);
        mulsig=(Switch)findViewById(R.id.load_wallet_mulsig);
        network=(Switch)findViewById(R.id.load_wallet_network);
        load_name=(EditText)findViewById(R.id.load_wallet_name);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=String.valueOf(load_name.getText());
                if(TextUtils.isEmpty(name))
                {
                    Toast toast = Toast.makeText(load_config.this,"名称不能为空",Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    List<String> s=getFilesAllName();
                    Iterator i=s.iterator();
                    Boolean find_name=Boolean.FALSE;
                    Boolean Testnet=Boolean.FALSE;
                    Boolean Mul_sig=Boolean.FALSE;
                    Intent intent=new Intent();
                    intent.putExtra("mode","load");
                    if(network.isChecked())
                    {
                        intent.putExtra("testnet","1");
                        name=name+"_test";
                    }
                    else
                    {
                        intent.putExtra("testnet","0");
                        name=name+"_main";
                    }
                    if(mulsig.isChecked())
                    {
                        //ToDo:add some new features
                        intent.setClass(load_config.this,multi_v2.class);
                        name=name+"_multi";
//                        return;
                        String filepath=getCacheDir().getPath()+"/"+name+".json";
                        try {
                            FileInputStream ip=new FileInputStream(filepath);
                            JsonReader jsr=new JsonReader((new InputStreamReader(ip,"UTF-8")));
                            jsr.beginObject();
                            while(jsr.hasNext())
                            {
                                if(jsr.nextName().equals("key_cnt"))
                                {
                                    intent.putExtra("key_cnt",jsr.nextString());
                                }
                                if(jsr.nextName().equals("threshold"))
                                {
                                   intent.putExtra("threshold",jsr.nextString());
                                }
                            }
                            jsr.endObject();
                            jsr.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    else{
                        intent.setClass(load_config.this,single.class);
                        name=name+"_single";
                    }
                    intent.putExtra("name",name);
//                    if("test".contentEquals("test"))
//                        Log.i("load_info","equal");
//                    else
//                        Log.i("load_info","not equal");
                    while(i.hasNext())
                    {
                        String tmp=(String)i.next();
                        Log.i("load_info","tmp:"+tmp+"\nname:"+name);
                        if(tmp.contains(name+".wallet"))
                        {
                            find_name=Boolean.TRUE;
                            Log.i("load_info","tmp:"+tmp+"\nname:"+name);
                            break;
//                            return;
                        }
                    }
                    if(find_name==Boolean.TRUE)
                    {
//                        Log.i("load_info","load succ"+name);
                        startActivity(intent);
                    }
                    else {
                        Toast toast = Toast.makeText(load_config.this,"不存在同名钱包",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }
            }
        });
    }
    public List<String> getFilesAllName() {
        File file=new File (getCacheDir()+"");
        File[] files=file.listFiles();
        Log.e("herere",file.getAbsolutePath());
        if (files == null)
        {
            Log.e("herere",file.getAbsolutePath());
            return null;
        }
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
}
