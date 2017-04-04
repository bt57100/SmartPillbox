package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static EditText editName;
    private static EditText editBirth;
    private static EditText editHeight;
    private static EditText editWeight;
    private static EditText editMail;
    private static EditText editPhone;
    private static EditText editDoctor;
    private static EditText editPharmacist;
    private static Button buttonSave;
    private static Spinner spinnerUserContact;
    private static ArrayAdapter<String> contactAdapter;
    private static List<String> contactList;
    private Thread updateThread;

    // Progress Dialog
    private ProgressDialog pDialog;
    // User id
    private static String user_id;
    private static String user_name;
    private static String patient_id;
    private static String id_to_get;
    private static String medical_staff;

    private static TextView header_name;

    // Creating JSON Parser object
    ArrayList<HashMap<String, String>> userList;
    JSONArray users = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
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
        // getting user id from intent
        if(i.hasExtra(MyGlobalVars.TAG_PATIENT_ID)) {
            patient_id = i.getStringExtra(MyGlobalVars.TAG_PATIENT_ID);
            id_to_get = patient_id;
        } else {
            id_to_get = user_id;
        }

        // Getting complete product details in background thread
        new GetUserDetails().execute();

        editName = (EditText) findViewById(R.id.editName);
        editBirth = (EditText) findViewById(R.id.editBirth);
        editHeight = (EditText) findViewById(R.id.editHeight);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editMail = (EditText) findViewById(R.id.editMail);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editDoctor = (EditText) findViewById(R.id.editDoctor);
        editPharmacist = (EditText) findViewById(R.id.editPharmacist);
        buttonSave = (Button) findViewById(R.id.buttonSaveNewProfile);
        spinnerUserContact = (Spinner) findViewById(R.id.spinnerUserContact);
        buttonSave.setOnClickListener(this);

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

        header_name = (TextView) header.findViewById(R.id.header_name);
        header_name.setText(user_name);

        updateThread = (Thread) getLastNonConfigurationInstance();
        if (updateThread != null && updateThread.isAlive()) {
            buttonSave.setClickable(false);
        }
    }

    public void initializeSpinner(){
        contactAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{});
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserContact.setAdapter(contactAdapter);

        contactList = new ArrayList<>();
        String[] data = new String[]{};
        for(String s : data) {
            addItemOnSpinnerContact(s);
        }
    }

    public void addItemOnSpinnerContact(String addItem) {
        spinnerUserContact= (Spinner) findViewById(R.id.spinnerUserContact);
        int length = spinnerUserContact.getAdapter().getCount();
        contactList.add(addItem);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, contactList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactAdapter = adapter;
        spinnerUserContact.setAdapter(contactAdapter);
    }

    public void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_patient).setVisible(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSaveNewProfile) {
            boolean cancel = false;
            View focusView = null;
            if (TextUtils.isEmpty(editName.getText())) {
                editName.setError(getString(R.string.error_field_required));
                focusView = editName;
                cancel = true;
            } else if (!isNameValid(editName.getText().toString())) {
                editName.setError(getString(R.string.error_invalid_name_format));
                focusView = editName;
                cancel = true;
            }
            if (TextUtils.isEmpty(editBirth.getText())) {
                editBirth.setError(getString(R.string.error_field_required));
                focusView = editBirth;
                cancel = true;
            } else if (!isBirthValid(editBirth.getText().toString())) {
                editBirth.setError(getString(R.string.error_invalid_birth_format));
                focusView = editBirth;
                cancel = true;
            }
            if (TextUtils.isEmpty(editHeight.getText())) {
                editHeight.setError(getString(R.string.error_field_required));
                focusView = editHeight;
                cancel = true;
            } else if (!isNumber(editHeight.getText().toString())) {
                editHeight.setError(getString(R.string.error_invalid_height_format));
                focusView = editHeight;
                cancel = true;
            }
            if (TextUtils.isEmpty(editWeight.getText())) {
                editWeight.setError(getString(R.string.error_field_required));
                focusView = editWeight;
                cancel = true;
            } else if (!isNumber(editWeight.getText().toString())) {
                editWeight.setError(getString(R.string.error_invalid_weight_format));
                focusView = editWeight;
                cancel = true;
            }
            if (TextUtils.isEmpty(editMail.getText())) {
                editMail.setError(getString(R.string.error_field_required));
                focusView = editMail;
                cancel = true;
            } else if (!isMailValid(editMail.getText().toString())) {
                editMail.setError(getString(R.string.error_invalid_email));
                focusView = editMail;
                cancel = true;
            }
            if (TextUtils.isEmpty(editPhone.getText())) {
                editPhone.setError(getString(R.string.error_field_required));
                focusView = editPhone;
                cancel = true;
            } else if (!isNumber(editPhone.getText().toString())) {
                editPhone.setError(getString(R.string.error_invalid_phone_format));
                focusView = editPhone;
                cancel = true;
            }
            if (TextUtils.isEmpty(editDoctor.getText())) {
                editDoctor.setError(getString(R.string.error_field_required));
                focusView = editDoctor;
                cancel = true;
            } else if (!isDoctorValid(editDoctor.getText().toString())) {
                editDoctor.setError(getString(R.string.error_invalid_phone_format));
                focusView = editDoctor;
                cancel = true;
            }
            if (TextUtils.isEmpty(editPharmacist.getText())) {
                editPharmacist.setError(getString(R.string.error_field_required));
                focusView = editPharmacist;
                cancel = true;
            } else if (!isNumber(editPharmacist.getText().toString())) {
                editPharmacist.setError(getString(R.string.error_invalid_phone_format));
                focusView = editPharmacist;
                cancel = true;
            }

            if (cancel) {
                // There was an error
                // form field with an error.
                focusView.requestFocus();
            } else {
                buttonSave.setClickable(false);
                new UpdateUserDetails().execute();
            }
        }
    }

    private boolean isNameValid(String name) {
        if(name.length() > 30) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isBirthValid(String birth) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.parse(birth);
            return true;
        }
        catch(ParseException e){
            return false;
        }
    }

    private boolean isNumber(String weight) {
        return TextUtils.isDigitsOnly(weight);
    }

    private boolean isMailValid(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    private boolean isDoctorValid(String doctor) {
        if(doctor.length() > 30) {
            return false;
        } else {
            return true;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    class UpdateUserDetails extends AsyncTask<Void, Void, Void> {

        private String update = MyGlobalVars.url_update_user
                + MyGlobalVars.TAG_USER_ID+"="+id_to_get+"&"
                + MyGlobalVars.TAG_NAME+"="+editName.getText().toString()+"&"
                + MyGlobalVars.TAG_BIRTH+"="+editBirth.getText().toString()+"&"
                + MyGlobalVars.TAG_EMAIL+"="+editMail.getText().toString()+"&"
                + MyGlobalVars.TAG_HEIGHT+"="+editHeight.getText().toString()+"&"
                + MyGlobalVars.TAG_WEIGHT+"="+editWeight.getText().toString()+"&"
                + MyGlobalVars.TAG_REFERRING_DOCTOR+"="+editDoctor.getText().toString()+"&"
                + MyGlobalVars.TAG_PHARMACIST_NUMBER+"="+editPharmacist.getText().toString()+"&"
                + MyGlobalVars.TAG_PHONE+"="+editPhone.getText().toString();
        private String data="";
        private String success="";

        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage("Updating user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = new ConnectToDB(update).connectJSON();
                jsonData = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once got all details
            user_name = editName.getText().toString();
            header_name.setText(user_name);
            buttonSave.setClickable(true);
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    /**
     * Background Async Task to Get complete product details
     * */
    public class GetUserDetails extends AsyncTask<Void, Void, Boolean> {

        private String update = MyGlobalVars.url_read_user+MyGlobalVars.TAG_USER_ID+"="+id_to_get;
        private String data="";
        private String name="";
        private String birth="";
        private String height="";
        private String weight="";
        private String phone="";
        private String mail="";
        private String refDoctor="";
        private String pharmacist="";

        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected Boolean doInBackground(Void... params) {
            data = new ConnectToDB(update).connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);

                    JSONArray users = jsonData.getJSONArray(MyGlobalVars.TAG_USER);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        name = user.getString(MyGlobalVars.TAG_NAME);
                        birth = user.getString(MyGlobalVars.TAG_BIRTH);
                        phone = user.getString(MyGlobalVars.TAG_PHONE);
                        height = user.getString(MyGlobalVars.TAG_HEIGHT);
                        weight = user.getString(MyGlobalVars.TAG_WEIGHT);
                        mail = user.getString(MyGlobalVars.TAG_EMAIL);
                        refDoctor = user.getString(MyGlobalVars.TAG_REFERRING_DOCTOR);
                        pharmacist = user.getString(MyGlobalVars.TAG_PHARMACIST_NUMBER);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(final Boolean success) {
            // dismiss the dialog once got all details
            editName.setText(this.name);
            editMail.setText(this.mail);
            editHeight.setText(this.height);
            editWeight.setText(this.weight);
            editBirth.setText(this.birth);
            editPhone.setText(this.phone);
            editDoctor.setText(this.refDoctor);
            editPharmacist.setText(this.pharmacist);
            initializeSpinner();
            new GetContactTask().execute();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetContactTask extends AsyncTask<Void, Void, Boolean> {

        private String update = MyGlobalVars.url_get_contact
                + MyGlobalVars.TAG_USER_ID+"="+id_to_get;
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
                for(int i = 0; i < names.size(); i++) {
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
}
