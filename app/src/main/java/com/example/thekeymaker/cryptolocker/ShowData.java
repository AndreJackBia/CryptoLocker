package com.example.thekeymaker.cryptolocker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.thekeymaker.cryptolocker.Cryptos.decrypt;

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
        final RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final WebsiteAdapter adapter = (WebsiteAdapter) recList.getAdapter();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                int swipedPosition = viewHolder.getAdapterPosition();
                                adapter.remove(swipedPosition);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowData.this);
                builder.setCancelable(false)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        final String key = getIntent().getStringExtra("key");
        //deleteFile(PASSWD);
        List set = decryptData(key);
        if (set == null) {
            set = new ArrayList();
        }
        recList.setAdapter(new WebsiteAdapter(this, set));
        itemTouchHelper.attachToRecyclerView(recList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowData.this, InsertEntry.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
            }
        });
    }

    private List decryptData(String key) {
        List<Website> res = new ArrayList<>();
        FileInputStream fis;
        try {
            fis = openFileInput(PASSWD);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray json = new JSONArray(sb.toString());
            JSONObject obj;
            for (int i = 0; i < json.length(); i++) {
                obj = json.getJSONObject(i);
                String name = obj.getString("name");
                String uID = obj.getString("uID");
                String psw = obj.getString("psw");
                res.add(new Website(decrypt(name, key), decrypt(uID, key), decrypt(psw, key)));
            }
            return res;
        } catch (JSONException e) {
            Log.e("JSON Exception", "Malformed JSON array");
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "the file doens't exist");
        } catch (IOException e) {
            Log.e("IO Exception", "An error occured while reading file");
        }
        return null;
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
