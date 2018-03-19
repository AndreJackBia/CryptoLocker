package com.example.thekeymaker.cryptolocker;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import static com.example.thekeymaker.cryptolocker.Cryptos.decrypt;

public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder> {
    private static final String PASSWD = "passwd";
    private Context context;
    private List<Website> contactList;

    public WebsiteAdapter(Context context, List<Website> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    public void remove(int position, String key) {
        Website item = contactList.get(position);
        if (contactList.contains(item)) {
            contactList.remove(position);
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
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(WebsiteViewHolder websiteViewHolder, int i) {
        Website ci = contactList.get(i);
        websiteViewHolder.vName.setText(ci.getName());
        websiteViewHolder.vUID.setText(ci.getuID());
        websiteViewHolder.vPsw.setText(ci.getPsw());
    }

    @Override
    public WebsiteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element, viewGroup, false);
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