package com.example.biagg.cryptolocker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by biagg on 11/06/2017.
 */

public class InsertKeyword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(InsertKeyword.this, ShowData.class));
                finish();
            }
        });
    }
}
