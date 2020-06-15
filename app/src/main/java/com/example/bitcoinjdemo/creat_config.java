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
// 为创建钱包配置相关输入，用户需要选择钱包类型（单 or 多签名）、存储文件名称、比特币网络
// 经过一定处理后通过intent(mode,name,testnet)的形式传入后续活动
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
                    Boolean Testnet=Boolean.FALSE;
                    Boolean Mul_sig=Boolean.FALSE;
                    Intent intent=new Intent();
                    // 此处对用户输入的名称添加后缀，这使得可以出现类型不同但命名相同的钱包
                    // 但如果类型、网络、命名都相同则会判违法，需要重新输入
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
                    if(mul_sig.isChecked())
                    {
                        //ToDo:add some new features
                        intent.setClass(creat_config.this,pre_multi.class);
                        name=name+"_multi";
//                        return;
                    }
                    else{
                        intent.setClass(creat_config.this,single.class);
                        name=name+"_single";
                    }
                    while(i.hasNext())
                    {
                        String tmp=(String)i.next();
                        // 需要注意的是此处只检查是否存在.wallet文件，而不检查json文件，而后者时存储多重签名钱包配置的文件
                        // 也就是说，如果用户丢失了多签名钱包文件，但未使用restore而直接使用creat同时其配置完全相同，那么
                        // 后续的pre_multi的配置中会出现问题，因为已经有一个json文件存在了，后续可能需要对此做出限制
                        if(tmp.contains(name+".wallet"))
                        {
                            Toast toast = Toast.makeText(creat_config.this,"已存在同名钱包",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }
                    intent.putExtra("name",name);
                    intent.putExtra("mode","creat");
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
