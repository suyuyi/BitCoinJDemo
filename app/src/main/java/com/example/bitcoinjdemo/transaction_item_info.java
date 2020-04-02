package com.example.bitcoinjdemo;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;

import java.io.Serializable;
import java.util.Date;

public class transaction_item_info implements Serializable {
    private Date day;
    private Sha256Hash txid;
    private Coin coin;
    private int depth;
    public transaction_item_info(Date day,Sha256Hash txid,Coin coin,int depth)
    {
        this.day=day;
        this.coin=coin;
        this.txid=txid;
        this.depth=depth;
    }
    public Date get_date()
    {
        return day;
    }
    public Coin get_coin()
    {
        return coin;
    }
    public Sha256Hash get_tx_id(){
        return txid;
    }
    public  int get_depth(){
        return depth;
    }
}
