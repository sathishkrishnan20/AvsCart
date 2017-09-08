package com.avs.online.avscart.Util;

/**
 * Created by SATHISH on 9/2/2017.
 */

public class AppPayUmoneyConfig {

    public AppPayUmoneyConfig() {

    }
    public String merchant_Key() {

        return "rjQUPktU";
        //return "VUHDB0K1";
    }


    public String merchant_ID() {
        return "5864029";
    }

    public String chekoutUrl() {
        return "https://test.payu.in/_payment";
        //return "https://secure.payu.in/_payment";
    }
    public String furl() {
        return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
    }

    public String surl() {
        return "https://www.payumoney.com/mobileapp/payumoney/success.php";
    }

    public String salt() {
        return "e5iIg1jwi8";
        //return "SPk2TAnL3r";
    }

    public boolean debug() {
        return true;
        //return false;
    }

}
