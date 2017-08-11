package com.example.trivedi_dell.final_app_post_purchase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Welcome_page extends AppCompatActivity{
    //integration of Geolocation to change the picture
    //potential for several pushbuttons navigating to subscription activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome_page);

        Button welcome_button = (Button) findViewById(R.id.automated_personalization);
        final Button about_us = (Button) findViewById(R.id.about_us_button) ;
        Button survey_button = (Button) findViewById(R.id.survey_button);


         welcome_button.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View view) {
                startActivity(new Intent(Welcome_page.this, ap_activity.class));
            }



        });


        about_us.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Welcome_page.this , about_us.class));
            }
        });

        
    }
}

