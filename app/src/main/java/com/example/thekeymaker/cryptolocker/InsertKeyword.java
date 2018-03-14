package com.example.thekeymaker.cryptolocker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by biagg on 11/06/2017.
 */

public class InsertKeyword extends AppCompatActivity {

    private static final String CHECK = "thekeymaker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        View view = this.getWindow().getDecorView();
        final Button button = (Button) findViewById(R.id.button);
        final EditText mEdit = (EditText) findViewById(R.id.editText);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    button.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CHECK.equals(mEdit.getText().toString())) {
                    Intent intent = new Intent(InsertKeyword.this, ShowData.class);
                    intent.putExtra("key", CHECK);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(InsertKeyword.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Wrong keyword, try again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mEdit.setText("");
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }
}
