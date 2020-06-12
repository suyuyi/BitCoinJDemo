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

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class restore_wallet extends AppCompatActivity {
    protected EditText wallet_name,seed_string,seed_time;
    protected Switch network,sin_mul;
    protected Button restore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_seed_config);
        init_view();
    }
    protected void init_view()
    {
        wallet_name=(EditText)findViewById(R.id.editText3);
        seed_string=(EditText)findViewById(R.id.editText);
        seed_time=(EditText)findViewById(R.id.editText2);
        network=(Switch)findViewById(R.id.restore_network);
        sin_mul=(Switch)findViewById(R.id.restore_mulsig);
        restore=(Button)findViewById(R.id.restore_from_seed);
        seed_string.setText("place all now offer traffic extend cream gown basket crane hybrid sweet");
        seed_time.setText("1588488265");
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=String.valueOf(wallet_name.getText());
                String seed_word=String.valueOf(seed_string.getText());
                String seed_t=String.valueOf(seed_time.getText());
                if(TextUtils.isEmpty(name))
                {
                    Toast toast = Toast.makeText(restore_wallet.this,"名称不能为空",Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    try
                    {
                        new DeterministicSeed(seed_word,null,"",Long.valueOf(seed_t));
                    } catch (UnreadableWalletException e) {
                        Toast toast = Toast.makeText(restore_wallet.this,e.getMessage(),Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    List<String> s=getFilesAllName();
                    Iterator i=s.iterator();
                    Boolean Testnet=Boolean.FALSE;
                    Boolean Mul_sig=Boolean.FALSE;
                    Intent intent=new Intent();
                    intent.putExtra("word",seed_word);
                    intent.putExtra("time",seed_t);
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
                    if(sin_mul.isChecked())
                    {
                        //ToDo:add some new features
                        intent.setClass(restore_wallet.this,pre_multi.class);
                        name=name+"_multi";
//                        return;
                    }
                    else{
                        intent.setClass(restore_wallet.this,single.class);
                        name=name+"_single";
                    }
                    while(i.hasNext())
                    {
                        String tmp=(String)i.next();
                        if(tmp.contains(name+".wallet"))
                        {
                            Toast toast = Toast.makeText(restore_wallet.this,"已存在同名钱包",Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }
                    intent.putExtra("mode","restore");
                    intent.putExtra("name",name);
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
