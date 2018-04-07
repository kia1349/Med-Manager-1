package com.nwagu.medmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    TextView nameTextView, descTextView, freqTextView, startTextView, endTextView, lastTextView;

    Button btnMedDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        final int index = i.getIntExtra(Constants.EXTRA_MED_INDEX, 1);
        final String medName = i.getStringExtra(Constants.EXTRA_MED_NAME);
        String medDesc = i.getStringExtra(Constants.EXTRA_MED_DESC);
        String medFreq = i.getStringExtra(Constants.EXTRA_MED_FREQ);
        String medStart = i.getStringExtra(Constants.EXTRA_MED_START);
        String medEnd = i.getStringExtra(Constants.EXTRA_MED_END);
        String medLast = i.getStringExtra(Constants.EXTRA_MED_LAST);

        nameTextView = (TextView) findViewById(R.id.name_text);
        descTextView = (TextView) findViewById(R.id.desc_text);
        freqTextView = (TextView) findViewById(R.id.freq_text);
        startTextView = (TextView) findViewById(R.id.start_text);
        endTextView = (TextView) findViewById(R.id.end_text);
        lastTextView = (TextView) findViewById(R.id.last_text);

        btnMedDelete = (Button) findViewById(R.id.btn_med_delete);
        btnMedDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailsActivity.this)
                        //.setIcon(R.drawable.delete)
                        .setTitle("Delete")
                        .setMessage("Delete " + medName + " from active list?")
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface abc, int def) {
                                        Intent intent = new Intent();
                                        intent.putExtra(Constants.EXTRA_MED_INDEX, index);

                                        // Set result and finish this activity
                                        setResult(Constants.RESULT_MED_DELETE, intent);
                                        finish();
                                    }

                                })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface abc, int def) {

                            }

                        }).show();

            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_med_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_MED_INDEX, index);

                // Set result and finish this activity
                setResult(Constants.RESULT_MED_TAKE, intent);
                finish();
            }
        });

        nameTextView.setText("Name: " + medName);
        descTextView.setText("Description: " + medDesc);
        freqTextView.setText("Frequency: " + medFreq + " hourly");
        startTextView.setText("Start date: " + medStart);
        endTextView.setText("End date: " + medEnd);
        lastTextView.setText("Drug was last taken at: " + medLast);

    }
}
