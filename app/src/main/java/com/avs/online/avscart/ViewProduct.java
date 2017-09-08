package com.avs.online.avscart;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class ViewProduct extends AppCompatActivity {

    TextView prodName, prodPrice, prodDesc; // prodTotalPrice;
    ImageView prodImage;
    //EditText quantity;
    String getProductDetailsURL = NetworkUtil.URL + "/viewProductById.php";
    double productPrice = 0;
    //double totalPrice = 0.0;
    int prodId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        prodName = (TextView)findViewById(R.id.view_prod_name);
        prodDesc= (TextView)findViewById(R.id.view_prod_desc);
        prodPrice = (TextView)findViewById(R.id.view_prod_price);
        prodImage = (ImageView)findViewById(R.id.view_prod_image);
        prodId = getIntent().getIntExtra("prodId",0);
        getProductDetailsByProdId(prodId);


            Button nbutton = (Button) findViewById(R.id.ProceedToCheckout_bt);
            nbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                    Intent intent = new Intent(getApplicationContext(), GetShipmentDetailsForCheckout.class);
                    intent.putExtra("productInfo",prodName.getText());
                    intent.putExtra("productId",String.valueOf(prodId));
                    intent.putExtra("productPrice",String.valueOf(productPrice));
                        startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

    }

    private void getProductDetailsByProdId(final int productId) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, getProductDetailsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                {
                @Override
                protected Map<String,String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", String.valueOf(productId));
                    return params;
                }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);
    }


    private void showJson(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int resultCount = 0; resultCount < result.length(); resultCount ++) {
                JSONObject prodDetails = result.getJSONObject(resultCount);

                int valueMasterId = prodDetails.getInt("valueMasterKey");
                String value = prodDetails.getString("value");
                switch (valueMasterId){
                    case 1:
                        prodName.setText(value);
                        break;
                    case 2:
                        prodDesc.setText(value);
                        break;
                    case 4:
                        prodPrice.setText(value);
                        productPrice = Double.parseDouble(value);
                        break;
                    case 5:
                        Picasso.with(getApplicationContext()).load(value).error(R.drawable.error).placeholder(R.drawable.placeholder).resize(600,360).into(prodImage); //this is optional the image to display while the url image is downloading.error(0)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
