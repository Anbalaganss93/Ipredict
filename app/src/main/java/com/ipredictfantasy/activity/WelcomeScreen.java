package com.ipredictfantasy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseApp;
import com.ipredictfantasy.asynctasks.EmailLoginTask;
import com.ipredictfantasy.R;
import com.ipredictfantasy.utility.GlobalActivity;
import com.ipredictfantasy.utility.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WelcomeScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int RC_SIGN_IN = 9001;
    OkHttpClient client = new OkHttpClient();
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mcallbackmanager;
    private InternetChecking internetChecking;
    private SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    private Profile profile;
    private String social_id;
    private String userid;
    private String login_data;
    private String mUsername;
    private EditText username, password;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        FirebaseApp.getInstance();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/proximanova-reg.ttf");
        Typeface typefacebold = Typeface.createFromAsset(getAssets(), "fonts/proximanova-bold.ttf");

        preferences = getSharedPreferences("ipredict", MODE_PRIVATE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mcallbackmanager = CallbackManager.Factory.create();
        internetChecking = new InternetChecking(WelcomeScreen.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        printHashKey();

        username = (EditText) findViewById(R.id.w_username);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        password = (EditText) findViewById(R.id.w_password);
        Button login = (Button) findViewById(R.id.login);
        TextView signup = (TextView) findViewById(R.id.signup);
        TextView welcometext = (TextView) findViewById(R.id.welcometext);
        TextView forgot = (TextView) findViewById(R.id.forgot);
        Button facebooklogin = (Button) findViewById(R.id.facebook);
        Button googlepluslogin = (Button) findViewById(R.id.googleplus);

        stopAnim();

        forgot.setOnClickListener(this);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        facebooklogin.setOnClickListener(this);
        googlepluslogin.setOnClickListener(this);

        login.setTypeface(typefacebold);
        signup.setTypeface(typefacebold);
        facebooklogin.setTypeface(typeface);
        googlepluslogin.setTypeface(typeface);
        username.setTypeface(typeface);
        password.setTypeface(typeface);
        welcometext.setTypeface(typefacebold);
        forgot.setTypeface(typeface);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    void startAnim() {
        avi.setVisibility(View.VISIBLE);
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.setVisibility(View.GONE);
        avi.hide();
        // or avi.smoothToHide();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (mcallbackmanager != null) {
            mcallbackmanager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;
            String personName = acct.getGivenName();
            Uri photoUrl = acct.getPhotoUrl();
            String personPhotoUrl = null;
            if (photoUrl != null) {
                personPhotoUrl = photoUrl.toString();
            }
            String email = acct.getEmail();

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("name", personName);
                jsonObject.put("email", email);
                jsonObject.put("phone", "");
                jsonObject.put("image_url", personPhotoUrl);
                jsonObject.put("social_id", social_id);
                jsonObject.put("login_type", "3");
                jsonObject.put("password", "");
                jsonObject.put("gcmId", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new socialloginCall(jsonObject).execute(DomainUrl.durl + "register");
        } else {
            stopAnim();
            signOut();
        }
    }


    private void signIn() {
        if (!mGoogleApiClient.isConnecting()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d("GOOGLEPLUS", "Signout");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                mUsername = username.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                if (validation()) {
                    if (internetChecking.isConnectingToInternet()) {
                        startAnim();
                        Logincall(mUsername, mPassword);
                    }
                }
                break;
            case R.id.signup:
                Intent registerintent = new Intent(getApplicationContext(), RegisterPage.class);
                startActivity(registerintent);
                finish();
                break;
            case R.id.facebook:
                if (internetChecking.isConnectingToInternet()) {
                    startAnim();
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                    LoginManager.getInstance().registerCallback(mcallbackmanager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            AccessToken accessToken = loginResult.getAccessToken();
                            stopAnim();
                            /*try {
                                profile = Profile.getCurrentProfile();
                                Profile.fetchProfileForCurrentAccessToken();
                                if (profile != null) {
                                    userid = profile.getId();
                                }

                                GraphRequest request = GraphRequest.newMeRequest(
                                        accessToken,
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONObject object,
                                                    GraphResponse response) {
                                                social_id = String.valueOf(userid
                                                );
                                                String username = null, usermail = null;
                                                String facebook_graph_respones = response.toString();
                                                Log.d("FACEBOOK", facebook_graph_respones);
                                                String personPhotoUrl = "https://graph.facebook.com/" + userid + "/picture?type=large";
                                                try {
                                                    username = object.getString("name");
                                                    if (object.has("email")) {
                                                        usermail = object.getString("email");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("name", username);
                                                    jsonObject.put("email", usermail);
                                                    jsonObject.put("phone", "");
                                                    jsonObject.put("image_url", String.valueOf(personPhotoUrl));
                                                    jsonObject.put("social_id", social_id);
                                                    jsonObject.put("login_type", "2");
                                                    jsonObject.put("password", "");
                                                    jsonObject.put("gcmId", "");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                new socialloginCall(jsonObject).execute(DomainUrl.durl + "register");
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,link");
                                request.setParameters(parameters);
                                request.executeAsync();

                            } catch (Exception e) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopAnim();
                                    }
                                }, GlobalActivity.delaytime);
                                e.printStackTrace();
                            }*/
                            Log.e("onSuccess", "--------" + loginResult.getAccessToken());
                            Log.e("Token", "--------" + loginResult.getAccessToken().getToken());
                            Log.e("Permision", "--------" + loginResult.getRecentlyGrantedPermissions());
                            Profile profile = Profile.getCurrentProfile();
                            Log.e("ProfileDataNameF", "--" + profile.getFirstName());
                            Log.e("ProfileDataNameL", "--" + profile.getLastName());

                            Log.e("Image URI", "--" + profile.getLinkUri());

                            Log.e("OnGraph", "------------------------");
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            try {
                                                String id = object.getString("id");
                                                String name = object.getString("name");
                                                String email = object.getString("email");

                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject();
                                                    jsonObject.put("name", name);
                                                    jsonObject.put("email", email);
                                                    jsonObject.put("phone", "");
                                                    jsonObject.put("image_url", "http://graph.facebook.com/" + id + "/picture?type=large");
                                                    jsonObject.put("social_id", social_id);
                                                    jsonObject.put("login_type", "2");
                                                    jsonObject.put("password", "");
                                                    jsonObject.put("gcmId", "");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                new socialloginCall(jsonObject).execute(DomainUrl.durl + "register");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Log.e("GraphResponse", "-------------" + response.toString());
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,gender,birthday,email,picture,first_name");
                            request.setParameters(parameters);
                            request.executeAsync();

                        }

                        @Override
                        public void onCancel() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stopAnim();
                                }
                            }, GlobalActivity.delaytime);
                            LoginManager mLoginManager = LoginManager.getInstance();
                            mLoginManager.logOut();
                            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stopAnim();
                                }
                            }, GlobalActivity.delaytime);
                            Toast.makeText(getApplicationContext(), "Error occured try again some time !!!" + error.toString(), Toast.LENGTH_SHORT).show();
                        }


                    });
                }
                break;
            case R.id.googleplus:
                if (internetChecking.isConnectingToInternet()) {
                    startAnim();
                    signIn();
                }
                break;
            case R.id.forgot:
                Intent intent = new Intent(getApplicationContext(), ForgotpasswordActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
//            google_api_availability.getErrorDialog(this, connectionResult.getErrorCode(), request_code).show();
            return;
        }
    }


    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHashee:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHashe:", e.toString());
        }
    }

    private void Logincall(String username, String password) {
        try {
            JSONObject regJsonObject = new JSONObject();
            regJsonObject.put("username", username);
            regJsonObject.put("password", password);
            login_data = regJsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "Connect to internet and try again", Toast.LENGTH_SHORT).show();
        } else {
            EmailLoginTask mlogin = new EmailLoginTask(DomainUrl.login, login_data);
            if (!login_data.isEmpty()) {
                try {
                    String response = mlogin.execute().get();
                    Log.d("Result", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (result.equals("success")) {
                        String _userid = jsonObject.getString("id");
                        String _image = jsonObject.getString("image");
                        String _phone = jsonObject.getString("phone");
                        String _email = jsonObject.getString("email");
                        String _name = jsonObject.getString("name");

                        editor = preferences.edit();
                        editor.putString("ipredict_userid", _userid);
                        editor.putString("ipredict_userimage", _image);
                        editor.putString("phone", _phone);
                        editor.putString("email", _email);
                        editor.putString("ipredict_name", _name);
                        editor.apply();

                        Intent intent1 = new Intent(getApplicationContext(), MatchsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (result.equalsIgnoreCase("error")) {
                        String msg = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            stopAnim();
        }
    }

    public Boolean validation() {
        if (mUsername.length() == 0) {
            username.setError("Enter email address");
            return false;
        }

        if (!mUsername.matches(Util.EmailPattern)) {
            username.setError("Please check your email address");
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        stopAnim();
    }

    private class socialloginCall extends AsyncTask<String, Void, String> {
        String json;

        socialloginCall(JSONObject jsonObject) {
            json = String.valueOf(jsonObject);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(urls[0])
                    .post(body)
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject response = new JSONObject(s);
                String result = response.getString("result");

                if (result.equals("success")) {
                    String userid = response.getString("id");
                    String image = response.getString("image");
                    String phone = response.getString("phone");
                    String email = response.getString("email");
                    String name = response.getString("name");
//                    String _questionid = response.getString("question_id");
//                    String _playid = response.getString("play_id");

                    editor = preferences.edit();
                    editor.putString("ipredict_userid", userid);
                    editor.putString("ipredict_userimage", image);
                    editor.putString("phone", phone);
                    editor.putString("email", email);
                    editor.putString("ipredict_name", name);
//                    editor.putString("questionid", _questionid);
//                    editor.putString("playid", _playid);
                    editor.apply();

                    Intent intent1 = new Intent(getApplicationContext(), MatchsActivity.class);
                    startActivity(intent1);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopAnim();
        }

    }
}
