package com.example.bitcoinjdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.bitcoinjdemo.EncodingUtils;

public class show_qrcode extends Activity {
    ImageView QRcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_qrcode);
        Intent tmp=getIntent();
        String str=tmp.getStringExtra("receive_address");
        QRcode=(ImageView)findViewById(R.id.code_image1);
        Bitmap bitmap=EncodingUtils.createQRCode(str,200,200,null);
        QRcode.setImageBitmap(bitmap);
    }
}
