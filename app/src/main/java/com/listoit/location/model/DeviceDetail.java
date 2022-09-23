package com.listoit.location.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ${ChandraMohanReddy} on 1/25/2017.
 */

public class DeviceDetail extends RealmObject {
    @PrimaryKey
    private long id;
    private String device_oid;

    private String reg_email_id;

    private String gcm_code;

    private String reg_datatime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDevice_oid() {
        return device_oid;
    }

    public void setDevice_oid(String device_oid) {
        this.device_oid = device_oid;
    }

    public String getReg_email_id() {
        return reg_email_id;
    }

    public void setReg_email_id(String reg_email_id) {
        this.reg_email_id = reg_email_id;
    }

    public String getGcm_code() {
        return gcm_code;
    }

    public void setGcm_code(String gcm_code) {
        this.gcm_code = gcm_code;
    }

    public String getReg_datatime() {
        return reg_datatime;
    }

    public void setReg_datatime(String reg_datatime) {
        this.reg_datatime = reg_datatime;
    }
}
