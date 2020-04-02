package com.example.bitcoinjdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.bitcoinj.core.Transaction;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.bitcoinjdemo.ser_Info_tx_list;

public class show_all_tx extends AppCompatActivity {
    private List<Transaction> tx_list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_transaction);
        ArrayList<transaction_item_info> data=(ArrayList<transaction_item_info>)getIntent().getSerializableExtra("ser_info");
        tx_adapter t_ad=new tx_adapter(show_all_tx.this,R.layout.tx_item,data);
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(t_ad);
    }
    public class tx_adapter extends ArrayAdapter<transaction_item_info>{
        private int resourceID;
        public tx_adapter(@NonNull Context context, int textViewResourceId, @NonNull List<transaction_item_info> objects) {
            super(context, textViewResourceId, objects);
            resourceID=textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            Fruit fruit = getItem(position); // 获取当前项的Fruit实例
            transaction_item_info t=getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
            TextView date=(TextView) view.findViewById(R.id.date);
            TextView coin=(TextView) view.findViewById(R.id.coin);
            TextView txid=(TextView) view.findViewById(R.id.txid);
            TextView depth=(TextView) view.findViewById(R.id.depth);
            date.setText(t.get_date().toString());
            coin.setText(t.get_coin().toFriendlyString());
            txid.setText(t.get_tx_id().toString());
            depth.setText(String.valueOf(t.get_depth()));
//            ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
//            TextView fruitName = (TextView) view.findViewById(R.id.fruit_name);
//            fruitImage.setImageResource(fruit.getImageId());
//            fruitName.setText(fruit.getName());
            return view;
//            return super.getView(position, convertView, parent);
        }
    }
}
