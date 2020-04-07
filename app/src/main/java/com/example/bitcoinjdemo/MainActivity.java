package com.example.bitcoinjdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.bitcoinj.params.MainNetParams;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    ImageView title;
    Button creat,load,restore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        init_window();
        creat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,creat_config.class);
                startActivity(intent);
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,load_config.class);
                startActivity(intent);
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,restore_wallet.class);
                startActivity(intent);
            }
        });
        String filepath=getCacheDir().getPath()+"/test3_test_multi.json";
        try {
//            FileOutputStream op=new FileOutputStream(filepath);
//            JsonWriter jsw=new JsonWriter(new OutputStreamWriter(op,"UTF-8"));
//            jsw.beginObject();
//            jsw.name("key_cnt").value("2");
//            jsw.name("testnet").value("1");
//            jsw.name("key_cnt").value("3");
//            jsw.name("testnet").value("2");
//            jsw.endObject();;
//            jsw.close();
            FileInputStream ip=new FileInputStream(filepath);
            JsonReader jsr=new JsonReader((new InputStreamReader(ip,"UTF-8")));
            jsr.beginObject();
            while(jsr.hasNext())
            {
                if(jsr.nextName().equals("key_cnt"))
                {
                    Log.i("json_sth","key_cnt:"+jsr.nextString());
                }
                if(jsr.nextName().equals("threshold"))
                {
                    Log.i("json_sth","threshold:"+jsr.nextString());
                }
                if(jsr.nextName().equals("testnet"))
                {
                    Log.i("json_sth","testnet:"+jsr.nextString());
                }
            }
            jsr.endObject();
            jsr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json_sth","path"+filepath);
    }
    protected void init_window()
    {
        title=(ImageView)findViewById(R.id.title_image);
        title.setImageResource(R.drawable.btc_icon);
        creat=(Button)findViewById(R.id.creat_wallet);
        load=(Button)findViewById(R.id.load_wallet);
        restore=(Button)findViewById(R.id.restore_wallet);
    }
}
