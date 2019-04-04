package com.project.naveen.smarteventsshare.socialintegration.constants;

/**
 * Created by Arun on 06-09-2015.
 */
public class AppConstants {

    public enum SharedPreferenceKeys {
        USER_NAME("userName"),
        USER_EMAIL("userEmail"),
        USER_IMAGE_URL("userImageUrl");


        private String value;

        SharedPreferenceKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }


    public static final String GOOGLE_CLIENT_ID = "95998377574-oasmaepqjpso14f657bvu3jafie5tsu7.apps.googleusercontent.com";
}
