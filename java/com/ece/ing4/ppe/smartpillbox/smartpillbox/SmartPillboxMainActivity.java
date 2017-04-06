package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ece.ing4.ppe.smartpillbox.smartpillbox.notification.AlarmReceiver;
import com.ece.ing4.ppe.smartpillbox.smartpillbox.notification.SMSSender;

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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmartPillboxMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // This is the date picker used to select the date for our notification
    private DatePicker picker;

    // Progress Dialog
    private ProgressDialog pDialog;
    private TextView textView2;

    // User id
    private static String user_id;
    private static String user_name;
    private static String medical_staff;

    private static ArrayList<String> contactPhone = new ArrayList<>();

    private static DrawerLayout drawer;
    private static ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_pillbox_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
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

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(user_name + String.valueOf(user_id));


        // Get a reference to our date picker
        picker = (DatePicker) findViewById(R.id.scheduleTimePicker);

        remindNotification();
    }

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void remindNotification() {
        // Send Notification
        Intent alarmReceiver = new Intent(this, AlarmReceiver.class);
        alarmReceiver.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
        alarmReceiver.putExtra(MyGlobalVars.TAG_NAME, user_name);
        alarmReceiver.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, 50);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        // Send SMS
        ActivityCompat.requestPermissions(SmartPillboxMainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    try {
                        Intent SMSSender = new Intent(this, SMSSender.class);
                        SMSSender.putExtra(MyGlobalVars.TAG_USER_ID, user_id);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, SMSSender, 0);
                        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR, MyGlobalVars.HOUR_SMS);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "SMS failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    // permission denied
                    Toast.makeText(SmartPillboxMainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(SmartPillboxMainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            1);
                }
                return;
            }
        }
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

    /**
     * This is the onClick called from the XML to set a new notification
     */
    public void onDateSelectedButtonClick(View v){}

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
