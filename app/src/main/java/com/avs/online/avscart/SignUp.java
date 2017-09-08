package com.avs.online.avscart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.Signature;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avs.online.avscart.Util.Network;
import com.basgeekball.awesomevalidation.AwesomeValidation;

import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignUp extends AppCompatActivity {

    private AutoCompleteTextView mCountryCodeView;
    private EditText mUserNameView,mEmailVie, mMobileNoView, mPasswordView;

    Button register;

    private static String signUpUrl = NetworkUtil.URL+"/signup.php";

    private static String dname="name";
    private static String demail="email";
    private static String dphone="phone";
    private static String dpassword="password";

    private String name,phone,email, countryCode,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserNameView = (EditText) findViewById(R.id.name);
        mMobileNoView = (AutoCompleteTextView) findViewById(R.id.mobileNo_sign_up);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailVie =(EditText) findViewById(R.id.email_sign_up);
        register = (Button) findViewById(R.id.email_sign_up_button);
        mCountryCodeView = (AutoCompleteTextView) findViewById(R.id.countryCode);


        mCountryCodeView.setText(getCountryCode());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateFields();
            }
        });
    }

    String CountryZipCode="";
    private String getCountryCode() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String CountryID= tm.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for (int i=0; i<rl.length; i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    private void validateFields() {
        name= mUserNameView.getText().toString().trim();
        email = mEmailVie.getText().toString().trim();
        phone = mMobileNoView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();
        countryCode =mCountryCodeView.getText().toString().trim();


        if(email.substring(0,1).equals("0")) {
            email = email.substring(1);
        }


        Network network=new Network();
        if (!network.isOnline(SignUp.this)) {
            Toast.makeText(SignUp.this, "No Network Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(SignUp.this, R.id.name, "[a-zA-Z .]+", R.string.err_name);

        try {
            if (countryCode.length() > 0 ) {


            } else {

                mMobileNoView.setError("Please Enter valid Mobile No");
                return;
            }
        } catch (Exception e)
        {
            mCountryCodeView.setError("Please Enter valid Country Code");
            return;
        }

        if(password.length() < 6 || password.length() > 16 )
        {
            mPasswordView.setError("Password Length Should be 6 to 16 characters");
            return;
        }

        if (name.isEmpty() && email.isEmpty()) {

            Toast.makeText(getApplicationContext(), "please Enter Crediantilas", Toast.LENGTH_LONG).show();
            return;
        }
        if(mAwesomeValidation.validate()) {
            registerUser();
        }

    }

    int progressStatus=1;
    boolean isCanceled =false;

    public void registerUser()
    {
        final ProgressDialog loading =new ProgressDialog(SignUp.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setTitle("Please Wait..");
        loading.setMessage("Loading.........");
        //pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        loading.setIndeterminate(false);
        loading.setCancelable(false);

        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            // Set a click listener for progress dialog cancel button
            @Override
            public void onClick(DialogInterface dialog, int which){
                progressStatus = 0;
                isCanceled = true;
                loading.dismiss();
            }
        });

        loading.show();
        if(isCanceled) {
            progressStatus = 1;
            return;
        }
        StringRequest stringRequest=new StringRequest(Request.Method.POST, signUpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.dismiss();

                        Toast.makeText(SignUp.this,response.split(";")[0],Toast.LENGTH_SHORT).show();
                        resetField();
                        Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        loading.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();

                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                        resetField();

                    }
                })
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map <String,String> params=new HashMap<String,String>();
                params.put(dname,name);
                params.put(demail,email);
                params.put(dphone, phone);
                params.put(dpassword,password);

                return params;
            }


        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);

    }
    private void resetField() {
        mEmailVie.setText("");
        mPasswordView.setText("");
        mMobileNoView.setText("");
        mUserNameView.setText("");
    }
}
