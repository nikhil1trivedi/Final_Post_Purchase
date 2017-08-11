package com.example.trivedi_dell.final_app_post_purchase;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.adobe.mobile.Config;
import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;
import java.util.Map;

public class ap_activity extends YouTubeBaseActivity {
// lets do three main components (ImageView) (Article) (Video?) all pertaining to a product
// this will integrate automated personalization
    //based on the survey or the interraction of Alexa -
    //ensure that the first step is to have the survey done

    private static final int Recovery_request = 1;
    private  YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private String Video_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ap_activity);

        Config.setContext(this.getApplicationContext());
        Config.setDebugLogging(true);
        Config.collectLifecycleData(this);

        // find out how you will be able to automate the Video_ID (thats gonna be based on the profiles)
        final String API_KEY = "AIzaSyCl6RUdeDw6FzulI9oS1c-tPFbavEZrf9c" ;
        final String video_ID = "v_i3Lcjli84" ; //this is what will interact with the adobe server!!

        //create hashmap of params
         Map<String, Object> video_params= new HashMap<String, Object>();
        video_params.put("soccer","profile.interests");


        // create a location request and load the other video strings

        TargetLocationRequest locationRequest_video_id= Target.createRequest("video_url",video_ID, video_params);
        Target.loadRequest(locationRequest_video_id, new Target.TargetCallback<String>() {
            @Override
            public void call(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       final String Video_ID = s ;
                        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view) ;
                        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,final boolean b) {
                                youTubePlayer.loadVideo(Video_ID);
                            }
                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                            }
                        };


                        Button play_button = (Button) findViewById(R.id.play_button) ;
                        play_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                youTubePlayerView.initialize(API_KEY,onInitializedListener);
                            }
                        });
                    }
                });

            }
        });



        //create Image parameters
        final Map<String,Object> image_params = new HashMap<String, Object>();
        image_params.put("woman" , "profile.gender");
        image_params.put("male","profile.gender");
        image_params.put("football","profile.interest");
        image_params.put("basketball","profile.interest");
        image_params.put("fashion","profile.interest");


        //recognize the image holder 1
        final ImageView image_holder1 = (ImageView) findViewById(R.id.image_holder1) ;
        //create location request for the two imageViews
        final TargetLocationRequest image_request = Target.createRequest("image-1" , "default.png",image_params );
        Target.loadRequest(image_request, new Target.TargetCallback<String>() {
            @Override
            public void call(String image_url) {
                final RequestCreator rq = Picasso.with(getApplicationContext()).load(image_url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rq.into(image_holder1);
                    }
                });
        final ImageView image_holder2 = (ImageView)  findViewById(R.id.image_holder2);
                TargetLocationRequest image_request2 = Target.createRequest("image-2" , "default.png", image_params);
                Target.loadRequest(image_request2, new Target.TargetCallback<String>() {
                    @Override
                    public void call(String image_url2) {
                        final RequestCreator rq2 = Picasso.with(getApplicationContext()).load(image_url2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rq2.into(image_holder2);
                            }
                        });

                    }
                });

                    }
                });
            }
        }




