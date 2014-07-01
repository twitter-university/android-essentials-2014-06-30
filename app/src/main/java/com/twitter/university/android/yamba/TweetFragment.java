package com.twitter.university.android.yamba;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class TweetFragment extends Fragment {

    static class Poster extends AsyncTask<String, Void, Integer> {
        private final Context ctxt;
        public Poster(Context ctxt) { this.ctxt = ctxt; }

        @Override
        protected Integer doInBackground(String... tweet) {
            int status = R.string.post_failed;
            try {
                Thread.sleep(1000 * 60 * 3);
                status = R.string.post_succeeded;
            }
            catch (InterruptedException e) { }
            return Integer.valueOf(status);
        }

        @Override
        protected void onPostExecute(Integer status) {
            Toast.makeText(ctxt, status.intValue(), Toast.LENGTH_LONG).show();
            cleanUp();
        }

        @Override
        protected void onCancelled() {
            cleanUp();
        }

        private void cleanUp() { poster = null; }
    }

    static volatile Poster poster;

    private int okColor;
    private int warnColor;
    private int errColor;

    private int tweetLenMax;
    private int warnMax;
    private int errMax;

    private EditText tweetView;
    private TextView countView;
    private Button submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources rez = getResources();
        okColor = rez.getColor(R.color.green);
        tweetLenMax = rez.getInteger(R.integer.tweet_limit);
        warnColor = rez.getColor(R.color.yellow);
        warnMax = rez.getInteger(R.integer.warn_limit);
        errColor = rez.getColor(R.color.red);
        errMax = rez.getInteger(R.integer.err_limit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle state) {
        View v = inflater.inflate(R.layout.fragment_tweet, root, false);

        submitButton = (Button) v.findViewById(R.id.tweet_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) { post(); }
        });

        countView = (TextView) v.findViewById(R.id.tweet_count);

        tweetView = (EditText) v.findViewById(R.id.tweet_tweet);

        tweetView.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) { updateCount(); }

                @Override
                public void beforeTextChanged(CharSequence s, int b, int n, int a) { }

                @Override
                public void onTextChanged(CharSequence s, int b, int p, int n) { }
            });

        return v;
    }

    void updateCount() {
        int n = tweetView.getText().length();

        submitButton.setEnabled(checkTweetLen(n));

        n = tweetLenMax - n;

        int color;
        if (n > warnMax) { color = okColor; }
        else if (n > errMax) { color = warnColor; }
        else  { color = errColor; }

        countView.setText(String.valueOf(n));
        countView.setTextColor(color);
    }

    void post() {
        String tweet = tweetView.getText().toString();
        if (!checkTweetLen(tweet.length())) { return; }

        YambaService.post(getActivity(), tweet);

        tweetView.setText("");
    }

    private boolean checkTweetLen(int n) {
        return (errMax < n) && (tweetLenMax > n);
    }
}
