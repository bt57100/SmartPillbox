package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Progress Dialog
    private ProgressDialog pDialog;

    // User id
    private static String user_id;
    private static String user_name;
    private static String medical_staff;
    private static ArrayList<String> patient_names = new ArrayList<>();
    private static ArrayList<String> patient_ids = new ArrayList<>();

    private static GridLayout add_new_patient_grid;
    private static Button addNewPatientButton;

    private static EditText new_patient_mail;
    private static EditText new_patient_phone;
    private static Button saveNewPatientButton;
    private static int gridID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
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

        new GetPatientDetails().execute();

        add_new_patient_grid = (GridLayout) findViewById(R.id.add_patient_grid);
        addNewPatientButton = (Button) findViewById(R.id.add_patient_to_list_button);
        addNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPatientButton.setVisibility(View.GONE);
                add_new_patient_grid.setVisibility(View.VISIBLE);
            }
        });

        new_patient_mail = (EditText) findViewById(R.id.editPatientMail);
        new_patient_phone = (EditText) findViewById(R.id.editPatientPhone);
        saveNewPatientButton = (Button) findViewById(R.id.buttonSaveNewPatient);
        saveNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewPatient();
            }
        });

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
    }

    public void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_patient).setVisible(true);
    }

    public void saveNewPatient() {
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(new_patient_mail.getText())) {
            new_patient_mail.setError(getString(R.string.error_field_required));
            focusView = new_patient_mail;
            cancel = true;
        } else if (!isMailValid(new_patient_mail.getText().toString())) {
            new_patient_mail.setError(getString(R.string.error_invalid_email));
            focusView = new_patient_mail;
            cancel = true;
        }
        if (TextUtils.isEmpty(new_patient_phone.getText())) {
            new_patient_phone.setError(getString(R.string.error_field_required));
            focusView = new_patient_phone;
            cancel = true;
        } else if (!isNumber(new_patient_phone.getText().toString())) {
            new_patient_phone.setError(getString(R.string.error_invalid_phone_format));
            focusView = new_patient_phone;
            cancel = true;
        }
        if (cancel) {
            // There was an error
            // form field with an error.
            focusView.requestFocus();
        } else {
            new SavePatientDetails().execute();
        }
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

    public void initializePatients() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.all_patient_list);

        final GridView gridView = new GridView(this);
        gridID = generateViewId();
        gridView.setId(gridID);
        gridView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        gridView.setNumColumns(3);

        final ArrayList<String> gridContentList = new ArrayList<>();
        for(String name : patient_names) {
            gridContentList.add(name);
            gridContentList.add(MyGlobalVars.TAG_PROFILE);
            gridContentList.add(MyGlobalVars.TAG_TREATMENT);
        }

        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, gridContentList);

        gridView.setAdapter(gridViewArrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position/3;
                if (position%3 == 1) {
                    patientProfile(patient_ids.get(index));
                } else if (position%3 == 2) {
                    patientTreatment(patient_ids.get(index));
                }
            }
        });

        layout.addView(gridView);
    }

    public void addPatient(String patientName, final String patientID) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.all_patient_list);
        layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"

        final GridView gridView = new GridView(this);
        gridID = generateViewId();
        gridView.setId(gridID);
        gridView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        gridView.setNumColumns(3);

        final ArrayList<String> gridContentList = new ArrayList<>();
        gridContentList.add(patientName);
        gridContentList.add(MyGlobalVars.TAG_PROFILE);
        gridContentList.add(MyGlobalVars.TAG_TREATMENT);

        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, gridContentList);

        gridView.setAdapter(gridViewArrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position%3 == 1) {
                    patientProfile(patientID);
                } else if (position%3 == 2) {
                    patientTreatment(patientID);
                }
            }
        });

        layout.addView(gridView);
    }

    public void patientProfile(String patientID) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
        i.putExtra(MyGlobalVars.TAG_NAME, user_name);
        i.putExtra(MyGlobalVars.TAG_PATIENT_ID, patientID);
        i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
        startActivity(i);
    }

    public void patientTreatment(String patientID) {
        Intent i = new Intent(this, TreatmentActivity.class);
        i.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
        i.putExtra(MyGlobalVars.TAG_NAME, user_name);
        i.putExtra(MyGlobalVars.TAG_PATIENT_ID, patientID);
        i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
        startActivity(i);
    }

    public static int generateViewId() {
        AtomicInteger sNextGeneratedId = new AtomicInteger(1);
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private boolean isNumber(String weight) {
        return TextUtils.isDigitsOnly(weight);
    }

    private boolean isMailValid(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    class SavePatientDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private String patientName = "";
        private String patientID = "";
        private String message = "";

        HttpURLConnection urlConnection;
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PatientActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = updateJSON();
                jsonData = new JSONObject(data);
                patientName = jsonData.getString(MyGlobalVars.TAG_PATIENT_NAME);
                patientID = jsonData.getString(MyGlobalVars.TAG_PATIENT_ID);
                message = jsonData.getString(MyGlobalVars.TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String updateJSON() {
            HttpURLConnection connection = null;
            try {
                String update = MyGlobalVars.url_add_patient
                        + MyGlobalVars.TAG_MEDECIN_ID + "=" + user_id + "&"
                        + MyGlobalVars.TAG_EMAIL + "=" + new_patient_mail.getText().toString() + "&"
                        + MyGlobalVars.TAG_PHONE + "=" + new_patient_phone.getText().toString();
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
            if(patientName.isEmpty()) {
                new_patient_mail.setError(message);
            } else {
                add_new_patient_grid.setVisibility(View.GONE);
                addNewPatientButton.setVisibility(View.VISIBLE);
                addPatient(patientName, patientID);
            }
            // dismiss the dialog once got all details
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    /**
     * Background Async Task to Get complete product details
     * */
    class GetPatientDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private ArrayList<String> patientNames = new ArrayList<>();
        private ArrayList<String> patientIDs = new ArrayList<>();

        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PatientActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected Void doInBackground(Void... voids) {
            try {
                data = getJSON();
                jsonData = new JSONObject(data);

                JSONArray users = jsonData.getJSONArray(MyGlobalVars.TAG_PATIENT_MIN);
                for(int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    patientIDs.add(user.getString(MyGlobalVars.TAG_USER_ID));
                    patientNames.add(user.getString(MyGlobalVars.TAG_NAME));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getJSON() {
            HttpURLConnection connection = null;
            try {
                URL u = new URL(MyGlobalVars.url_get_patient+MyGlobalVars.TAG_MEDECIN_ID+"="+user_id);
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
                        BufferedReader br = new BufferedReader(new InputStreamReader(responseStream),8);
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
            // dismiss the dialog once got all details
            patient_names = this.patientNames;
            patient_ids = this.patientIDs;
            initializePatients();
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

}
