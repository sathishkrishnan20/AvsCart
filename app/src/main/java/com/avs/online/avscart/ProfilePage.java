package com.avs.online.avscart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
    try {
        TextView userProfile = (TextView) findViewById(R.id.user_profile_name);
        TextView userPhNo = (TextView) findViewById(R.id.user_profile_ph_no);
        TextView userCart = (TextView) findViewById(R.id.user_cart);
        TextView userDetails = (TextView) findViewById(R.id.user_details);
        ImageView userEdit = (ImageView) findViewById(R.id.user_edit);
        TextView logOut = (TextView) findViewById(R.id.user_logout);

        final ProgressBar pg = (ProgressBar) findViewById(R.id.progPro);
        pg.setVisibility(View.GONE);

        SharedPreferences userdetails = getApplicationContext().getSharedPreferences("Login", 0);

        if (!userdetails.getBoolean("isLogin", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        String loginUserName, loginUserEmail;
        loginUserName = userdetails.getString("name", null);
        loginUserEmail = userdetails.getString("email", null);

        userProfile.setText(loginUserName);
        userPhNo.setText(loginUserEmail);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pg.setVisibility(View.VISIBLE);
                CountDownTimer countDownTimerStatic = new CountDownTimer(1000, 16) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {

                        SharedPreferences userdetails = getApplicationContext().getSharedPreferences("Login", 0);
                        SharedPreferences.Editor editor = userdetails.edit();
                        if (userdetails.contains("isLogin")) {
                            editor.remove("id");
                            editor.remove("name");
                            editor.remove("mobile");
                            editor.remove("email");
                            editor.remove("isLogin");
                            editor.apply();
                            boolean commit = editor.commit();
                            pg.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Logout Succesfully", Toast.LENGTH_SHORT).show();

                        } else {
                            pg.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Your are already Logged out", Toast.LENGTH_SHORT).show();
                        }


                    }
                };
                countDownTimerStatic.start();
            }
        });
    }catch (Exception e) {
        Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show();
    }
/*
        userCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Answers.class);
                startActivity(intent);

            }
        });

        userDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(), UserViewPoem.class);
                startActivity(intent);

            }
        });


        userEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent=new Intent(getApplicationContext(), UserUpdateDetails.class);
                startActivity(intent);

            }
        });

*/

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
