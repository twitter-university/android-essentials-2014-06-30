package com.twitter.university.android.yamba;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final int OP_POST = -1;
    private static final int OP_STATUS = -2;

    private static final String PARAM_OP = "YambaService.OP";
    private static final String PARAM_TWEET = "YambaService.TWEET";

    public static void post(Context ctxt, String tweet) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_TWEET, tweet);
        ctxt.startService(i);
    }


    private final class StatusHdlr extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OP_STATUS:
                    postComplete(msg.arg1);
                    break;
                default:
            }
        }
    }

    private volatile StatusHdlr hdlr;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();
        hdlr = new StatusHdlr();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int op = intent.getIntExtra(PARAM_OP, 0);
        switch (op) {
            case OP_POST:
                doPost(intent.getStringExtra(PARAM_TWEET));
                break;
            default:
                throw new IllegalStateException("unexpected op: " + op);
        }
    }

    private void doPost(String tweet) {
        int status = R.string.post_failed;
        try {
            Thread.sleep(1000 * 10);
            status = R.string.post_succeeded;
        }
        catch (InterruptedException e) { }

        hdlr.obtainMessage(OP_STATUS, status, 0).sendToTarget();
    }

    void postComplete(int status) {
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }
}
