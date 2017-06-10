package com.example.biagg.cryptolocker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder> {
    protected TextView vWebsite;
    protected TextView vUID;
    protected TextView vPsw;

    private List<Website> contactList;

    public WebsiteAdapter(List<Website> contactList) {
        this.contactList = contactList;
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
            vName =  (TextView) v.findViewById(R.id.website);
            vUID = (TextView)  v.findViewById(R.id.uid);
            vPsw = (TextView)  v.findViewById(R.id.psw);
        }
    }
}