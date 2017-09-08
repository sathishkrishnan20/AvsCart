package com.avs.online.avscart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import org.json.JSONObject;

public class Products extends AppCompatActivity {

    private static String getProductURL = NetworkUtil.URL+ "/viewProductLst.php";

    JSONArray result = new JSONArray();
    ImageView[] mImages;
    LinearLayout layout ;
    ScrollView mScrollViewImage;// = new ScrollView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        layout = (LinearLayout) findViewById(R.id.productLinearLayout);
        mScrollViewImage = (ScrollView)findViewById(R.id.scrollView);
        getProducts();

    }



    public void getProducts() {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getProductURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"coming", Toast.LENGTH_LONG).show();
                        showJSON(response);
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



    private void showJSON(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            result = jsonObject.getJSONArray("result");
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
        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
