package com.ece.ing4.ppe.smartpillbox.smartpillbox.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import com.ece.ing4.ppe.smartpillbox.smartpillbox.ConnectToDB;
import com.ece.ing4.ppe.smartpillbox.smartpillbox.MyGlobalVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kevin on 04/04/2017.
 */

public class SMSSender extends BroadcastReceiver {
    private String user_id;
    private String user_name;
    private ArrayList<String> contactPhones = new ArrayList<>();
    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> contactEmails = new ArrayList<>();
    private ArrayList<String> contactRelationships = new ArrayList<>();
    private ArrayList<String> contactNotifies = new ArrayList<>();
    private Context context;
    private String succeed = "0";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        user_id = intent.getStringExtra(MyGlobalVars.TAG_NAME);

        new UserInfoTask().execute();
        if (succeed.contentEquals("1")) {
            succeed = "0";
            new GetContactTask().execute();
        }
        if (succeed.contentEquals("1")) {
            succeed = "0";
            new UserDoseTask().execute();
        }

        if (succeed.contentEquals("1")) {
            for (int i = 0; i < contactPhones.size(); i++) {
                if (contactNotifies.get(i).contentEquals("1")) {
                    String content = "Bonjour " + contactNames.get(i)
                            + ",\nVotre assistant SmartPillbox vous renseigne sur le suivi du traitement"
                            + " de votre " + contactRelationships.get(i) + " " + user_name;
                    //TODO à compléter par le traitement pris ou non
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contactPhones.get(i), null, content, null, null);
                    //TODO send email
                }
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetContactTask extends AsyncTask<Void, Void, Boolean> {

        private String update = MyGlobalVars.url_get_contact
                + MyGlobalVars.TAG_USER_ID+"="+user_id;
        private String success="0";
        private ArrayList<String> names = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> relationships = new ArrayList<>();
        private ArrayList<String> notifies = new ArrayList<>();
        private String data="";
        JSONObject jsonData;

        @Override
        protected Boolean doInBackground(Void... params) {
            data = new ConnectToDB(update).connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.success = jsonData.getString(MyGlobalVars.TAG_SUCCESS);
                    JSONArray contacts = jsonData.getJSONArray(MyGlobalVars.TAG_CONTACTS_MIN);
                    data ="";
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject contact = contacts.getJSONObject(i);
                        this.names.add(contact.getString(MyGlobalVars.TAG_NAME));
                        this.phones.add(contact.getString(MyGlobalVars.TAG_PHONE));
                        this.emails.add(contact.getString(MyGlobalVars.TAG_EMAIL));
                        this.relationships.add(contact.getString(MyGlobalVars.TAG_RELATIONSHIP));
                        this.notifies.add(contact.getString(MyGlobalVars.TAG_NOTIFY));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(this.success.contains("1")) {
                for(int i = 0; i < phones.size(); i++) {
                    if(notifies.get(i).contains("1")) {
                        contactNames.add(names.get(i));
                        contactPhones.add(phones.get(i));
                        contactEmails.add(emails.get(i));
                        contactRelationships.add(relationships.get(i));
                        contactNotifies.add(notifies.get(i));
                    }
                }
            }
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {

        private String update;
        private String success = "0";
        private String userId = "0";
        private String userName = "";
        private String medicalStaff = "false";
        private String message = "1";
        private String data = "";
        JSONObject jsonData;


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            update = MyGlobalVars.url_get_user
                    + MyGlobalVars.TAG_USER_ID + "=" + user_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            data = new ConnectToDB(update).connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.message = jsonData.getString(MyGlobalVars.TAG_MESSAGE);
                    this.success = jsonData.getString(MyGlobalVars.TAG_SUCCESS);
                    JSONArray users = jsonData.getJSONArray(MyGlobalVars.TAG_USER);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        this.userName = user.getString(MyGlobalVars.TAG_NAME);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (this.success.contentEquals("1")) {
                succeed = "1";
                user_name = userName;
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserDoseTask extends AsyncTask<Void, Void, Boolean> {

        private String update;
        private String success = "0";
        private String userName = "";
        private String data = "";
        JSONObject jsonData;


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            update = MyGlobalVars.url_get_user
                    + MyGlobalVars.TAG_USER_ID + "=" + user_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            data = new ConnectToDB(update).connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.success = jsonData.getString(MyGlobalVars.TAG_SUCCESS);
                    JSONArray users = jsonData.getJSONArray(MyGlobalVars.TAG_USER);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        this.userName = user.getString(MyGlobalVars.TAG_NAME);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (this.success.contentEquals("1")) {
                succeed = "1";
                user_name = userName;
            }
        }
    }
}
