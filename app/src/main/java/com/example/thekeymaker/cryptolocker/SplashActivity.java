package com.example.thekeymaker.cryptolocker;

/**
 * Created by biagg on 11/06/2017.
 */

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, InsertKeyword.class));
        finish();
    }

}