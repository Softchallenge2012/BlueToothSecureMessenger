package com.example.android.common.des;

/**
 * Created by shen on 3/16/17.
 */


public class SingleRound extends DES implements IDESRound {


    public byte[/*8*/] doOneRound( byte[/*8*/] data, byte[/*6*/] key ){

        // 1. divide the data into left(0)  and right(1) parts

        byte[] leftData = {data[0],data[1],data[2],data[3]};
        byte[]  rightData = {data[4],data[5],data[6],data[7]};

        long leftLong = DES.byteArrayToLong(rightData);
        long rightLong = DES.byteArrayToLong(rightData);


        // 2. mangle the right part with the key

        // 2.1 divide the right part and the key into 8 parts

        long[] right4bits = {0,0,0,0,0,0,0,0};
        for(int i = right4bits.length; i > 0; i--){
            for(int j = 0; j < 4; j++){
                right4bits[i-1]<<=1;
                right4bits[i-1] |= getBit(3-j,rightLong);

            }

            rightLong >>=4;

        }

        // expand rightData from 4 bits to 6 bits
        byte[] rightExpandedData = {0,0,0,0,0,0,0,0};
        for(int i = 0; i < rightExpandedData.length; i++){


            if(i == 0){

                rightExpandedData[i]<<=1;
                rightExpandedData[i] |=getBit(0, right4bits[right4bits.length-1]);
            }
            else{
                rightExpandedData[i] <<=1;
                rightExpandedData[i] |= getBit(0, right4bits[i-1]);
            }
            for(int j = 0; j < 4; j++){
                rightExpandedData[i] <<=1;
                rightExpandedData[i] |= getBit(3-j, right4bits[i]);
            }
            if(i==(rightExpandedData.length-1) ){
                rightExpandedData[i]<<=1;
                rightExpandedData[i] |=getBit(3, right4bits[0]);
            }
            else{
                rightExpandedData[i]<<=1;
                rightExpandedData[i] |= getBit(3, right4bits[i+1]);
            }
        }


        // 2.2 right xor key
        long newrightlong = 0;

        for(int i = 0; i < rightExpandedData.length; i++){
            for(int j = 0; j < 6; j++){

                newrightlong <<=1;
                newrightlong |= getBit(5-j, rightExpandedData[i]);
            }
        }

        long[] rightXORkey = {0,0,0,0,0,0,0,0};
        long rightXORkeyLong = 0;
        rightXORkeyLong =  newrightlong ^ byteArrayToLong(key);

        for(int i = rightXORkey.length; i > 0; i--){
            for(int j = 0; j < 6; j++){
                rightXORkey[i-1]<<=1;
                rightXORkey[i-1] |= getBit(5-j, rightXORkeyLong);
            }
            rightXORkeyLong >>=6;
        }


        long out = 0;

        /* without sbox

        rightLong = 0;
        for(int i = 0; i < rightXORkey.length; i++){

            for(int j = 0; j < 4; j++){

                out <<=1;
                out |= getBit(4-j, rightXORkey[i]);
            }
        }
*/

        // 2.3 go through s-box

        out = 0;
        for(int i = 0; i < rightXORkey.length; i++){

            long lr = getBit(5,rightXORkey[i]);
            lr <<=1;
            lr |= getBit(0,rightXORkey[i]);


            long middle = 0;
            for(int j = 0; j < 4; j++){
                middle <<=1;
                middle |= getBit(4-j, rightXORkey[i]);
            }

            out <<=4;

            if(lr == 0){
                out |= SBoxContents[i][0][(int)middle] ;
            }
            else if(lr == 1){
                out |= SBoxContents[i][1][(int)middle] ;
            }
            else if(lr == 2){
                out |= SBoxContents[i][2][(int)middle] ;
            }
            else if(lr == 3){
                out |= SBoxContents[i][3][(int)middle] ;
            }

        }


        // 2.4 s-box permutation

        long sboxPermuted = 0;


        for(int j = 0; j < permutationBits.length; j++){
            int loc = DES.permutationBits[j];
            sboxPermuted <<=1;
            sboxPermuted |= getBit(permutationBits.length-1-loc, out);
        }


        long newR = sboxPermuted ^ byteArrayToLong(leftData);
        long newL = byteArrayToLong(rightData);


        // 3. combine left and right parts.
        byte[] output = DES.twoLongsTo8ByteArray(newR, newL);

        return output;

    }
}
