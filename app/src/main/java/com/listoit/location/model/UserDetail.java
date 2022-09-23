package com.listoit.location.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ${ChandraMohanReddy} on 1/25/2017.
 */

public class UserDetail extends RealmObject {
    @PrimaryKey
    private long id;
    private String user_oid;
    private String user_phone;
    private String user_lat;
    private String user_longi;
    private String user_reg_datetime;
    private String user_verified_status;
    private String user_login_status;
    private String user_status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_oid() {
        return user_oid;
    }

    public void setUser_oid(String user_oid) {
        this.user_oid = user_oid;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_longi() {
        return user_longi;
    }

    public void setUser_longi(String user_longi) {
        this.user_longi = user_longi;
    }

    public String getUser_reg_datetime() {
        return user_reg_datetime;
    }

    public void setUser_reg_datetime(String user_reg_datetime) {
        this.user_reg_datetime = user_reg_datetime;
    }

    public String getUser_verified_status() {
        return user_verified_status;
    }

    public void setUser_verified_status(String user_verified_status) {
        this.user_verified_status = user_verified_status;
    }

    public String getUser_login_status() {
        return user_login_status;
    }

    public String setUser_login_status(String user_login_status) {
        this.user_login_status = user_login_status;
        return user_login_status;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
