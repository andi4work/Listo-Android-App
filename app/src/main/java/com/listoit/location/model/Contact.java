package com.listoit.location.model;

/**
 * Created by ${ChandraMohanReddy} on 1/21/2017.
 */

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import static io.realm.log.RealmLog.trace;

public class Contact {
    ContactNumber contactNumber;
    ContactEmail contactEmail;
    String id;
    String name;
    Bitmap thumb;

    public ContactEmail getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(ContactEmail contactEmail) {
        this.contactEmail = contactEmail;
    }

    public ContactNumber getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(ContactNumber contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public JSONObject getJSONObject() {
        JSONObject objContact = new JSONObject();
        JSONObject objMoblie = new JSONObject();
        JSONObject objEmail = new JSONObject();
        try {
            objContact.put("id", id);
            objContact.put("name", name);
                objMoblie.put("mobile_no_primary", contactNumber.getPhonePrimary());
                objMoblie.put("mobile_no_secondary", contactNumber.getPhoneSecondary());
            objContact.put("mobile_number", objMoblie);
                objEmail.put("email_id_primary", contactEmail.getEmailPrimary());
                objEmail.put("email_id_secondary", contactEmail.getEmailSecondary());
            objContact.put("email_id", objEmail);

        } catch (JSONException e) {
            trace("DefaultListItem.toString JSONException: " + e.getMessage());
        }
        return objContact;
    }
}