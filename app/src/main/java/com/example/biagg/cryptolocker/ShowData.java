package com.example.biagg.cryptolocker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShowData extends AppCompatActivity {

    private static final String PASSWD = "passwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        final String key = getIntent().getStringExtra("key");

        Website w = new Website("Slack", "pippo", "pluto");
        JSONArray json = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", w.getName());
            obj.put("uID", w.getuID());
            obj.put("psw", w.getPsw());
            json.put(obj);
        } catch (JSONException e) {
            Log.e("JSONExeption", "error creating JSONObj");
        }
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(PASSWD, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("Sti cazzi", "non c'è il file");
        }
        List<Website> set = decryptData(key);
        recList.setAdapter(new WebsiteAdapter(set));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private List decryptData(String key) {
        List<Website> res = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = openFileInput(PASSWD);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFound Exception", "The file does not exist");
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
        JSONArray json;
        JSONObject obj;
        try {
            json = new JSONArray(sb.toString());
            for (int i = 0; i < json.length(); i++) {
                obj = json.getJSONObject(i);
                res.add(new Website(obj.getString("name"), obj.getString("uID"), obj.getString("psw")));
            }
        } catch (JSONException e) {
            Log.e("JSON Exception", "Malformed JSON array");
        }
        return res;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
