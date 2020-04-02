package com.example.bitcoinjdemo;

import org.bitcoinj.core.Transaction;

import java.io.Serializable;
import java.util.List;

public class ser_Info_tx_list implements Serializable {
    private List<transaction_item_info> tx_list;
    public ser_Info_tx_list(List<transaction_item_info> tx_list)
    {
        this.tx_list=tx_list;
    }
    public List<transaction_item_info> get_tx_list()
    {
        return tx_list;
    }
}
