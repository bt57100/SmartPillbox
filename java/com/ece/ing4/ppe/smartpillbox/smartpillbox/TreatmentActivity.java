package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar.LayoutParams;


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
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreatmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // User id
    private static String user_id;
    private static String user_name;
    private static String medical_staff;

    private TextView mytextName;

    private static Spinner spinnerTreatment, spinnerMedicine;


    private String user, temps, dose, expiration, nom;

    // Progress Dialog
    private ProgressDialog pDialog;
    private EditText t;
    int text_number, number;
    private boolean onChange = false;
    private String myTest, myTest2 = "1", myTest3, myTest4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        t = (EditText) findViewById(R.id.opLevel);

        mytextName = (TextView) findViewById(R.id.mytextName);

        spinnerTreatment = (Spinner) findViewById(R.id.spinnerTreatment);

        spinnerTreatment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                //t.setText(myTest2);
                if (spinnerTreatment.getSelectedItem().toString() != myTest && onChange) {
                    spinnerMedicine.setAdapter(null);
                    initializeSpinner(spinnerMedicine);
                    new GetMedicineDetails().execute();
                }

                myTest = spinnerTreatment.getSelectedItem().toString();
                myTest2 = "" + myTest.charAt(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                new GetMedicineDetails().execute();
            }

        });

        spinnerMedicine = (Spinner) findViewById(R.id.spinnerMedicine);

        spinnerMedicine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                myTest3 = spinnerMedicine.getSelectedItem().toString();
                myTest4 = "" + myTest3.charAt(0);
                //t.setText(myTest2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        initializeSpinner(spinnerTreatment);


        initializeSpinner(spinnerMedicine);

        // Getting complete product details in background thread
        new GetTreatmentDetails().execute();

        // Getting complete product details in background thread
        //new GetMedicineDetails().execute();

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

    public void initializeSpinner(Spinner mySpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        //TODO get contact list from database
        String[] data = new String[]{};
        for (String s : data) {
            addItemOnSpinnerContact(s, mySpinner);
        }
    }

    public void addItemOnSpinnerContact(String addItem, Spinner mySpinner) {
        int length = mySpinner.getAdapter().getCount();
        String arrN[] = new String[length + 1];
        arrN[0] = addItem;

        for (int i = 0; i < length; i++) {
            arrN[i + 1] = (String) mySpinner.getAdapter().getItem(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrN);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
    }

    public void myTreatment(ArrayList<String> batch_number, ArrayList<String> name, ArrayList<String> expiration_date, ArrayList<String> dosage) {

        final LinearLayout lm = (LinearLayout) findViewById(R.id.medicineAddLayout);
        if (lm.getChildCount() > 0) {
            lm.removeAllViews();
        }
        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ArrayList<LinearLayout> myMedicine = new ArrayList<>();
        //Create four
        text_number = batch_number.size();
        for (int j = 0; j <= batch_number.size() - 1; j++) {
            // Create LinearLayout
            myMedicine.add(new LinearLayout(this));
            //LinearLayout ll = new LinearLayout(this);
            myMedicine.get(j).setOrientation(LinearLayout.HORIZONTAL);

            // Create TextView
            TextView product = new TextView(this);
            product.setText(name.get(j) + " : ");
            myMedicine.get(j).addView(product);

            // Create TextView
            TextView doom = new TextView(this);
            doom.setText(batch_number.get(j));
            doom.setVisibility(View.GONE);
            myMedicine.get(j).addView(doom);

            // Create TextView
            TextView doom1 = new TextView(this);
            doom1.setText(name.get(j));
            doom1.setVisibility(View.GONE);
            myMedicine.get(j).addView(doom1);

            // Create TextView
            TextView doom2 = new TextView(this);
            doom2.setText(expiration_date.get(j) + " ");
            doom2.setVisibility(View.GONE);
            myMedicine.get(j).addView(doom2);

            // Create TextView
            TextView doom3 = new TextView(this);
            doom3.setText(dosage.get(j) + " ");
            myMedicine.get(j).addView(doom3);

            // Create Button
            final Button btn = new Button(this);
            // Give button an ID
            btn.setId(j + 1);
            btn.setText("Change");
            // set the layoutParams on the button
            btn.setLayoutParams(params);

            final int index = j;
            // Set click listener for button
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Log.i("TAG", "index :" + index);
                    number = index;
                    test(index);

                    Toast.makeText(getApplicationContext(), "Clicked Button Index :" + index, Toast.LENGTH_SHORT).show();

                }
            });

            //Add button to LinearLayout
            myMedicine.get(j).addView(btn);
            //Add button to LinearLayout defined in XML
            lm.addView(myMedicine.get(j));
        }
    }
     /*mAuthTask = new TreatmentTask(email, password);
            mAuthTask.execute((Void) null);*/

    public void saveTreatment(View view) {
        saveUpdateUser();
    }

    private void saveUpdateUser() {
        new UpdateUserDetails().execute();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class UpdateUserDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private ArrayList<String> success = new ArrayList<>();
        private String dosage, name, expiration_date, batch_number, treatment_id = myTest2;

        HttpURLConnection urlConnection;
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for (int x = 0; x < 10; x++) {
                System.out.println("im a banana");
            }
            number = 0;
            pDialog = new ProgressDialog(TreatmentActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (number < text_number) {
                saves();
                try {
                    data = updateJSON(MyGlobalVars.url_update_medicine_info);
                    jsonData = new JSONObject(data);
                    success.add(jsonData.getString(MyGlobalVars.TAG_SUCCESS));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                number++;
            }
            return null;
        }

        private void saves() {
            Button myButton = null;
            for (int i = 0; i < text_number; i++) {
                if (i == number) {
                    myButton = (Button) findViewById(i + 1);
                }
            }
            LinearLayout test = (LinearLayout) myButton.getParent();
            for (int j = 0; j < test.getChildCount(); j++) {
                View myView = test.getChildAt(j);
                if (j == 4) {
                    dosage = (((TextView) myView).getText().toString());
                }
                if (j == 3) {
                    expiration_date = (((TextView) myView).getText().toString());
                }
                if (j == 2) {
                    name = (((TextView) myView).getText().toString());
                }
                if (j == 1) {
                    batch_number = (((TextView) myView).getText().toString());
                }
            }
            System.out.println("dosage :" + dosage + " " + "expiration :" + expiration_date + " " + "name  :" + name + " " + "batch  :" + batch_number + " " + "treatment  :" + treatment_id);
        }

        // http://smartpillbox.byethost7.com/database/medicine/update_medicine_info.php?TREATMENT_ID=1&BATCH_NUMBER=bfjkng897hj82&NAME=granddieu&DOSAGE=soir%20et%20matin&EXPIRATION_DATE=2018-04-20%2000:00:00
        // TREATMENT_ID=1&BATCH_NUMBER=bfjkng897hj82&NAME=granddieu&DOSAGE=soir%20et%20matin&EXPIRATION_DATE=2018-04-20%2000:00:00
        public String updateJSON(String url) {
            HttpURLConnection connection = null;
            try {
                String update = "" + url
                        + MyGlobalVars.TAG_BATCH_NUMBER + "=" + batch_number + "&"
                        + MyGlobalVars.TAG_TREATMENT_ID + "=" + treatment_id + "&"
                        + MyGlobalVars.TAG_NAME + "=" + name + "&"
                        + MyGlobalVars.TAG_EXPIRATION_DATE + "=" + expiration_date + "&"
                        + MyGlobalVars.TAG_DOSAGE + "=" + dosage;
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

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once got all details
            //editContact.setText(data);
            for(String ok : success) {
                if(ok.contentEquals("0")) {
                    Toast.makeText(getBaseContext(), "Failed to update", Toast.LENGTH_SHORT);
                }
            }
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    /**
     * Background Async Task to Get complete product details
     */
    class GetTreatmentDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private ArrayList<String> start_date = new ArrayList<>();
        private ArrayList<String> end_date = new ArrayList<>();
        private ArrayList<String> doctor = new ArrayList<>();
        private ArrayList<String> treatmentID = new ArrayList<>();

        HttpURLConnection urlConnection;
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onChange = false;
            pDialog = new ProgressDialog(TreatmentActivity.this);
            pDialog.setMessage("Loading treatment details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected Void doInBackground(Void... voids) {
            try {
                data = getJSON(MyGlobalVars.url_get_all_treatments);
                jsonData = new JSONObject(data);

                JSONArray treatments = jsonData.getJSONArray(MyGlobalVars.TAG_TREATMENT_MIN);
                for (int i = 0; i < treatments.length(); i++) {
                    JSONObject treatment = treatments.getJSONObject(i);
                    treatmentID.add(treatment.getString(MyGlobalVars.TAG_TREATMENT_ID));
                    start_date.add(treatment.getString(MyGlobalVars.TAG_START_DATE));
                    end_date.add(treatment.getString(MyGlobalVars.TAG_END_DATE));
                    doctor.add(treatment.getString(MyGlobalVars.TAG_DOCTOR));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getJSON(String url) {
            HttpURLConnection connection = null;
            try {
                URL u = new URL(url + user_id);
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

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once got all details
            String s;
            for (int i = treatmentID.size() - 1; i >= 0; i--) {
                s = treatmentID.get(i) + ": " + start_date.get(i) + " " + end_date.get(i);
                addItemOnSpinnerContact(s, spinnerTreatment);
            }

            //myTreatment();

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            onChange = true;

            new GetMedicineDetails().execute();
        }
    }

    /**
     * Background Async Task to Get complete product details
     */
    class GetMedicineDetails extends AsyncTask<Void, Void, Void> {

        private String data = "";
        private ArrayList<String> dosage = new ArrayList<>();
        private ArrayList<String> expiration_date = new ArrayList<>();
        private ArrayList<String> batch_number = new ArrayList<>();
        private ArrayList<String> treatmentID = new ArrayList<>();
        private ArrayList<String> name = new ArrayList<>();

        HttpURLConnection urlConnection;
        JSONObject jsonData;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onChange = false;
            pDialog = new ProgressDialog(TreatmentActivity.this);
            pDialog.setMessage("Loading treatment details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected Void doInBackground(Void... voids) {
            try {
                data = getJSON(MyGlobalVars.url_get_all_medicine);
                jsonData = new JSONObject(data);

                JSONArray medicines = jsonData.getJSONArray(MyGlobalVars.TAG_MEDICINE);
                for (int i = 0; i < medicines.length(); i++) {
                    JSONObject medicine = medicines.getJSONObject(i);
                    batch_number.add(medicine.getString(MyGlobalVars.TAG_BATCH_NUMBER));
                    treatmentID.add(medicine.getString(MyGlobalVars.TAG_TREATMENT_ID));
                    name.add(medicine.getString(MyGlobalVars.TAG_NAME));
                    expiration_date.add(medicine.getString(MyGlobalVars.TAG_EXPIRATION_DATE));
                    dosage.add(medicine.getString(MyGlobalVars.TAG_DOSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getJSON(String url) {
            HttpURLConnection connection = null;
            try {
                URL u = new URL(url + myTest2);
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

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once got all details
            String s;
            for (int i = treatmentID.size() - 1; i >= 0; i--) {
                s = "Medicine " + i + " : " + dosage.get(i) + " " + expiration_date.get(i);
                addItemOnSpinnerContact(s, spinnerMedicine);
            }

            myTreatment(batch_number, name, expiration_date, dosage);

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            onChange = true;
        }
    }

    public void test(int view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.medicine_change_dialog, null);
        final EditText batch_number = (EditText) alertLayout.findViewById(R.id.batch_number);
        final EditText name = (EditText) alertLayout.findViewById(R.id.medicine_patient_name);
        final EditText expiration_date = (EditText) alertLayout.findViewById(R.id.expiration_date);
        final EditText dosage = (EditText) alertLayout.findViewById(R.id.dosage);
        final CheckBox show = (CheckBox) alertLayout.findViewById(R.id.show);
        Button myButton = null;

        for (int i = 0; i < text_number; i++) {
            if (i == view) {
                myButton = (Button) findViewById(i + 1);
            }
        }

        LinearLayout test = (LinearLayout) myButton.getParent();
        for (int j = 0; j < test.getChildCount(); j++) {
            View myView = test.getChildAt(j);
            if (j == 4) {
                dosage.setText(((TextView) myView).getText().toString());
            }

            if (j == 3) {
                expiration_date.setText(((TextView) myView).getText().toString());
            }

            if (j == 2) {
                name.setText(((TextView) myView).getText().toString());
            }

            if (j == 1) {
                batch_number.setText(((TextView) myView).getText().toString());
            }

        }

        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Medicin change");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Batch Number", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Button myButton = null;

                for (int i = 0; i < text_number; i++) {
                    if (i == number) {
                        myButton = (Button) findViewById(i + 1);
                    }
                }

                final LinearLayout test = (LinearLayout) myButton.getParent();
                for (int j = 0; j < test.getChildCount(); j++) {
                    View myView = test.getChildAt(j);
                    if (j == 4) {
                        ((TextView) myView).setText(dosage.getText().toString());
                    }

                    if (j == 3) {
                        ((TextView) myView).setText(expiration_date.getText().toString());
                    }

                    if (j == 2 || j == 0) {
                        ((TextView) myView).setText(name.getText().toString());
                    }

                    if (j == 1) {
                        ((TextView) myView).setText(batch_number.getText().toString());
                    }

                }
                user = batch_number.getText().toString();
                nom = name.getText().toString();
                expiration = expiration_date.getText().toString();
                dose = dosage.getText().toString();
                Toast.makeText(getBaseContext(), "Batch number: " + batch_number.getText().toString() + " " + number + " Time: " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                saveUpdateUser();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void add_medicine(int view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.medicine_change_dialog, null);
        final EditText batch_number = (EditText) alertLayout.findViewById(R.id.batch_number);
        final EditText name = (EditText) alertLayout.findViewById(R.id.medicine_patient_name);
        final EditText expiration_date = (EditText) alertLayout.findViewById(R.id.expiration_date);
        final EditText dosage = (EditText) alertLayout.findViewById(R.id.dosage);
        final CheckBox show = (CheckBox) alertLayout.findViewById(R.id.show);


        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Medicin change");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Batch Number", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                user = batch_number.getText().toString();
                nom = name.getText().toString();
                expiration = expiration_date.getText().toString();
                dose = dosage.getText().toString();
                Toast.makeText(getBaseContext(), "Batch number: " + batch_number.getText().toString() + " " + number + " Time: " + name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
