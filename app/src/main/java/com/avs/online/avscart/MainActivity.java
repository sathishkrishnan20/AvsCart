package com.avs.online.avscart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static String getProductURL = NetworkUtil.URL+ "/viewProductLst.php";
    private  static  String getCategoriesURL = NetworkUtil.URL + "/getCategories.php";
    Spinner category;
    ImageView[] mImages;
    LinearLayout layout ;
    ScrollView mScrollViewImage;// = new ScrollView();
    ProgressBar productsProgressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout = (LinearLayout) findViewById(R.id.productLinearLayout);
        productsProgressBar = (ProgressBar)findViewById(R.id.products_progress_bar);
        mScrollViewImage = (ScrollView)findViewById(R.id.scrollView);
        category = (Spinner)findViewById(R.id.prod_category_spinner);
        TextView helloUser = (TextView)findViewById(R.id.helloUser);

        StringBuilder hello = new StringBuilder();
        hello.append("Hello ");
        helloUser.setText(hello.toString());
        SharedPreferences userdetails = getApplicationContext().getSharedPreferences("Login", 0);
        if (userdetails.getBoolean("isLogin", false)) {
            hello.append(userdetails.getString("name", null));
            helloUser.setText(hello.toString());
        }
        getCategories();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                  getProducts(category.getSelectedItem().toString());
             }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

          }

    public void getCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getCategoriesURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        showCategoriesJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(stringRequest);
    }

    ArrayAdapter prodArrayAdapter;
    private void showCategoriesJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray categoryResult = jsonObject.getJSONArray("result");
            ArrayList categoryArrayList = new ArrayList();
            categoryArrayList.add("All");
            for (int productCount = 0; productCount < categoryResult.length(); productCount++) {
                try {
                    JSONObject json = categoryResult.getJSONObject(productCount);
                    categoryArrayList.add(json.getString("categoryName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                prodArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryArrayList);
                prodArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(prodArrayAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void getProducts(final String categoryName) {
        layout.removeAllViews();
        productsProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getProductURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                productsProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }){
            @Override
            protected Map<String,String> getParams()
            {
                Map <String,String> params=new HashMap<String,String>();
                params.put("categorySearch",categoryName);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(stringRequest);
    }

    private void showJSON(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            layout.setOrientation(LinearLayout.VERTICAL);
            mImages = new ImageView[result.length()];

            for (int productCount = 0; productCount < result.length(); productCount++) {
                try {
                    JSONObject json = result.getJSONObject(productCount);
                    String imageURL = json.getString("ImageURL");
                    mImages[productCount] = new ImageView(this);
                    Picasso.with(getApplicationContext()).load(imageURL).error(R.drawable.error).placeholder(R.drawable.placeholder).resize(600,360).into(mImages[productCount]); //this is optional the image to display while the url image is downloading.error(0)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    mImages[productCount].setId(json.getInt("id"));
                    layout.addView(mImages[productCount]);

                    mImages[productCount].setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent viewProductIntent  = new Intent(getApplicationContext(), ViewProduct.class);
                            viewProductIntent.putExtra("prodId",v.getId());
                            startActivity(viewProductIntent);
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            productsProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login_nav) {
            Intent loginActivity = new Intent(MainActivity.this, ProfilePage.class);
            startActivity(loginActivity);
        }/* else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } */

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
