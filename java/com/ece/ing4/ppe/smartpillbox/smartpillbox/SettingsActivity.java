package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    // Progress Dialog
    private ProgressDialog pDialog;
    // User id
    private static String user_id;
    private static String user_name;
    private static String medical_staff;

    private  static TextView textContact;
    private static Switch switchMedicalStaff;
    private static Spinner spinnerNotification;
    private static Button buttonSaveUserSettings;
    private static Button buttonAdd;
    private static Button buttonDeleteContact;
    private static Button buttonSaveNewContact;
    private static GridLayout contactAddLayout;
    private static Spinner spinnerContact;
    private static EditText editContactName;
    private static EditText editContactMail;
    private static EditText editContactPhone;
    private static EditText editContactRelationship;
    private static Switch switchContactNotify;
    private static ArrayAdapter<String> contactAdapter;
    private static List<String> contactList;

    private static ArrayList<String> contactId = new ArrayList<>();
    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private EditText editConfirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // getting product details from intent
        Intent i = getIntent();
        if(i.hasExtra(MyGlobalVars.TAG_USER_ID)) {
            // getting user id from intent
            user_id = i.getStringExtra(MyGlobalVars.TAG_USER_ID);
            user_name = i.getStringExtra(MyGlobalVars.TAG_NAME);
            // getting medical from intent
            medical_staff = i.getStringExtra(MyGlobalVars.TAG_MEDICAL_STAFF);
        } else {
            // Log out
            startActivity(new Intent(this, LoginActivity.class));
        }

        editCurrentPassword = (EditText) findViewById(R.id.editCurrentPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmNewPassword = (EditText) findViewById(R.id.editConfirmNewPassword);
        switchMedicalStaff = (Switch) findViewById(R.id.switchMedicalStaff);
        textContact = (TextView) findViewById(R.id.textContact);
        spinnerNotification = (Spinner) findViewById(R.id.spinnerNotification);
        buttonAdd = (Button) findViewById(R.id.buttonAddNewContact);
        buttonDeleteContact = (Button) findViewById(R.id.buttonDeleteContact);
        buttonSaveNewContact = (Button) findViewById(R.id.buttonSaveNewContact);
        contactAddLayout = (GridLayout) findViewById(R.id.contactAddLayout);
        editContactName = (EditText) findViewById(R.id.editContactName);
        editContactMail = (EditText) findViewById(R.id.editContactMail);
        editContactPhone = (EditText) findViewById(R.id.editContactPhone);
        editContactRelationship = (EditText) findViewById(R.id.editContactRelationship);
        switchContactNotify = (Switch) findViewById(R.id.switchContactNotify);
        spinnerContact = (Spinner) findViewById(R.id.spinnerContact);
        switchMedicalStaff = (Switch) findViewById(R.id.switchMedicalStaff);

        buttonDeleteContact.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonSaveNewContact.setOnClickListener(this);

        buttonSaveUserSettings = (Button) findViewById(R.id.buttonSaveUserSettings);
        buttonSaveUserSettings.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView header_status = (TextView) header.findViewById(R.id.header_status);
        if(medical_staff.contains("1")) {
            header_status.setText(MyGlobalVars.TAG_MEDICAL_STAFF_AVG);
            hideItem();
        } else {
            header_status.setText(MyGlobalVars.TAG_PATIENT_AVG);
        }

        TextView header_name = (TextView) header.findViewById(R.id.header_name);
        header_name.setText(user_name);

        initializeSpinner();

        new GetContactTask().execute();
    }

    public void initializeSpinner(){
        contactAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{});
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContact.setAdapter(contactAdapter);

        contactList = new ArrayList<>();
        String[] data = new String[]{};
        for(String s : data) {
            addItemOnSpinnerContact(s);
        }
    }

    public void addItemOnSpinnerContact(String addItem) {
        spinnerContact= (Spinner) findViewById(R.id.spinnerContact);
        int length = spinnerContact.getAdapter().getCount();
        contactList.add(addItem);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, contactList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactAdapter = adapter;
        spinnerContact.setAdapter(contactAdapter);
    }

    public void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_patient).setVisible(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Go to home page
            Intent i = new Intent(this, SmartPillboxMainActivity.class);
            i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
            i.putExtra(MyGlobalVars.TAG_NAME, user_name);
            i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            // Go to profile page
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
            i.putExtra(MyGlobalVars.TAG_NAME, user_name);
            i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
            startActivity(i);
        } else if (id == R.id.nav_treatment) {
            // Go to treatment page
            Intent i = new Intent(this, TreatmentActivity.class);
            i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
            i.putExtra(MyGlobalVars.TAG_NAME, user_name);
            i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
            startActivity(i);
        } else if (id == R.id.nav_patient) {
            // Go to patient page
            Intent i = new Intent(this, PatientActivity.class);
            i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
            i.putExtra(MyGlobalVars.TAG_NAME, user_name);
            i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            // Go to settings page
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
            i.putExtra(MyGlobalVars.TAG_NAME, user_name);
            i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            // Log out
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNameValid(String name) {
        if(name.length() > 30) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isNumber(String weight) {
        return TextUtils.isDigitsOnly(weight);
    }

    private boolean isMailValid(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonDeleteContact) {
            new DeleteContactTask().execute();
        } else if(view.getId() == R.id.buttonAddNewContact) {
            buttonDeleteContact.setVisibility(View.GONE);
            buttonAdd.setVisibility(View.GONE);
            textContact.setVisibility(View.GONE);
            contactAddLayout.setVisibility(View.VISIBLE);
        } else if(view.getId() == R.id.buttonSaveNewContact) {
            boolean cancel = false;
            View focusView = null;
            if (TextUtils.isEmpty(editContactName.getText())) {
                editContactName.setError(getString(R.string.error_field_required));
                focusView = editContactName;
                cancel = true;
            } else if (!isNameValid(editContactName.getText().toString())) {
                editContactName.setError(getString(R.string.error_invalid_name_format));
                focusView = editContactName;
                cancel = true;
            }
            if (TextUtils.isEmpty(editContactMail.getText())) {
                editContactMail.setError(getString(R.string.error_field_required));
                focusView = editContactMail;
                cancel = true;
            } else if (!isMailValid(editContactMail.getText().toString())) {
                editContactMail.setError(getString(R.string.error_invalid_email));
                focusView = editContactMail;
                cancel = true;
            }
            if (TextUtils.isEmpty(editContactPhone.getText())) {
                editContactPhone.setError(getString(R.string.error_field_required));
                focusView = editContactPhone;
                cancel = true;
            } else if (!isNumber(editContactPhone.getText().toString())) {
                editContactPhone.setError(getString(R.string.error_invalid_phone_format));
                focusView = editContactPhone;
                cancel = true;
            }
            if (cancel) {
                // There was an error
                // form field with an error.
                focusView.requestFocus();
            } else {

                new AddContactTask().execute();
            }
        } else if (view.getId() == R.id.buttonSaveUserSettings) {
            boolean cancel = false;
            View focusView = null;
            if (!editNewPassword.getText().toString().equals(editConfirmNewPassword.getText().toString())) {
                cancel = true;
                focusView = editConfirmNewPassword;
                editConfirmNewPassword.setError(getString(R.string.error_invalid_new_password));
            } else {
                if (!TextUtils.isEmpty(editCurrentPassword.getText().toString())
                        && !isPasswordValid(editCurrentPassword.getText().toString())) {
                    editCurrentPassword.setError(getString(R.string.error_invalid_password));
                    focusView = editCurrentPassword;
                    cancel = true;
                }
                if (!TextUtils.isEmpty(editNewPassword.getText().toString())
                        && !isPasswordValid(editNewPassword.getText().toString())) {
                    editNewPassword.setError(getString(R.string.error_invalid_password));
                    focusView = editNewPassword;
                    cancel = true;
                }
                if (!TextUtils.isEmpty(editConfirmNewPassword.getText().toString())
                        && !isPasswordValid(editConfirmNewPassword.getText().toString())) {
                    editConfirmNewPassword.setError(getString(R.string.error_invalid_password));
                    focusView = editConfirmNewPassword;
                    cancel = true;
                }
            }
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                new UpdateUserDetails().execute();
            }
        }
    }

    public void refreshActivity() {
        // Go to settings page
        Intent i = new Intent(this, SmartPillboxMainActivity.class);
        i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
        i.putExtra(MyGlobalVars.TAG_NAME,user_name);
        i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
        startActivity(i);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    class UpdateUserDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private String success = "0";
        private String message = "";

        HttpURLConnection urlConnection;
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            buttonSaveUserSettings.setClickable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = updateJSON();
                jsonData = new JSONObject(data);
                this.message=jsonData.getString(MyGlobalVars.TAG_MESSAGE);
                this.success=jsonData.getString(MyGlobalVars.TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String updateJSON() {
            HttpURLConnection connection = null;
            try {
                String medic = "0";
                if(switchMedicalStaff.isChecked()) {
                    medic = "1";
                }
                String update = MyGlobalVars.url_update_user
                        + MyGlobalVars.TAG_USER_ID + "=" + user_id + "&"
                        + MyGlobalVars.TAG_OLD_PASSWORD + "=" + editCurrentPassword.getText().toString() + "&"
                        + MyGlobalVars.TAG_PASSWORD + "=" + editNewPassword.getText().toString() + "&"
                        + MyGlobalVars.TAG_MEDICAL_STAFF + "=" + medic + "&"
                        + MyGlobalVars.TAG_NOTIFICATION_TYPE + "=" + spinnerNotification.getSelectedItem().toString();
                update = update.replaceAll(" ", "%20");
                URL u = new URL(update);
                connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-length", "0");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setAllowUserInteraction(false);
                connection.setConnectTimeout(MyGlobalVars.TIMEOUT);
                connection.setReadTimeout(MyGlobalVars.TIMEOUT);
                connection.setRequestProperty("Cookie", MyGlobalVars.myCookie);
                connection.connect();
                int status = connection.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        InputStream responseStream = new BufferedInputStream(connection.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        String response = sb.toString();
                        return response;
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Void file_url) {
            if(this.success.contains("1")) {
                new UserLoginTask().execute();
            } else {
                // dismiss the dialog once got all details
                if(pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                String medic = "0";
                if(switchMedicalStaff.isChecked()) {
                    medic = "1";
                }
                buttonSaveUserSettings.setClickable(true);
                Toast.makeText(getApplicationContext(), this.message,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String success="0";
        private String medicalStaff="";
        private String name="";
        private String data="";
        JSONObject jsonData;

        @Override
        protected Boolean doInBackground(Void... params) {
            data = connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.success = jsonData.getString(MyGlobalVars.TAG_SUCCESS);
                    JSONArray users = jsonData.getJSONArray(MyGlobalVars.TAG_USER);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        this.name = user.getString(MyGlobalVars.TAG_NAME);
                        this.medicalStaff = user.getString(MyGlobalVars.TAG_MEDICAL_STAFF);
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
                user_name = this.name;
                medical_staff = this.medicalStaff;
            }
            // dismiss the dialog once got all details
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
            buttonSaveUserSettings.setClickable(true);
            refreshActivity();
        }

        public String connectJSON() {
            HttpURLConnection connection = null;
            try {
                String update = MyGlobalVars.url_connect_user
                        + MyGlobalVars.TAG_USER_ID+"="+user_id+"&"
                        + MyGlobalVars.TAG_PASSWORD+"="+editNewPassword.getText().toString();
                update = update.replaceAll(" ", "%20");
                URL u = new URL(update);
                connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-length", "0");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setAllowUserInteraction(false);
                connection.setConnectTimeout(MyGlobalVars.TIMEOUT);
                connection.setReadTimeout(MyGlobalVars.TIMEOUT);
                connection.setRequestProperty("Cookie", MyGlobalVars.myCookie);
                connection.connect();
                int status = connection.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        InputStream responseStream = new BufferedInputStream(connection.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        String response = sb.toString();
                        return response;
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
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
        private ArrayList<String> ids = new ArrayList<>();
        private ArrayList<String> names = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> relationships = new ArrayList<>();
        private ArrayList<String> notifies = new ArrayList<>();
        private String data="";
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
                    JSONArray contacts = jsonData.getJSONArray(MyGlobalVars.TAG_CONTACTS_MIN);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject contact = contacts.getJSONObject(i);
                        this.ids.add(contact.getString(MyGlobalVars.TAG_CONTACT_ID));
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
                contactId = new ArrayList<>();
                for(int i = 0; i < names.size(); i++) {
                    contactId.add(ids.get(i));
                    addItemOnSpinnerContact(names.get(i) + " "
                            + phones.get(i) + " "
                            + emails.get(i) + " "
                            + relationships.get(i) + " ");
                }
            }
            // dismiss the dialog once got all details
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AddContactTask extends AsyncTask<Void, Void, Boolean> {

        private String update;
        private String message="";
        private String success="0";
        private String contact_id;
        private String data="";
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            int notif = 0;
            if (switchContactNotify.isChecked()) notif = 1;
            update = MyGlobalVars.url_add_contact
                    + MyGlobalVars.TAG_USER_ID + "=" + user_id + "&"
                    + MyGlobalVars.TAG_NAME + "=" + editContactName.getText() + "&"
                    + MyGlobalVars.TAG_EMAIL + "=" + editContactMail.getText() + "&"
                    + MyGlobalVars.TAG_PHONE + "=" + editContactPhone.getText() + "&"
                    + MyGlobalVars.TAG_NOTIFY + "=" + notif;
            if (!editContactRelationship.getText().toString().isEmpty())
                update += "&" + MyGlobalVars.TAG_RELATIONSHIP + "=" + editContactRelationship.getText();
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
                    this.message = jsonData.getString(MyGlobalVars.TAG_MESSAGE);
                    this.contact_id = jsonData.getString(MyGlobalVars.TAG_CONTACT_ID_MIN);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(this.success.contains("1")) {
                addItemOnSpinnerContact(editContactName.getText().toString()+ " "
                        + editContactPhone.getText().toString() + " "
                        + editContactMail.getText().toString() + " "
                        + editContactRelationship.getText().toString());
                contactId.add(this.contact_id);
            }
            // dismiss the dialog once got all details
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), this.message,
                    Toast.LENGTH_LONG).show();
            buttonDeleteContact.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.VISIBLE);
            textContact.setVisibility(View.VISIBLE);
            contactAddLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DeleteContactTask extends AsyncTask<Void, Void, Boolean> {
        private int selectedContact;
        private String update;
        private String success = "0";
        private ArrayList<String> names = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> relationships = new ArrayList<>();
        private ArrayList<String> notifies = new ArrayList<>();
        private String data = "";
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            selectedContact = spinnerContact.getSelectedItemPosition();
            update = MyGlobalVars.url_delete_contact
                    + MyGlobalVars.TAG_USER_ID + "=" + user_id + "&"
                    + MyGlobalVars.TAG_CONTACT_ID + "=" + contactId.get(selectedContact);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
           data = new ConnectToDB(update).connectJSON();
            if (data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.success = jsonData.getString(MyGlobalVars.TAG_SUCCESS);
                    JSONArray contacts = jsonData.getJSONArray(MyGlobalVars.TAG_CONTACTS_MIN);
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
            if (this.success.contains("1")) {
                contactList.remove((String) spinnerContact.getSelectedItem());
                contactAdapter.notifyDataSetChanged();
                contactId.remove(selectedContact);
            }
            // dismiss the dialog once got all details
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }
}
