package com.example.tweetAndDraw;


import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class TwitPicActivity extends Activity {

    /**
     * Generate these in your twitter api settings page..
     */
    public static final String CONSUMER_KEY = "redacted";
    public static final String CONSUMER_SECRET = "redacted";
    public static final String ACCESS_TOKEN = "redacted";
    public static final String ACCESS_TOKEN_SECRET = "redacted";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button saveButton = (Button) findViewById(R.id.myButton);
        Button resetButton = (Button) findViewById(R.id.resetImage);

        final GestureOverlayView gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestureOverlay);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureOverlayView.clear(true);
                gestureOverlayView.cancelClearAnimation();
                gestureOverlayView.refreshDrawableState();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureOverlayView.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(gestureOverlayView.getDrawingCache());
                gestureOverlayView.setDrawingCacheEnabled(false);
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    File f = new File(getFilesDir() + File.separator + "tweet.jpg");
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    postToTwitter(f);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void postToTwitter(final File file) {

        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {

                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
                configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
                configurationBuilder.setOAuthAccessToken(ACCESS_TOKEN);
                configurationBuilder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
                configurationBuilder.setUseSSL(true);
                Configuration configuration = configurationBuilder.build();
                Twitter instance = new TwitterFactory(configuration).getInstance();

                StatusUpdate update = new StatusUpdate("FOO!");
                update.setMedia(file);

                try {
                    instance.updateStatus(update);
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return null;
            }
        }.execute();
    }

}
