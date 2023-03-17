package com.example.team_32;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class UsernameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
    }

    public void onUserNameSaveBtnClicked(View view) {
        EditText usrName = findViewById(R.id.userNameEntry);
        String name= usrName.getText().toString();
        Log.d("nameActivity", String.valueOf(name.isEmpty()));
        if (usrName.getText().toString().isEmpty()){
            Log.d("nameActivity", "onUserNameSaveBtnClicked: but no name is given");

        }else {
            Log.d("nameActivity", "onUserNameSaveBtnClicked: ");
            Intent resultData = new Intent();
            resultData.putExtra("result_key", usrName.getText().toString());
            setResult(RESULT_OK, resultData);
            finish();
        }
    }
}