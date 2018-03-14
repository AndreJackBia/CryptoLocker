package com.example.biagg.cryptolocker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class InsertEntry extends AppCompatActivity {

    EditText wbs;
    EditText uid;
    EditText psw;
    private static final String PASSWD = "passwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_entry);
        wbs = (EditText) findViewById(R.id.wbs);
        uid = (EditText) findViewById(R.id.userid);
        psw = (EditText) findViewById(R.id.passwd);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        final Button button = (Button) findViewById(R.id.store);
        psw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                final Website w = new Website(wbs.getText().toString(), uid.getText().toString(), psw.getText().toString());
                AlertDialog alertDialog = new AlertDialog.Builder(InsertEntry.this).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (writeEntry(w)) {
                                    Intent intent = new Intent(InsertEntry.this, ShowData.class);
                                    intent.putExtra("key", getIntent().getStringExtra("key"));
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private boolean writeEntry(Website w) {
        JSONArray data = getJsonArray();
        JSONObject obj = new JSONObject();
        try {
            String temp = encrypt(w.getName(),getIntent().getStringExtra("key"));
            if (temp == null)
                return false;
            obj.put("name", temp);
            temp = encrypt(w.getuID(),getIntent().getStringExtra("key"));
            if (temp == null)
                return false;
            obj.put("uID", temp);
            temp = encrypt(w.getPsw(),getIntent().getStringExtra("key"));
            if (temp == null)
                return false;
            obj.put("psw", encrypt(w.getPsw(),getIntent().getStringExtra("key")));
            data.put(obj);
        } catch (JSONException e) {
            Log.e("JSONExeption", "error creating JSONObj");
        }
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(PASSWD, Context.MODE_PRIVATE);
            outputStream.write(data.toString().getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("Missing file", "File not found");
            return false;
        } catch (IOException e) {
            Log.e("IOException", "Error while writing");
            return false;
        }
        return true;
    }

    private String encrypt(String psw, String keyword) {
        String result = null;
        try {
            byte[] key = Arrays.copyOf(keyword.getBytes("UTF-8"), 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(psw.getBytes());
            result = Base64.encodeToString(encrypted, Base64.DEFAULT);

            /*
            * Old encryption
            byte[] key = keyword.getBytes("UTF-8");
            MessageDigest sha;
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(psw.getBytes());
            result = Base64.encodeToString(encrypted, Base64.DEFAULT);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private JSONArray getJsonArray() {
        FileInputStream fis;
        try {
            fis = openFileInput(PASSWD);
        } catch (FileNotFoundException e) {
            return new JSONArray();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Log.e("IO Exception", "An error occured while reading file");
        }
        JSONArray res = null;
        try {
            res = new JSONArray(sb.toString());
        } catch (JSONException e) {
            Log.e("JSON Exception", "Failed to read");
        }
        return res;
    }
}
