package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class TreatmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // User id
    private static String user_id;
    private static String user_name;
    private static String medical_staff;


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
}
