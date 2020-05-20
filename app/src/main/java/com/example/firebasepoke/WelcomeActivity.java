package com.example.firebasepoke;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class WelcomeActivity extends AppCompatActivity {

    TextView name, email, id;
    EditText token, msg;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        name = findViewById(R.id.nameTv);
        email = findViewById(R.id.emailTv);
        id = findViewById(R.id.textView4);
        msg = findViewById(R.id.editText2);
        token = findViewById(R.id.editText);
        getUserProfile();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    public void getUserProfile() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText("Name : " + personName);
            email.setText("Email : " + personEmail);

            System.out.println(acct.getIdToken());
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("Error", "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            id.setText("Token: " + token);
                            System.out.println(token);

                        }
                    });
        }
    }

    public void sendMessage(View view) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("message", msg.getText().toString());
        sendPushToSingleInstance(this, hashMap, token.getText().toString());
    }

    public static void sendPushToSingleInstance(final Context activity, final HashMap dataValue, final String instanceIdToken) {

        final String url = "https://fcm.googleapis.com/fcm/send";
        StringRequest myReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, "Bingo Success", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Oops error", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                Map<String, Object> rawParameters = new Hashtable();
                rawParameters.put("data", new JSONObject(dataValue));
                rawParameters.put("to", instanceIdToken);
                return new JSONObject(rawParameters).toString().getBytes();
            }

            ;

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=" + "AAAAWakKgxU:APA91bEnp2JZ5n7DOnx1UHRqZ-dMUTvGWn-D_t1OGnoi-T8CjbJX07-eGyGjUuvcXgagGsYWZR97VOb_JmbPjnXHlNdcBmoNTq1aDA7htQIFsgEzHJaiq8FsGGVxXbuXfFVpGrwNQqfy");
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        Volley.newRequestQueue(activity).add(myReq);
    }

    public void signOut(View view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WelcomeActivity.this, "SignOut Success!!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }

                });
    }

}
