package com.example.trivedi_dell.final_app_post_purchase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class about_us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Button subscribe_button = (Button) findViewById(R.id.subscribe_button);
        Button home_button = (Button) findViewById(R.id.home_button);


        subscribe_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(about_us.this , subsciption_success.class));
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(about_us.this , Welcome_page.class));
                }
        });
    }
}
