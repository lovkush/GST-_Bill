package com.taneja.ajay.gstbilling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class NewBillCustomerActivity extends AppCompatActivity {

    private EditText customerNameEt;
    private EditText phoneNumberEt;
    private EditText gstNumberEt;
    private EditText motEt;
    private EditText vehNumberEt;
    private EditText uniqueIdEt;

    public static final String ADD_CUSTOMER_NAME_KEY = "customerName";
    public static final String ADD_CUSTOMER_GST_KEY = "gstNumber";
    public static final String ADD_CUSTOMER_PHONE_KEY = "phoneNumber";
    public static final String ADD_CUSTOMER_MOT_KEY = "mot";
    public static final String ADD_CUSTOMER_VEHNO_KEY = "vehNumber";
    public static final String ADD_CUSTOMER_UNQID_KEY = "uniqueId" ;
    public static final String ADD_CUSTOMER_INVOICENO_KEY = "invoiceNo" ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill_customer);

        customerNameEt = (EditText) findViewById(R.id.customer_name_value);
        phoneNumberEt = (EditText) findViewById(R.id.phone_number_value);
        gstNumberEt = (EditText) findViewById(R.id.gst_value);
        motEt = (EditText) findViewById(R.id.mot_value);
        vehNumberEt = (EditText) findViewById(R.id.veh_no_value);
        uniqueIdEt = (EditText) findViewById(R.id.unique_id_value);


    }

    public void addCustomer(View view){
        String customerName = customerNameEt.getText().toString();
        String gstNumber = gstNumberEt.getText().toString();
        String phoneNumber = phoneNumberEt.getText().toString();
        String mot = motEt.getText().toString();
        String vehNumber = vehNumberEt.getText().toString();
        String uniqueId = uniqueIdEt.getText().toString();

        if(customerName.length() == 0){
            Toast.makeText(this, getString(R.string.enter_customer_name_error), Toast.LENGTH_SHORT).show();
            return;
        }


        if(phoneNumber.length() > 0 && phoneNumber.length() < 10){
            Toast.makeText(this, getString(R.string.invalid_phone_number_error), Toast.LENGTH_SHORT).show();
            return;
        }else if(phoneNumber.length() == 0){
            phoneNumber = "NA";
        }

       /* if(gstNumber.length() > 0 && gstNumber.length() < 15){
            Toast.makeText(this, getString(R.string.invalid_phone_number_error), Toast.LENGTH_SHORT).show();
            return;
        }else if(gstNumber.length() == 0){
            gstNumber = "NA";
        } */




        Intent intent = new Intent(this, NewBillActivity.class);
        intent.putExtra(ADD_CUSTOMER_NAME_KEY, customerName);
        intent.putExtra(ADD_CUSTOMER_GST_KEY, gstNumber);
        intent.putExtra(ADD_CUSTOMER_PHONE_KEY, phoneNumber);
        intent.putExtra(ADD_CUSTOMER_MOT_KEY, mot);
        intent.putExtra(ADD_CUSTOMER_INVOICENO_KEY,billNumberGenerator());
        intent.putExtra(ADD_CUSTOMER_VEHNO_KEY, vehNumber);
        intent.putExtra(ADD_CUSTOMER_UNQID_KEY, uniqueId);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_discard){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static String billNumberGenerator(){
        String billNumber = null;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        /*int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);*/
        year = 2020 - year + 65;
        String syear = Character.toString((char) year);
        month = month + 65;
        String smonth = Character.toString((char) month);
        //hour = hour + 65;
        //String shour = Character.toString((char) hour);

        billNumber = syear  + smonth   + day  ;
        return billNumber;
    }

}
