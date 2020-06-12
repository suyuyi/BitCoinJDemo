package com.example.bitcoinjdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.MarriedKeyChain;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class pre_multi extends AppCompatActivity {
    protected EditText key_num,key_threshold;
    protected Button creat_wallet;
    protected Intent intent;
    protected DeterministicKeyChain key_one,key_two,key3,key4,key5,key6;
    protected DeterministicSeed seed_one,seed_two,seed3,seed4,seed5,seed6,seed_m;
    protected NetworkParameters network;
    protected ArrayList<DeterministicKey> followingkey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_mul_config);
        init_key();
        intent=getIntent();
        //creat|load|restore
        if(intent.getStringExtra("testnet").contentEquals("0"))
            network= MainNetParams.get();
        else
            network= TestNet3Params.get();
        if(intent.getStringExtra("mode").contentEquals("creat"))
        {
            setContentView(R.layout.pre_mul_config);
            init_key();
            init_view_creat();
        }
        else if(intent.getStringExtra("mode").contentEquals("load"))
        {
        }
        else if(intent.getStringExtra("mode").contentEquals("restore"))
        {
            //ToDo:get key_cnt,threshold and following keys
            Intent load_intent=new Intent();
            load_intent.putExtra("name",getIntent().getStringExtra("name"));
            load_intent.putExtra("mode",getIntent().getStringExtra("mode"));
            load_intent.putExtra("word",getIntent().getStringExtra("word"));
            load_intent.putExtra("time",getIntent().getStringExtra("time"));
            String name=getIntent().getStringExtra("name");
            String filepath=getCacheDir().getPath()+"/"+name+".json";
            String key_cnt=null,threshold=null;
            try {
                FileInputStream ip=new FileInputStream(filepath);
                JsonReader jsr=new JsonReader((new InputStreamReader(ip,"UTF-8")));
                jsr.beginObject();
                while(jsr.hasNext())
                {
                    if(jsr.nextName().equals("key_cnt"))
                    {
                        key_cnt=jsr.nextString();
                    }
                    if(jsr.nextName().equals("threshold"))
                    {
                        threshold=jsr.nextString();
                    }
                }
                jsr.endObject();
                jsr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(pre_multi.this,"相关多重签名钱包备份文件不存在",Toast.LENGTH_LONG);
                toast.show();
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(pre_multi.this,"相关多重签名钱包备份文件编码错误",Toast.LENGTH_LONG);
                toast.show();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(pre_multi.this,"相关多重签名钱包备份文件读取错误",Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            if(network==MainNetParams.get())
                load_intent.putExtra("testnet","0");
            else
                load_intent.putExtra("testnet","1");
            load_intent.putExtra("threshold",threshold);
            load_intent.putExtra("key_cnt",key_cnt);
            int i=1;
            Iterator tky=followingkey.iterator();
            while(i<=Integer.valueOf(threshold)&&tky.hasNext())
            {

                DeterministicKey tmp=(DeterministicKey)tky.next();
                load_intent.putExtra("followingkey"+String.valueOf(i),tmp.serializePubB58(network));
                i++;
            }
            load_intent.setClass(pre_multi.this,multi_v2.class);
            startActivity(load_intent);
        }
    }
    protected void init_view_creat()
    {
        key_num=(EditText)findViewById(R.id.editText5);
        key_threshold=(EditText)findViewById(R.id.editText6);
        creat_wallet=(Button)findViewById(R.id.button);
        creat_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(key_num.getText().toString().isEmpty()||key_threshold.getText().toString().isEmpty())
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量以及下限不能为空",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                int key_cnt= Integer.valueOf(key_num.getText().toString());
                int key_thr=Integer.valueOf(key_threshold.getText().toString());
                if(key_cnt>6)
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量不可超过6",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(key_cnt<key_thr)
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量不能小于下限",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Intent intent_mul=new Intent();
                intent_mul.putExtra("name",intent.getStringExtra("name"));
                intent_mul.putExtra("mode",intent.getStringExtra("mode"));
                if(network==MainNetParams.get())
                    intent_mul.putExtra("testnet","0");
                else
                    intent_mul.putExtra("testnet","1");
                intent_mul.putExtra("threshold",String.valueOf(key_thr));
                intent_mul.putExtra("key_cnt",String.valueOf(key_cnt));
                int i=1;
                Iterator tky=followingkey.iterator();
                while(i<=key_thr&&tky.hasNext())
                {

                    DeterministicKey tmp=(DeterministicKey)tky.next();
                    intent_mul.putExtra("followingkey"+String.valueOf(i),tmp.serializePubB58(network));
                    i++;
                }
                intent_mul.setClass(pre_multi.this,multi_v2.class);
                String filepath=getCacheDir().getPath()+"/"+intent.getStringExtra("name")+".json";
                try {
                    FileOutputStream op=new FileOutputStream(filepath);
                    JsonWriter jsw=new JsonWriter(new OutputStreamWriter(op,"UTF-8"));
                    jsw.beginObject();
                    jsw.name("key_cnt").value(String.valueOf(key_cnt));
                    jsw.name("threshold").value(String.valueOf(key_thr));
                    jsw.endObject();;
                    jsw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(intent_mul);
            }
        });
    }

    protected void init_view_restore()
    {
        //ToDo:here we can add information about key_cnt
        key_num=(EditText)findViewById(R.id.editText5);
        key_threshold=(EditText)findViewById(R.id.editText6);
        creat_wallet=(Button)findViewById(R.id.button);
        creat_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(key_num.getText().toString().isEmpty()||key_threshold.getText().toString().isEmpty())
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量以及下限不能为空",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                int key_cnt= Integer.valueOf(key_num.getText().toString());
                int key_thr=Integer.valueOf(key_threshold.getText().toString());
                if(key_cnt>6)
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量不可超过6",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(key_cnt<key_thr)
                {
                    Toast toast = Toast.makeText(pre_multi.this,"密钥数量不能小于下限",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Intent intent_mul=new Intent();
                intent_mul.putExtra("name",intent.getStringExtra("name"));
                intent_mul.putExtra("mode",intent.getStringExtra("mode"));
                if(network==MainNetParams.get())
                    intent_mul.putExtra("testnet","0");
                else
                    intent_mul.putExtra("testnet","1");
                intent_mul.putExtra("threshold",String.valueOf(key_thr));
                intent_mul.putExtra("key_cnt",String.valueOf(key_cnt));
                int i=1;
                Iterator tky=followingkey.iterator();
                while(i<=key_thr-1&&tky.hasNext())
                {

                    DeterministicKey tmp=(DeterministicKey)tky.next();
                    intent_mul.putExtra("followingkey"+String.valueOf(i),tmp.serializePubB58(network));
                    i++;
                }
                intent_mul.setClass(pre_multi.this,multi_v2.class);
                startActivity(intent_mul);
            }
        });
    }
    protected void init_key(){
        followingkey=new ArrayList<DeterministicKey>();
        String s1="math glare tail staff intact trophy super double cinnamon arch segment sheriff";
        long t1=1585623347;
        String s2="balcony mandate absorb lesson rude comic hurdle year rack brave nasty isolate";
        long t2=1585623701;
        String s3="fury odor city place orient void radar goose daring topic source cement";
        long t3=1585627015;
        String s4="junior loop wet fat manage crawl egg vital derive scrub muscle believe";
        long t4=1585964485;
        String s5="liquid village follow pigeon banner eternal logic adapt forest jelly buyer case";
        long t5=1585964531;
        String s6="warm fire artefact foam lift finish such lizard spatial attend voyage agree";
        long t6=1585964553;
        try{
            seed_one=new DeterministicSeed(s1,null,"",t1);
            key_one= DeterministicKeyChain.builder().seed(seed_one).build();
            followingkey.add(key_one.getWatchingKey());
            seed_two=new DeterministicSeed(s2,null,"",t2);
            key_two= DeterministicKeyChain.builder().seed(seed_two).build();
            followingkey.add(key_two.getWatchingKey());
            seed3=new DeterministicSeed(s3,null,"",t3);
            key3= DeterministicKeyChain.builder().seed(seed3).build();
            followingkey.add(key3.getWatchingKey());
            seed4=new DeterministicSeed(s4,null,"",t4);
            key4= DeterministicKeyChain.builder().seed(seed4).build();
            followingkey.add(key4.getWatchingKey());
            seed5=new DeterministicSeed(s5,null,"",t5);
            key5= DeterministicKeyChain.builder().seed(seed5).build();
            followingkey.add(key5.getWatchingKey());
            seed6=new DeterministicSeed(s6,null,"",t6);
            key6= DeterministicKeyChain.builder().seed(seed6).build();
            followingkey.add(key6.getWatchingKey());
        }
        catch(UnreadableWalletException e)
        {
            key_one=null;
            key_two=null;
            Log.e("mul_mess","creat failed!");
        }
    }
}
