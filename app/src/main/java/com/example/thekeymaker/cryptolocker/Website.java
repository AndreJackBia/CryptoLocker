package com.example.biagg.cryptolocker;

/**
 * Created by biagg on 10/06/2017.
 */

public class Website {

    private String name;
    private String uID;
    private String psw;

    public Website (String name, String uID, String psw) {
        this.name = name;
        this.uID = uID;
        this.psw = psw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }
}
