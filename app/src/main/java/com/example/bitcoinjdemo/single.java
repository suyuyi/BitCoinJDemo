package com.example.bitcoinjdemo;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitcoinjdemo.ser_Info_tx_list;

public class single<onStop> extends AppCompatActivity{
    protected WalletAppKit kit;
    protected NetworkParameters network;
    protected String wallet_name;
    protected TextView sync_info,balance_info,receive_address,fee,pending_info,tx_num;
    protected EditText send_address,coin_amount;
    protected Button receive,send,scan,show_seed,show_all;
    protected Boolean time_record;
    protected long time_distance;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    protected String mode;
    protected DeterministicSeed restore_seed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent single= getIntent();
        wallet_name=single.getStringExtra("name");
        mode=single.getStringExtra("mode");
        if(mode.contains("restore"))
        {
            try
            {
                restore_seed=new DeterministicSeed(single.getStringExtra("word"),null,"",Long.valueOf(single.getStringExtra("time")));
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }
        }
        if(Integer.valueOf(single.getStringExtra("testnet"))==1)
            network=TestNet3Params.get();
        else
            network=MainNetParams.get();
//        wallet_name=creat_single.getStringExtra("wallet_name");
//        if(creat_single.getStringExtra("network")=="main")
//            network= MainNetParams.get();
//        else
//            network= TestNet3Params.get();
        setContentView(R.layout.activity_single);
        start_wallet(wallet_name,network,mode);
        init_view();
        init_window();
    }
    private void start_wallet(String name,NetworkParameters network,String mode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                kit = new WalletAppKit(network, new File(getCacheDir()+""), name);
                if(mode.contains("restore"))
                    kit.restoreWalletFromSeed(restore_seed);
                kit.startAsync();
                kit.awaitRunning();
            }
        }).start();
    }
    private void init_view()
    {
        sync_info=(TextView) findViewById(R.id.sync_info);
        balance_info=(TextView) findViewById(R.id.balance_info);
        receive_address=(TextView) findViewById(R.id.receive_address);
        send_address=(EditText) findViewById(R.id.send_adress);
        coin_amount=(EditText)findViewById(R.id.coin_amount);
        receive=(Button)findViewById(R.id.receive);
        send=(Button)findViewById(R.id.send);
        scan=(Button)findViewById(R.id.scan_button);
        fee=(TextView)findViewById(R.id.fee);
        pending_info=(TextView)findViewById(R.id.pending_trans);
        tx_num=(TextView)findViewById(R.id.tx_num);
        show_seed=(Button)findViewById(R.id.show_seed);
        show_all=(Button)findViewById(R.id.show_all);
        time_record=Boolean.FALSE;
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent show_QRcode=new Intent(single.this,show_qrcode.class);
                show_QRcode.putExtra("receive_address",kit.wallet().currentReceiveAddress().toString());
                startActivity(show_QRcode);
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan_QRcode=new Intent(single.this,scan_qrcode.class);
                startActivityForResult(scan_QRcode,1);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_amount= String.valueOf(coin_amount.getText());
                String str_address=String.valueOf(send_address.getText());
                if(TextUtils.isEmpty(str_amount)||TextUtils.isEmpty(str_address)) {
                    Toast toast = Toast.makeText(single.this,"请输入金额和发送地址",Toast.LENGTH_LONG);
                    toast.show();
//                    Log.d("send_coin","no_amount");
                }
                else{//numberdecimal
                    Coin coin_amount=Coin.parseCoin(str_amount);
                    Address send_address;
                    try{
                        send_address=Address.fromString(network, str_address);
                    }
                    catch (AddressFormatException.WrongNetwork e)
                    {
                        Toast toast = Toast.makeText(single.this,"地址在所选网络上不存在",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    catch(AddressFormatException e)
                    {
                        Toast toast = Toast.makeText(single.this,"地址不合法",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    SendRequest request = SendRequest.to(send_address, coin_amount);
                    try{
                        kit.wallet().completeTx(request);
                    }
                    catch (InsufficientMoneyException e) {
                        Toast toast = Toast.makeText(single.this,"余额不足",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    catch (Wallet.DustySendRequested e)
                    {
                        Toast toast = Toast.makeText(single.this,"发送金额低于最小值，无法发送",Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    //TODO:commit&broadcast
                    showTwo(request);
                }

            }
        });
        show_seed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_str_seed();
            }
        });
        show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Transaction> tmp_list=kit.wallet().getTransactionsByTime();
                ArrayList<transaction_item_info> data = new ArrayList<transaction_item_info>();
                Iterator i=tmp_list.iterator();
                while(i.hasNext())
                {
                    Transaction t=(Transaction) i.next();
                    transaction_item_info t1=new transaction_item_info(t.getUpdateTime(),t.getTxId(),t.getValue(kit.wallet()),t.getConfidence().getDepthInBlocks());
                    data.add(t1);
                }
//                ser_Info_tx_list tx_data=new ser_Info_tx_list();
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("ser_info", (Serializable) data);
                intent.setClass(single.this,show_all_tx.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void init_window()
    {
//        SystemClock.sleep(1000);
        showLoading();
        new Thread() {
            public void run() {
                super.run();
//                while(true) {
                    refreshMSG();//刷新UI
//                }
            }
        }.start();
    }
    public void refreshMSG() {
        while(true)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            Message msg = Message.obtain();
            refresh_handler.sendMessage(msg);//sendMessage()用来传送Message类的值到mHandler
        }
    }

    final Handler refresh_handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                long last_unix_secs = kit.wallet().getLastBlockSeenTimeSecs();
                long now_unix_secs = System.currentTimeMillis();
                if (time_record == Boolean.FALSE) {
                    time_distance = now_unix_secs - last_unix_secs;
                    time_record = Boolean.TRUE;
                }
                Date day = kit.wallet().getLastBlockSeenTime();
                Log.d("time", String.valueOf(1 - ((now_unix_secs - last_unix_secs) / time_distance)) + "%");
                Coin balance = kit.wallet().getBalance();
                Address rece_addr = kit.wallet().currentReceiveAddress();
                sync_info.setText(day.toString());
                balance_info.setText(balance.toFriendlyString());
                receive_address.setText(rece_addr.toString());
                Log.i("receive_addr", rece_addr.toString());
                fee.setText("fee:" + Transaction.DEFAULT_TX_FEE.toFriendlyString());
                Collection<Transaction> pending = kit.wallet().getPendingTransactions();
//                List<Transaction> pending = kit.wallet().getRecentTransactions(1,Boolean.FALSE);
                if(pending.size()==0)
                {
                    tx_num.setText("Tx_num:"+String.valueOf(pending.size()));
                    pending_info.setText("当前没有正在Pending的交易");
                }
                else
                {
                    tx_num.setText("Tx_num:"+String.valueOf(pending.size()));
                    Iterator<Transaction> i=pending.iterator();
                    int cnt=0;
                    String s="";
                    while(i.hasNext())
                    {
                        Transaction t=(Transaction)i.next();
                        cnt++;
                        Date d=t.getUpdateTime();
                        Coin c=t.getValue(kit.wallet());
                        Sha256Hash sh=t.getTxId();
                        s=s+"Date:"+d.toString()+"\nCoin:"+c.toFriendlyString()+"\nTxID:"+sh.toString()+"\n";
                        if(cnt==3)
                        {
                            s=s+"查看更多请点击查看全部交易记录";
                            break;
                        }
                    }
                    pending_info.setText(s);
                }
                Log.d("fileaddress",getCacheDir().getAbsolutePath());
            } catch (Exception e) {
                sync_info.setText("Unknow");
                balance_info.setText("Unknow");
                receive_address.setText("Unknow");
                fee.setText("fee:"+"Unknow");
            }
        }
    };
    private void showTwo(SendRequest request) {
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("请确认发送")
                .setMessage("发送总计:"+request.tx.getValue(kit.wallet()).toFriendlyString()+"\n其中包含小费:"+request.tx.getFee().toFriendlyString())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        kit.wallet().commitTx(request.tx);
                        kit.peerGroup().broadcastTransaction(request.tx).broadcast();
                        send_address.setText("");
                        coin_amount.setText("");
                        Toast.makeText(single.this, "已发送", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
//                        Toast.makeText(single.this, "关闭按钮", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
    private void show_str_seed() {
        Log.i("seed_info","助记词:"+kit.wallet().getKeyChainSeed().getMnemonicCode().toString()+"\n时间盐:"+String.valueOf(kit.wallet().getKeyChainSeed().getCreationTimeSeconds()));
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("请勿向他人展示助记词")
                .setMessage("助记词:"+kit.wallet().getKeyChainSeed().getMnemonicCode().toString()+"\n时间盐:"+String.valueOf(kit.wallet().getKeyChainSeed().getCreationTimeSeconds()))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
//                        kit.wallet().commitTx(request.tx);
//                        kit.peerGroup().broadcastTransaction(request.tx).broadcast();
//                        send_address.setText("");
//                        coin_amount.setText("");
//                        Toast.makeText(single.this, "已发送", Toast.LENGTH_LONG).show();
                    }
                });
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //ToDo: 你想做的事情
////                        Toast.makeText(single.this, "关闭按钮", Toast.LENGTH_LONG).show();
//                        dialogInterface.dismiss();
//                    }
//                });
        builder.create().show();
    }

    private void showLoading() {
        final int MAX_VALUE = 100;
        progressDialog = new ProgressDialog(single.this);
        progressDialog.setProgress(0);
        progressDialog.setTitle("正在初始化");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(MAX_VALUE);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress < MAX_VALUE) {
                    try {
                        Thread.sleep(10);
                        progress++;
                        progressDialog.setProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //加载完毕自动关闭dialog
                progressDialog.cancel();
            }
        }).start();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 ){
            try{
                Bundle bundle = data.getExtras();
                String str = bundle.getString("QRcode");
                send_address.setText(str);
            }catch(Exception e)
            {
                Toast.makeText(single.this, "扫描错误，请重试", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AppStatue","Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AppStatue","Resumed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("AppStatue","Restarted");
    }
}
