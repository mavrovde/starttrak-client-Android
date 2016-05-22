package com.startrack.android.startrack_android.app;

import android.app.Application;
import android.content.Context;

import com.startrack.android.startrack_android.api.ApiHandler;
import com.startrack.android.startrack_android.model.BaseProfileProperty;
import com.startrack.android.startrack_android.model.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/15/16.
 */
public class StarTrackApplication extends Application {
    public static String SERVER_ADRESS = "http://mavrov.de:8080";
    public static String LOGIN_API = "/starttrak-profiles-rest/service/auth/login";
    public static String PROFILE_API = "/starttrak-profiles-rest/service/profile";
    public static String GET_DICTIONARIES_API = "/starttrak-profiles-rest/service/dictionaries/all";
    public static String VALIDATE_SESSION_TOKEN_API = "/starttrak-profiles-rest/service/auth/validate";
    public static String SEARCH_API = "/starttrak-profiles-rest/service/profile/search";
    public static String MEET_API = "/starttrak-profiles-rest/service/profile/meet";

    //public static boolean testModeFlag = false;
    public static boolean allScreensAlwaysForTest = false;

    public static String[] meetParameters;

    //public static Map<Integer, BaseProfileProperty> editableProperties = new HashMap<Integer,BaseProfileProperty>();
    //public static Map<Integer, BaseProfileProperty> selectableProperties = new HashMap<Integer,BaseProfileProperty>();

    /*    0 - first name
*     1 - last name
*     2 - phone number
*     3 - company name
*     4 - company size
*     5 - title
*     6 - seniority
*     7 - industry
*     8 - country
*     9 - state
*     10 - position
* */

    public static final Map<Integer,String> propertyNames = new HashMap<Integer,String>() {

        {
            put(0, "FIRST NAME");
            put(1, "LAST NAME");
            put(2, "PHONE NUMBER");
            put(3, "COMPANY NAME");
            put(4, "COMPANY SIZE");
            put(5, "TITLE");
            put(6, "SENIORITY");
            put(7, "INDUSTRY");
            put(8, "COUNTRY");
            put(9, "STATE");
            put(10, "POSITION");
            put(11, "CITY");
        }

        ;
    };

    public static Map<Integer,Map> dictionaries = new HashMap<Integer,Map>();
    public static Profile currentProfile = new Profile();
    public static List<Profile> searchResults = new ArrayList<Profile>();
    public static String oauthToken = "";
    public static String oauthExpiresIn = "";
    public static int soc_network_type = -1;
    public static String sessionId = "";
    public static String internal_account_email = "";
    public static String internal_account_password = "";

    //xing settings
    public static String xingOauthRequestToken;
    public static String xingOauthRequestTokenSecret;
    public static String xingOauthVerifier;

    // search settings
    public static String searchText = "";
    public static int titleId =  (-1);
    public static int seniorityId =  (-1);
    public static int industryId =  (-1);
    public static int countryId =  (-1);
    public static int positionId =  (-1);
    public static int companysizeId =  (-1);

    public static boolean profileExistsFlag = false;

    public static void logout(Context context){
        dictionaries = new HashMap<Integer,Map>();
        currentProfile = new Profile();
        searchResults = new ArrayList<Profile>();
        oauthToken = "";
        oauthExpiresIn = "";
        soc_network_type = -1;
        sessionId = "";
        internal_account_email = "";
        internal_account_password = "";

        ApiHandler.setSocTypeFromPreferences(context, (-1));
        ApiHandler.setSessionIdFromPreferences(context, "null");
        ApiHandler.setLoginFromPreferences(context, "null");
        ApiHandler.setPasswordFromPreferences(context, "null");
        ApiHandler.setSocTokenFromPreferences(context, "null");
    }
}
