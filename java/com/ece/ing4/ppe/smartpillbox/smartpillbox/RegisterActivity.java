package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    private static String user_id;
    private static String user_name;
    private static String medical_staff;
    private static Button mEmailSignInButton;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.passwordLogin);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        TextView registerText = (TextView) findViewById(R.id.register_text);
        registerText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            new UserRegisterTask().execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void success() {
        Intent i = new Intent(this, SmartPillboxMainActivity.class);
        i.putExtra(MyGlobalVars.TAG_USER_ID, user_id);
        i.putExtra(MyGlobalVars.TAG_NAME, user_name);
        i.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF, medical_staff);
        startActivity(i);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

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
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            mEmailSignInButton.setClickable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            data = connectJSON();
            if(data.isEmpty()) {
                return false;
            } else {
                try {
                    jsonData = new JSONObject(data);
                    this.message = jsonData.getString(MyGlobalVars.TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(this.message.contains("successful")) {
                new UserLoginTask().execute();
            } else {
                Toast.makeText(getApplicationContext(), this.message,
                        Toast.LENGTH_LONG).show();
            }
        }

        public String connectJSON() {
            HttpURLConnection connection = null;
            String update="";
            int status=0;
            try {
                update = MyGlobalVars.url_register_user
                        + MyGlobalVars.TAG_EMAIL + "=" + mEmailView.getText().toString() + "&"
                        + MyGlobalVars.TAG_PASSWORD + "=" + mPasswordView.getText().toString();
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
                //int status = connection.getResponseCode();
                status = connection.getResponseCode();
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
                status=100;
                data=ex.getMessage();
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                status=101;
                data=ex.getMessage();
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return String.valueOf(status)+data;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

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
            mEmailSignInButton.setClickable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            data = connectJSON();
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
                        this.userId = user.getString(MyGlobalVars.TAG_USER_ID);
                        this.medicalStaff = user.getString(MyGlobalVars.TAG_MEDICAL_STAFF);
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
            // dismiss the dialog once got all details
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (this.success.contentEquals("1")) {
                user_id = this.userId;
                medical_staff = this.medicalStaff;
                user_name = this.userName;
                success();
            } else {
                Toast.makeText(getApplicationContext(), this.message,
                        Toast.LENGTH_LONG).show();
            }
            mEmailSignInButton.setClickable(true);
        }

        public String connectJSON() {
            HttpURLConnection connection = null;
            String update="";
            int status=0;
            try {
                update = MyGlobalVars.url_connect_user
                        + MyGlobalVars.TAG_EMAIL + "=" + mEmailView.getText().toString() + "&"
                        + MyGlobalVars.TAG_PASSWORD + "=" + mPasswordView.getText().toString();
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
                //int status = connection.getResponseCode();
                status = connection.getResponseCode();
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
                status=100;
                data=ex.getMessage();
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                status=101;
                data=ex.getMessage();
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return String.valueOf(status)+data;
        }
    }
}


