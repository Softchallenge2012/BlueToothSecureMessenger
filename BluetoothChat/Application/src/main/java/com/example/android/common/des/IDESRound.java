package com.example.android.common.des;

/**
 * Created by shen on 3/16/17.
 */


public interface IDESRound {

    /** This function should carry out a single round of DES using
     the provided data and subkey for that round. */
    public byte[/*8*/] doOneRound( byte[/*8*/] data, byte[/*6*/] key );

}