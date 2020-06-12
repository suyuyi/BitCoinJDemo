package com.example.bitcoinjdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

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
