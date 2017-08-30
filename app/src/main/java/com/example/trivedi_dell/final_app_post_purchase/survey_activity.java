package com.example.trivedi_dell.final_app_post_purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class survey_activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_activity);

        Button button = (Button) findViewById(R.id.button2);
        ImageView img = (ImageView) findViewById(R.id.img2);
        TextView txt = (TextView) findViewById(R.id.txt);
        TextView txt2 = (TextView) findViewById(R.id.txt2);
        final Spinner survey = (Spinner) findViewById(R.id.spinner);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(survey_activity.this, Welcome_page.class));
                finish();
            }
        });

        List<String> options = new ArrayList<String>();
        options.add("Male");
        options.add("Female");
        options.add("Non-binary");


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        survey.setAdapter(spinnerAdapter);

        // Posts runnable callback to UI thread's message queue (to be implemented on its turn),
        // used to fix the issue where listener's callback is automatically called upon listener's
        // instantiation with spinner's "default" option selected (this is clearly unwanted behavior)
        survey.post(new Runnable() {
            @Override
            public void run() {
                survey.setOnItemSelectedListener(new MyOnItemSelectedListener());
            }
        });
    }

    // Implementing callbacks for Spinner (dropdown item selection via user click)
    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selection = (String) parent.getItemAtPosition(pos);

            // Using spinner selection (survery selection) to update profile parameters
            // on Target server using Target call with (empty) callback, as no offer is expected
            Map<String, Object> targetParamsUpdate = new HashMap<String, Object>();
            targetParamsUpdate.put("profile.gender", selection);
            targetParamsUpdate.put("mbox3rdPartyId","visitor_1");

            TargetLocationRequest locReqforProfileUpdate = Target.createRequest("dummy-mbox-for-prof-update", "default content",
                    targetParamsUpdate);
            Target.loadRequest(locReqforProfileUpdate, new Target.TargetCallback<String>() {
                @Override
                public void call(final String content) {
                }
            });
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // For handling things you needn't worry about too much for now
        }
    }

}
