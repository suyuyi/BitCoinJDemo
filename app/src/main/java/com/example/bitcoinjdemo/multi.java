package com.example.bitcoinjdemo;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.protobuf.ByteString;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.signers.CustomTransactionSigner;
import org.bitcoinj.signers.TransactionSigner;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyBag;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.MarriedKeyChain;
import org.bitcoinj.wallet.RedeemData;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class multi extends AppCompatActivity {
    protected WalletAppKit kit;
    protected NetworkParameters network;
    protected String wallet_name;
    protected Button send,refresh;
    protected TextView show_window;
    protected DeterministicKeyChain key_one,key_two;
    protected DeterministicSeed seed_one,seed_two,seed_m;
    protected MarriedKeyChain m;
    protected TransactionSigner cus;
    protected Address rece_addr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mul);
        init_key();
        start_wallet("mul_test1", TestNet3Params.get());
        init_window();
    }
    protected void init_window(){
        send=(Button)findViewById(R.id.send_mul);
        refresh=(Button)findViewById(R.id.refresh_mul);
        show_window=(TextView)findViewById(R.id.textView2);
        rece_addr=Address.fromString(TestNet3Params.get(),"2NGZrVvZG92qGYqzTLjCAewvPZ7JE8S8VxE");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("mul_mess","Signer_num:"+String.valueOf(kit.wallet().getTransactionSigners().size()));
//                print_redeemdata();
//                print_Unspent();
                Log.i("wallet_info",kit.wallet().toString());
                Log.i("wallet_info",kit.wallet().getTransactionsByTime().toString());
            }
        });
//        Output.getScriptPubKey().toString():HASH160 PUSHDATA(20)[c8b50c9a5769a230717f0ae17bec19beb362898a] EQUAL
//        Outpoint:2ca4ca91ff132c6f2cf3f4f9d54ab51449f25dc6044f26f077a04f54b604ab30:1
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("sendrequest_info","wallet_info:"+kit.wallet().getBalance().toFriendlyString());
                String send_v="0.0001";
                SendRequest sqr=SendRequest.to(rece_addr,Coin.parseCoin(send_v));
                sqr.feePerKb=Coin.ZERO;
//                sqr.missingSigsMode=Wallet.MissingSigsMode.USE_OP_ZERO;
//                Log.i("sendrequest_info","tx:"+sqr.tx.toString());
                try{
                    kit.wallet().completeTx(sqr);
                    Log.i("sendrequest_info","spent_value:"+send_v+"\nsqr:"+sqr.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("sendrequest_info","spent_value:"+send_v+"\nsqr:"+sqr.toString()+"\ne:"+e.toString());
                }
                Log.i("sendrequest_info","sqr_info:"+sqr.tx.toString()+"\nTxInfo:"+sqr.tx.getTxId().toString());
                kit.wallet().commitTx(sqr.tx);
                kit.peerGroup().broadcastTransaction(sqr.tx).broadcast();
            }
        });
    }
    protected void init_key(){
        String s1="math glare tail staff intact trophy super double cinnamon arch segment sheriff";
        long t1=1585623347;
        String s2="balcony mandate absorb lesson rude comic hurdle year rack brave nasty isolate";
        long t2=1585623701;
        String s3="fury odor city place orient void radar goose daring topic source cement";
        long t3=1585627015;
        try{
            seed_one=new DeterministicSeed(s1,null,"",t1);
            key_one= DeterministicKeyChain.builder().seed(seed_one).build();
            seed_two=new DeterministicSeed(s2,null,"",t2);
            key_two= DeterministicKeyChain.builder().seed(seed_two).build();
            m= MarriedKeyChain.builder().seed(seed_one)
                    .followingKeys(key_two.getWatchingKey().dropPrivateBytes().dropParent())
                    .threshold(2)
                    .build();
        }
        catch(UnreadableWalletException e)
        {
            key_one=null;
            key_two=null;
            Log.e("mul_mess","creat failed!");
        }
    }
    private void start_wallet(String name, NetworkParameters network){
        new Thread(new Runnable() {
            @Override
            public void run() {
                kit = new WalletAppKit(network, new File(getCacheDir()+""), name);
                kit.startAsync();
                kit.awaitRunning();
                kit.wallet().addAndActivateHDChain(m);
                cus=new CustomTransactionSigner() {
                    @Override
                    protected SignatureAndKey getSignature(Sha256Hash sha256Hash, List<ChildNumber> list) {
                        DeterministicKey from_k2=key_two.getKeyByPath(list,Boolean.TRUE);
                        try{
                            SignatureAndKey result=new SignatureAndKey(ECKey.ECDSASignature.decodeFromDER(from_k2.sign(sha256Hash).encodeToDER()),ECKey.fromPrivate(from_k2.getPrivKey()));
                            return result;
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
                kit.wallet().addTransactionSigner(cus);
            }
        }).start();
    }
    protected void print_Unspent()
    {
        Log.i("Unspent",kit.wallet().getUnspents().toString());
    }

    protected void print_send(String v)
    {
        SendRequest sqr=SendRequest.to(rece_addr,Coin.parseCoin(v));
        try {
            kit.wallet().completeTx(sqr);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
    }

    protected void print_redeemdata()
    {
        List<TransactionOutput> txopl=kit.wallet().getUnspents();
        Iterator i=txopl.iterator();
        while(i.hasNext())
        {
            TransactionOutput txop=(TransactionOutput)i.next();
            TransactionOutPoint txopt=txop.getOutPointFor();
            String txop_s=txop.toString();
            String txopt_s=txopt.toString();
            try
            {
//                ArrayList<DeterministicKeyChain> tmp_list=new ArrayList<DeterministicKeyChain>();
//                tmp_list.add(key_one);
//                tmp_list.add(key_two);
//                KeyChainGroup keyG= KeyChainGroup.builder(TestNet3Params.get()).chains(tmp_list).build();
                RedeemData rd=txopt.getConnectedRedeemData(kit.wallet());
                Log.i("redeemdata","Redeemdata:"+rd.toString());
                List<ECKey> klist=rd.keys;
                Iterator j=klist.iterator();
//                Log.i("redeemdata","KeyOne_WatchingKey:"+key_one.getWatchingKey().toString());
//                Log.i("redeemdata","KeyTwo_WatchingKey:"+key_two.getWatchingKey().toString());
                while(j.hasNext())
                {
                    DeterministicKey tmpkey=(DeterministicKey) j.next();
                    Log.i("redeemdata","Path:"+tmpkey.getPath().toString());
                    Log.i("redeemdata","KeyOne_Path:"+key_one.getKeyByPath(tmpkey.getPath(),Boolean.TRUE).toString());
                    Log.i("redeemdata","KeyTwo_Path:"+key_two.getKeyByPath(tmpkey.getPath(),Boolean.TRUE).toString());
                    Log.i("redeemdata","Redeemdata_keys:"+tmpkey.toString()+"\nPubKey:"+tmpkey.getPubKey().toString());
                    if(kit.wallet().findKeyFromPubKey(tmpkey.getPubKey())!=null)
                        Log.i("redeemdata","Find_in_wallet");
                    else if(key_one.findKeyFromPubKey(tmpkey.getPubKey())!=null)
                        Log.i("redeemdata","Find_in_keyone");
                    else if(key_two.findKeyFromPubKey(tmpkey.getPubKey())!=null)
                        Log.i("redeemdata","Find_in_keytwo");
                    else if(key_one.findKeyFromPubKey(key_one.getKeyByPath(tmpkey.getPath(),Boolean.TRUE).getPubKey())!=null)
                        Log.i("redeemdata","Find_in_keyone_c");
                    else if(key_two.findKeyFromPubKey(key_two.getKeyByPath(tmpkey.getPath(),Boolean.TRUE).getPubKey())!=null)
                        Log.i("redeemdata","Find_in_keytwo_c");
//                    else if(keyG.findKeyFromPubKey(tmpkey.getPubKey())!=null)
//                        Log.i("redeemdata","here we are");
                    else
                        Log.i("redeemdata","404_Not_Found");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            Log.i("redeemdata","\ntxop_scriPub:"+txop.getScriptPubKey()+"\ntxop:"+txop_s+"\ntxopt:"+txopt_s);
        }
    }
}
