package com.example.bitcoinjdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class creat_config extends AppCompatActivity {
    EditText wallet_name;
    Switch network,mul_sig;
    Button create_wallet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_wallet);
        init_window();
    }
    protected void init_window()
    {
        wallet_name=(EditText)findViewById(R.id.config_wallet_name);
        network=(Switch)findViewById(R.id.config_wallet_network);
        mul_sig=(Switch)findViewById(R.id.config_wallet_mulsig);
        create_wallet=(Button)findViewById(R.id.config_creat_wallet);
        create_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=String.valueOf(wallet_name.getText());
                if(TextUtils.isEmpty(name))
                {
                    Toast toast = Toast.makeText(creat_config.this,"名称不能为空",Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    List<String> s=getFilesAllName();
                    Iterator i=s.iterator();
                    while(i.hasNext())
                    {
                        String tmp=(String)i.next();
                        if(tmp.contains(name+".wallet"))
                        {
                            Toast toast = Toast.makeText(creat_config.this,"已存在同名钱包",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }
                    Boolean Testnet=Boolean.FALSE;
                    Boolean Mul_sig=Boolean.FALSE;
                    Intent intent=new Intent();
                    intent.putExtra("name",name);
                    if(network.isChecked())
                        intent.putExtra("testnet","1");
                    else
                        intent.putExtra("testnet","0");
                    if(mul_sig.isChecked())
                        intent.setClass(creat_config.this,multi.class);
                    else
                        intent.setClass(creat_config.this,single.class);
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
