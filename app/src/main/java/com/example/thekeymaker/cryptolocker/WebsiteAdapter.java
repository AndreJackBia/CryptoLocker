package com.example.thekeymaker.cryptolocker;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.example.thekeymaker.cryptolocker.Cryptos.decrypt;

public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder> {
    private static final String PASSWD = "passwd";
    private Context context;
    private List<Website> websites;

    public WebsiteAdapter(Context context, List<Website> websites) {
        this.websites = websites;
        this.context = context;
    }

    public void remove(int position, String key) {
        Website item = websites.get(position);
        if (websites.contains(item)) {
            websites.remove(position);
            notifyItemRemoved(position);
            deleteItemFromFile(item, key);
            //TODO chiamata a BackupManager dataChanged();
        }
    }

    private void deleteItemFromFile(Website item, String key) {
        FileInputStream fis;
        try {
            fis = context.openFileInput(PASSWD);
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
                if (item.getName().equals(decrypt(obj.getString("name"), key))
                        && item.getuID().equals(decrypt(obj.getString("uID"), key))
                        && item.getPsw().equals(decrypt(obj.getString("psw"), key))) {
                    json.remove(i);
                }
            }
            context.deleteFile(PASSWD);
            FileOutputStream outputStream;

            outputStream = context.openFileOutput(PASSWD, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Impossible to delete item");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (IOException e) {
            Log.e("IO Exception", "An error occured while reading file");
        } catch (JSONException e) {
            Log.e("JSON Exception", "Malformed JSON array");
        }
    }

    @Override
    public int getItemCount() {
        return websites.size();
    }

    @Override
    public void onBindViewHolder(WebsiteViewHolder websiteViewHolder, int i) {
        Website ci = websites.get(i);
        websiteViewHolder.vName.setText(ci.getName());
        websiteViewHolder.vUID.setText(ci.getuID());
        websiteViewHolder.vPsw.setText(ci.getPsw());

        websiteViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TextView tv = (TextView) view.findViewById(R.id.psw);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tv.setTransformationMethod(null);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tv.setTransformationMethod(new PasswordTransformationMethod());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public WebsiteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element, viewGroup, false);
        TextView tv = (TextView) itemView.findViewById(R.id.psw);
        tv.setTransformationMethod(new PasswordTransformationMethod());
        return new WebsiteViewHolder(itemView);
    }

    public static class WebsiteViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vUID;
        protected TextView vPsw;

        public WebsiteViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.website);
            vUID = (TextView) v.findViewById(R.id.uid);
            vPsw = (TextView) v.findViewById(R.id.psw);
        }
    }
}