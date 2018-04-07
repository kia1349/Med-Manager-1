package com.nwagu.medmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NewMedActivity extends AppCompatActivity {

    TextView medNameText, medDescText, medFreqText, medStartText, medEndText;
    EditText medNameEdit, medDescEdit;
    Button btnSetStart, btnSetEnd, btnAddMed;
    Spinner medFreqSpinner;

    private int pHour, pMinute; // for present time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_med);
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        medNameText = (TextView) findViewById(R.id.med_name_text);
        medDescText = (TextView) findViewById(R.id.med_desc_text);
        medFreqText = (TextView) findViewById(R.id.med_freq_text);
        medStartText = (TextView) findViewById(R.id.med_start_text);
        medEndText = (TextView) findViewById(R.id.med_end_text);
        medNameEdit = (EditText) findViewById(R.id.med_name_edit);
        medDescEdit = (EditText) findViewById(R.id.med_desc_edit);
        medFreqSpinner = (Spinner) findViewById(R.id.freq_spinner);
        btnSetStart = (Button) findViewById(R.id.btn_set_start);
        btnSetEnd = (Button) findViewById(R.id.btn_set_end);
        btnAddMed = (Button) findViewById(R.id.btn_add_med);

        btnSetStart.setOnClickListener(dateSetClickListener);
        btnSetEnd.setOnClickListener(dateSetClickListener);

        btnAddMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((medNameEdit.getText().toString().length() <= 0) ||
                        (medStartText.getText().toString().length() <= 0) ||
                        (medStartText.getText().toString().equals(getResources().getString(R.string.med_start)))) {
                    Toast.makeText(getApplicationContext(), "Some necessary fields are empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_MED_NAME, medNameEdit.getText().toString());
                intent.putExtra(Constants.EXTRA_MED_DESC, medDescEdit.getText().toString());
                intent.putExtra(Constants.EXTRA_MED_FREQ, String.valueOf(medFreqSpinner.getSelectedItem()));
                intent.putExtra(Constants.EXTRA_MED_START, medStartText.getText().toString());
                intent.putExtra(Constants.EXTRA_MED_END, medEndText.getText().toString());

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    View.OnClickListener dateSetClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int pYear = c.get(Calendar.YEAR);
            int pMonth = c.get(Calendar.MONTH);
            int pDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(NewMedActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final String dateString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                            // Get Current Tim
                            final Calendar c = Calendar.getInstance();
                            pHour = c.get(Calendar.HOUR_OF_DAY);
                            pMinute = c.get(Calendar.MINUTE);

                            //Then launch TimePickerDialog
                            TimePickerDialog timePickerDialog = new TimePickerDialog(NewMedActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            String timeDateString = dateString + " " + hourOfDay + ":" + minute;

                                            if (v == btnSetStart) {
                                                medStartText.setText(timeDateString);
                                            } else {
                                                medEndText.setText(timeDateString);
                                            }

                                        }

                                    }, pHour, pMinute, false);
                            timePickerDialog.show();

                        }

                    }, pYear, pMonth, pDay);
            datePickerDialog.show();

        }
    };

}
