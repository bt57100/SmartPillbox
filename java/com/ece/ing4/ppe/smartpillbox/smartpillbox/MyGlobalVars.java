package com.ece.ing4.ppe.smartpillbox.smartpillbox;

/**
 * Created by Kevin on 06/03/2017.
 */

public final class MyGlobalVars {

    // Cookie
    private static final String cookie="bee48062b61a751e23b813174183a983";
    public static final String myCookie = "__test="+cookie+"; expires=Fri, 1-Jan-38 00:55:55 GMT; path=/";

    // URL
    public static final String url_get_user = "http://smartpillbox.byethost7.com/database/user/get_user_info.php?";
    public static final String url_connect_user = "http://smartpillbox.byethost7.com/database/user/connect_user.php?";
    public static final String url_register_user = "http://smartpillbox.byethost7.com/database/user/create_user.php?";
    public static final String url_read_user = "http://smartpillbox.byethost7.com/database/user/get_user_info.php?";
    public static final String url_update_user = "http://smartpillbox.byethost7.com/database/user/update_user_info.php?";
    public static final String url_delete_user = "http://smartpillbox.byethost7.com/database/user/delete_user.php?";
    public static final String url_get_patient = "http://smartpillbox.byethost7.com/database/user/get_all_patients.php?";
    public static final String url_add_patient = "http://smartpillbox.byethost7.com/database/user/add_new_patient.php?";
    public static final String url_get_contact = "http://smartpillbox.byethost7.com/database/contact/get_all_contacts.php?";
    public static final String url_add_contact = "http://smartpillbox.byethost7.com/database/contact/create_contact.php?";
    public static final String url_delete_contact = "http://smartpillbox.byethost7.com/database/contact/delete_contact.php?";
    // Connection timeout
    public static final int TIMEOUT = 4000;

    // JSON Node names
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_USER = "user";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_PASSWORD = "PASSWORD";
    public static final String TAG_PATIENT = "PATIENT";
    public static final String TAG_PATIENT_AVG = "patient";
    public static final String TAG_PATIENT_MIN = "patient";
    public static final String TAG_CONTACTS_MIN = "contacts";
    public static final String TAG_TREATMENT = "Treatment";
    public static final String TAG_TREATMENT_ID = "TREATMENT_ID";
    public static final String TAG_PROFILE = "Profile";
    public static final String TAG_OLD_PASSWORD = "OLD_PASSWORD";
    public static final String TAG_USER_ID = "USER_ID";
    public static final String TAG_MEDECIN_ID = "MEDECIN_ID";
    public static final String TAG_PATIENT_NAME = "PATIENT_NAME";
    public static final String TAG_PATIENT_ID = "PATIENT_ID";
    public static final String TAG_CONTACT_ID = "CONTACT_ID";
    public static final String TAG_CONTACT_ID_MIN = "contact_id";
    public static final String TAG_NAME = "NAME";
    public static final String TAG_BIRTH = "BIRTHDAY";
    public static final String TAG_HEIGHT = "HEIGHT";
    public static final String TAG_WEIGHT = "WEIGHT";
    public static final String TAG_EMAIL = "EMAIL";
    public static final String TAG_PHONE = "PHONE";
    public static final String TAG_RELATIONSHIP = "RELATIONSHIP";
    public static final String TAG_RELATIONSHIP_AVG = "Relationship";
    public static final String TAG_NOTIFY = "NOTIFY";
    public static final String TAG_REFERRING_DOCTOR = "REFERRING_DOCTOR";
    public static final String TAG_PHARMACIST_NUMBER = "PHARMACIST_NUMBER";
    public static final String TAG_MEDICAL_STAFF = "MEDICAL_STAFF";
    public static final String TAG_MEDICAL_STAFF_AVG = "Medical staff";
    public static final String TAG_NOTIFICATION_TYPE= "NOTIFICATION_TYPE";
    public static final String TAG_NOTIFICATION_MESSAGE= "Time to take your pills !";
    public static final String TAG_APP_NAME = "SmartPillbox";
    public static final int HOUR_SMS = 14;
}
