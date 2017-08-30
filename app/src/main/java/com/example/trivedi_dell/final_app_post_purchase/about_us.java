package com.example.trivedi_dell.final_app_post_purchase;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adobe.mobile.Config;
import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class about_us extends AppCompatActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        //config files for target
        Config.setDebugLogging(true);
        Config.setContext(this.getApplicationContext());
        Config.collectLifecycleData(this);

        //hashmap of new parameters
        //this is an A/B test activity so the only parameters that matter is the gender
        final Map<String, Object> text_params_about = new HashMap<String , Object>();
        text_params_about.put("profile.gender" ,"female" );

        Button subscribe_button = (Button) findViewById(R.id.subscribe_button);
        Button home_button = (Button) findViewById(R.id.home_button);

        TargetLocationRequest sub_button_success = Target.createRequest("subscrip-success" , "default" ,text_params_about);

        //identify the success metric
        // navigate to the subscription success page
        subscribe_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(about_us.this , subsciption_success.class));
                Target.createOrderConfirmRequest("subscrip-success","a2","1","abcd",text_params_about);
            }
        });

        //navigate to the home button
        home_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(about_us.this , Welcome_page.class));
                }
        });




        final TextView textView_content = (TextView) findViewById(R.id.content_text);


        //set the target location request for the content
        TargetLocationRequest text_content_location = Target.createRequest("textContent" , "We are PP", text_params_about );
        Target.loadRequest(text_content_location, new Target.TargetCallback<String>() {
            @Override
            public void call(final String about_content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView_content.setText(about_content);
                    }
                });

            }
        });

        final TextView title_string = (TextView) findViewById(R.id.title_text);

        // set the target location request for the title of the page
        TargetLocationRequest title_location = Target.createRequest("titleRequest","Post Purchase",text_params_about);
        Target.loadRequest(title_location, new Target.TargetCallback<String>() {
            @Override
            public void call(final String new_title) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title_string.setText(new_title);
                    }
                });
            }
        });










    }


}
