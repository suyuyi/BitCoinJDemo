package com.example.bitcoinjdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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
                    while(i.hasNext())
                    {
                        String tmp=(String)i.next();
                        if(tmp.contains(name+".wallet"))
                        {
                            find_name=Boolean.TRUE;
                            break;
                        }
                    }
                    if(find_name=Boolean.FALSE)
                    {
                        Toast toast = Toast.makeText(load_config.this,"不存在同名钱包",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    Boolean Testnet=Boolean.FALSE;
                    Boolean Mul_sig=Boolean.FALSE;
                    Intent intent=new Intent();
                    intent.putExtra("name",name);
                    if(network.isChecked())
                        intent.putExtra("testnet","1");
                    else
                        intent.putExtra("testnet","0");
                    if(mulsig.isChecked())
                        intent.setClass(load_config.this,multi.class);
                    else
                        intent.setClass(load_config.this,single.class);
                    startActivity(intent);
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
