package com.twitter.university.android.yamba;

import android.content.Context;
import android.content.Intent;

import com.twitter.university.android.yamba.service.YambaContract;


public class YambaServiceHelper {

    public static void post(Context ctxt, String tweet) {
        Intent i = new Intent(YambaContract.Service.ACTION_EXECUTE);
        i.putExtra(YambaContract.Service.PARAM_OP, YambaContract.Service.OP_POST);
        i.putExtra(YambaContract.Service.PARAM_TWEET, tweet);
        ctxt.startService(i);
    }
}
