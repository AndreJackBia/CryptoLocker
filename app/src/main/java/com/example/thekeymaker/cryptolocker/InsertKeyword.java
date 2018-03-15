package com.example.thekeymaker.cryptolocker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class InsertKeyword extends AppCompatActivity {

    SharedPreferences prefs = null;
    private static final String KEY = "key";
    private String keyToStore;

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
        prefs = getSharedPreferences("com.example.thekeymaker.cryptolocker", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(InsertKeyword.this, R.style.AlertDialogCustom);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Keyword");
            alertDialog.setMessage("It's the first time you are running this app. Choose a keyword to encrypt/decrypt your data");
            alertDialog.setView(R.layout.dialog);
            alertDialog.setIcon(R.drawable.ic_vpn_key_black_24dp);
            alertDialog.setNeutralButton("Store",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Dialog layd = (Dialog) dialog;
                            EditText editText = (EditText) layd.findViewById(R.id.keyword);
                            keyToStore = editText.getText().toString();
                            if (!storePassword(keyToStore)) {
                                CharSequence text = "Error storing keyword";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                            } else {
                                dialog.dismiss();
                                prefs.edit().putBoolean("firstrun", false).apply();
                            }
                        }
                    });
            final AlertDialog dialog = alertDialog.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                }
            });
            dialog.show();
        }


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = mEdit.getText().toString();
                String entryDigest = Cryptos.generateDigest(s);
                String keywordDigest = readKeyword();
                if (entryDigest.equals(keywordDigest)) {
                    Intent intent = new Intent(InsertKeyword.this, ShowData.class);
                    intent.putExtra("key", s);
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

    private String readKeyword() {
        FileInputStream fis;
        try {
            fis = openFileInput(KEY);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean storePassword(String keyword) {
        try {
            FileOutputStream outputStream;
            outputStream = openFileOutput(KEY, Context.MODE_PRIVATE);
            outputStream.write(Cryptos.generateDigest(keyword).getBytes());
            outputStream.close();
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
