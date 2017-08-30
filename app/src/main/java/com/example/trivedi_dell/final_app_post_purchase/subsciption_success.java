package com.example.trivedi_dell.final_app_post_purchase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class subsciption_success extends AppCompatActivity {

    // this is the final destination of the app, the success page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsciption_success);

        Button back_to_main= (Button) findViewById(R.id.back_to_main_button);


        // set the destination of the button to the opening screen
        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(subsciption_success.this, Welcome_page.class));
            }
        });


    }
}
