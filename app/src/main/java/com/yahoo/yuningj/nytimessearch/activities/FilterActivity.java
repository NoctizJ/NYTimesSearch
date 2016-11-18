package com.yahoo.yuningj.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.yahoo.yuningj.nytimessearch.R;

public class FilterActivity extends AppCompatActivity {

    EditText etBeginDate;
    EditText etEndDate;
    RadioButton rbNewest;
    RadioButton rbOldest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Set Filter");

        etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);

        rbNewest = (RadioButton) findViewById(R.id.rbNewest);
        rbOldest = (RadioButton) findViewById(R.id.rbOldest);

        String beginDate = getIntent().getStringExtra("beginDate");
        String endDate = getIntent().getStringExtra("endDate");
        String sortOrder = getIntent().getStringExtra("sortOrder");

        etBeginDate.setText(beginDate);
        etEndDate.setText(endDate);

        if (sortOrder.equals("newest")) {
            rbNewest.setChecked(true);
            rbOldest.setChecked(false);
        } else if (sortOrder.equals("oldest")) {
            rbNewest.setChecked(false);
            rbOldest.setChecked(true);
        } else {
            rbNewest.setChecked(false);
            rbOldest.setChecked(false);
        }
    }


    public void onApplyFilter(View view) {
        Intent data = new Intent();
        data.putExtra("beginDate", etBeginDate.getText().toString());
        data.putExtra("endDate", etEndDate.getText().toString());

        if (rbNewest.isChecked()) {
            data.putExtra("sortOrder", "newest");
        } else if (rbOldest.isChecked()) {
            data.putExtra("sortOrder", "oldest");
        } else {
            data.putExtra("sortOrder", "");
        }

        setResult(RESULT_OK, data);
        finish();
    }

    public void onClickOldest(View view) {
        if (rbOldest.isChecked()) {
            rbOldest.setChecked(true);
            rbNewest.setChecked(false);
        } else {
            rbOldest.setChecked(false);
        }
    }

    public void onClickNewest(View view) {
        if (rbNewest.isChecked()) {
            rbNewest.setChecked(true);
            rbOldest.setChecked(false);
        } else {
            rbNewest.setChecked(false);
        }
    }
}
