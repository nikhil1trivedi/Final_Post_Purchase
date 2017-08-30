package com.example.trivedi_dell.final_app_post_purchase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adobe.mobile.Config;
import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ap_activity extends YouTubeBaseActivity  {
// lets do three main components (ImageView) (Article) (Video?) all pertaining to a product
// this will integrate automated personalization
    //based on the survey or the interraction of Alexa -
    //ensure that the first step is to have the survey done


    private static final int Recovery_request = 1;
    private  YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private String Video_ID;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ap_activity);





        //set config files
        Config.setContext(this.getApplicationContext());
        Config.setDebugLogging(true);
        Config.collectLifecycleData(this);

        //  automate the Video_ID (thats gonna be based on the profiles)
        //done through a target call
        final String API_KEY = "AIzaSyCl6RUdeDw6FzulI9oS1c-tPFbavEZrf9c" ;
        final String video_ID = "v_i3Lcjli84" ; //this is what will interact with the adobe server!!

        //create hashmap of params
        /*possible parameters
        product-Apple , Beats, Polo
        interests - football,basketball,soccer,fashion,hip hop
        gender - male female
        season- fall spring winter summer (mainly for Polo)

        Beats parameters
            - interests
            - gender
        Apple parameters
            - type (new products, featured Products, Steve Jobs)
            -device (iPhone, MacBook, iMac, Apple Watch)
        Polo
            - season (fall spring winter summer )
            -gender



         */
        String key;
        String object ;

         final Map<String, Object> visitor_profile= new HashMap<String, Object>();
        visitor_profile.put("profile.product","Beats");
        visitor_profile.put("profile.interests","soccer");
        visitor_profile.put("profile.gender" ,"female");
        visitor_profile.put("profile.season","fall");
        visitor_profile.put("mbox3rdPartyId","visitor_1");

        //check if you even need to make these because it might be easier to just use one parameter map


        // create a location request and load the other video strings

        TargetLocationRequest locationRequest_video_id= Target.createRequest("video_url",video_ID, visitor_profile);
        Target.loadRequest(locationRequest_video_id, new Target.TargetCallback<String>() {
            @Override
            public void call(final String s) {
                //run on seperate thread to decrease chance of crashing
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

                        // create the button that allows the youtube video play
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










        //recognize the image holder 1
        final ImageView image_holder1 = (ImageView) findViewById(R.id.image_holder1) ;
        //create location request for the two imageViews





        //target request to change the image
        final TargetLocationRequest image_request = Target.createRequest("image_1" , "default.png",visitor_profile );
        Target.loadRequest(image_request, new Target.TargetCallback<String>() {
            @Override
            public void call(final String image_url) {
               final RequestCreator rq = Picasso.with(getApplicationContext()).load(image_url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rq.into(image_holder1);






                    }
                });


                //do the same for the second image holder
                final ImageView image_holder2 = (ImageView)  findViewById(R.id.image_holder2);
                TargetLocationRequest image_request2 = Target.createRequest("image_2" , "default.png", visitor_profile);
                Target.loadRequest(image_request2, new Target.TargetCallback<String>() {
                    @Override
                    public void call(final String image_url2) {
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



//create location for the textView
        final TextView ap_text = (TextView) findViewById(R.id.ap_text);

        TargetLocationRequest text_request = Target.createRequest("text_1","Welcome", visitor_profile);
        Target.loadRequest(text_request, new Target.TargetCallback<String>() {
            @Override
            public void call(final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ap_text.setText(text);
                    }
                });

            }
        });

        Button home_button= (Button) findViewById(R.id.back_home_button);
        Button success = (Button) findViewById(R.id.subscribe_sucess_metric);

        TargetLocationRequest success_met = Target.createRequest("success_met" , "default",visitor_profile);

        home_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ap_activity.this , Welcome_page.class));
            }
        });

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ap_activity.this ,subsciption_success.class ));
                Target.createOrderConfirmRequest("button-clicked","a1","1" ,"abc",visitor_profile);
            }
        });

        //this is the scroll image project not yet finished
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

    }


    }










