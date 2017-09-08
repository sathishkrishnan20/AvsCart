package com.avs.online.avscart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.avs.online.avscart.Util.Network;

import static android.text.TextUtils.isEmpty;

public class GetShipmentDetailsForCheckout extends AppCompatActivity {

    private EditText quantity,mUserName, mMoileNo, mEmail,
            mAddresshomeNo,mAddressPlace, mAddressStreet, mAddressDistrict, maddressState, mAddressCountry, mAddressPinCode;

    Button makePaymentBtn;
    private String strName, strMobileNo, strEmail, strAddress;
    TextView prodTotalPrice;
    double totalPrice,productPrice = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_shipment_details_for_checkout);

        quantity = (EditText)findViewById(R.id.prod_quantity);
        prodTotalPrice = (TextView)findViewById(R.id.productTotalprice);
        mUserName = (EditText) findViewById(R.id.name1);
        mMoileNo = (EditText) findViewById(R.id.mobileNo1);
        mEmail = (EditText) findViewById(R.id.email1);


        mAddresshomeNo = (EditText) findViewById(R.id.userAddressHomeNo);
        mAddressPlace = (EditText) findViewById(R.id.userAddressPlace);
        mAddressStreet = (EditText) findViewById(R.id.userAddressStreet);
        mAddressDistrict = (EditText) findViewById(R.id.userAddressDistrict);
        maddressState = (EditText) findViewById(R.id.userAddressState);
        mAddressCountry = (EditText) findViewById(R.id.userAddressCountry);
        mAddressPinCode = (EditText) findViewById(R.id.userAddressPincode);

        Intent getIntent = getIntent();

        TextView txtview = (TextView) findViewById(R.id.prod_name_checkout);
        txtview.setText(getIntent.getStringExtra("productInfo"));
        productPrice = Double.parseDouble(getIntent.getStringExtra("productPrice"));
        totalPrice = productPrice;
        prodTotalPrice.setText(String.valueOf(totalPrice));

        makePaymentBtn = (Button) findViewById(R.id.registerBtn1);

        makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                validationCheck();
            }
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!isEmpty(quantity.getText())) {
                    totalPrice = productPrice * Integer.parseInt(quantity.getText().toString());
                    prodTotalPrice.setText(String.valueOf(totalPrice));
                }
            }
        });
    }

    public void validationCheck() {

        strName = mUserName.getText().toString().trim();
        strMobileNo = mMoileNo.getText().toString().trim();
        strEmail = mEmail.getText().toString().trim();

        String strHomeNo = mAddresshomeNo.getText().toString().trim();
        String strStreet = mAddressStreet.getText().toString().trim();
        String strPlace = mAddressPlace.getText().toString().trim();
        String strDistrict = mAddressDistrict.getText().toString().trim();

        String strState = maddressState.getText().toString().trim();
        String strCountry = mAddressCountry.getText().toString().trim();
        String strPincode = mAddressPinCode.getText().toString().trim();

        Network network = new Network();
        if (!network.isOnline(GetShipmentDetailsForCheckout.this)) {
            Toast.makeText(GetShipmentDetailsForCheckout.this, "No Network Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strStreet.isEmpty())
            strStreet = "Null";

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(GetShipmentDetailsForCheckout.this, R.id.name1, "[a-zA-Z .]+", R.string.err_name);
        mAwesomeValidation.addValidation(GetShipmentDetailsForCheckout.this, R.id.userAddressPlace, "[a-zA-Z .]+", R.string.err_address_place);
        mAwesomeValidation.addValidation(GetShipmentDetailsForCheckout.this, R.id.userAddressDistrict, "[a-zA-Z .]+", R.string.err_address_district);
        mAwesomeValidation.addValidation(GetShipmentDetailsForCheckout.this, R.id.userAddressState, "[a-zA-Z .]+", R.string.err_address_state);
        mAwesomeValidation.addValidation(GetShipmentDetailsForCheckout.this, R.id.userAddressCountry, "[a-zA-Z .]+", R.string.err_address_country);


        if (strName.isEmpty()) {
            Toast.makeText(this, "Please enter Valid Name", Toast.LENGTH_LONG).show();
            return;
        }
        if (strMobileNo.isEmpty()) {
            Toast.makeText(this, "Please enter the Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }
        if (strPincode.isEmpty()) {
            Toast.makeText(this, "Please enter the PinCode", Toast.LENGTH_LONG).show();
            return;
        }
        if (strHomeNo.isEmpty()) {
            Toast.makeText(this, "Please enter Home Number", Toast.LENGTH_LONG).show();
            return;
        }
        if (strStreet.isEmpty()) {
            Toast.makeText(this, "Please enter Street Name", Toast.LENGTH_LONG).show();
            return;
        }


        strAddress = strHomeNo + "," + strStreet + "," +strPlace + "," + strDistrict + "," + strState + "," +strCountry + ","+strPincode;

        if(mAwesomeValidation.validate()) {

            Intent intent = new Intent(GetShipmentDetailsForCheckout.this, CheckoutWebView.class);
            Intent getIntent = getIntent();
            intent.putExtra("firstName", strName);
            intent.putExtra("email", strEmail);
            intent.putExtra("mobileNo", strMobileNo);
            intent.putExtra("productInfo" , getIntent.getStringExtra("productInfo"));
            intent.putExtra("productId" , getIntent.getStringExtra("productId"));
            intent.putExtra("amount", String.valueOf(totalPrice));
            intent.putExtra("address", strAddress);
            startActivity(intent);
        }
    }




}
