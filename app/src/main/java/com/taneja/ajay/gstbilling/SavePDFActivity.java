package com.taneja.ajay.gstbilling;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.taneja.ajay.gstbilling.data.GSTBillingContract;
import com.taneja.ajay.gstbilling.utils.NumberToWord;
import com.taneja.ajay.gstbilling.utils.PDFUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SavePDFActivity extends AppCompatActivity {

    private static Activity thisActivity;

    private static final int MAX_ITEMS_PER_PAGE = 32;
    private static Cursor pdfCursor;
    private static int totalItems;
    private static int itemsShown;
    private static PDFUtils pdfMaker;
    private static boolean moreItemsPresent;

    private static int totalPages;
    private static int pageNumber;

    private static String customerName;
    private static String customerContact;
    private static String gstNumber;
    private static String mot;
    private static String vehNumber;
    private static String uniqueId;
    private static String billId;
    private static String invoiceNumber;

    private TextView customerNameTv;
    private TextView customerContactTv;
    private TextView invoiceTv;
    private TextView gstNumberTv;
    private TextView motTv;
    private TextView uniqueIdTv;
    private TextView vehNumberTv;
    private TextView invoiceDateTv;
    private TextView businessNameTv;
    private TextView businessGSTTv;
    private TextView businessAddressTv;
    private TextView authorisedSignatoryTv;
    private TextView businessContactTv;
    private static TextView totalQtyTv;
    private static TextView totalTaxableValueTv;
    private static TextView totalCgstTv;
    private static TextView totalSgstTv;
    private static TextView totalAmountBeforeTaxTv;
    private static TextView totalTaxAmountTv;
    private static TextView totalAmountAfterTaxTv;
    private static TextView totalAmountInWordsTv;
    private static TextView billPageNumber;

    private RecyclerView pdfRecyclerView;
    private static SavePDFAdapter adapter;

    private static ContentResolver contentResolver;

    private Intent getPDFIntent;

    private static String inr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_pdf);

        thisActivity = this;

        getPDFIntent = getIntent();
        customerName = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_NAME);
        customerContact = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_PHONE_NUMBER);
        gstNumber = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_GSTIN);
        mot = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_MOT);
        vehNumber = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_VEHNO);
        uniqueId = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_UNQID);
        billId = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry._ID);
        totalItems = getPDFIntent.getIntExtra(DetailActivity.ITEM_COUNT_KEY, 0);
        invoiceNumber = getPDFIntent.getStringExtra(GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_INVOICENO);
        totalPages = (int) Math.ceil((double)totalItems/MAX_ITEMS_PER_PAGE);
        pageNumber = 0;

        customerNameTv = (TextView) findViewById(R.id.pdf_customer_name);
        customerContactTv = (TextView) findViewById(R.id.pdf_customer_contact);
        invoiceTv = (TextView) findViewById(R.id.pdf_customer_invoice_no_);
        gstNumberTv = (TextView) findViewById(R.id.pdf_customer_gst);
        motTv = (TextView) findViewById(R.id.pdf_mot);
        vehNumberTv = (TextView) findViewById(R.id.pdf_veh_no);
        uniqueIdTv = (TextView) findViewById(R.id.pdf_uniqueId);
        invoiceDateTv = (TextView) findViewById(R.id.pdf_invoice_date);
        businessNameTv = (TextView) findViewById(R.id.pdf_business_name);
        businessGSTTv = (TextView) findViewById(R.id.pdf_business_gst);
        businessAddressTv = (TextView) findViewById(R.id.pdf_business_address);
        authorisedSignatoryTv = (TextView) findViewById(R.id.pdf_authorised_signatory);
        businessContactTv = (TextView) findViewById(R.id.pdf_business_contact);
        totalQtyTv = (TextView) findViewById(R.id.pdf_total_qty);
        totalTaxableValueTv = (TextView) findViewById(R.id.pdf_total_taxable_value);
        totalCgstTv = (TextView) findViewById(R.id.pdf_total_cgst);
        totalSgstTv = (TextView) findViewById(R.id.pdf_total_sgst);
        totalAmountBeforeTaxTv = (TextView) findViewById(R.id.pdf_total_amount_before_tax);
        totalTaxAmountTv = (TextView) findViewById(R.id.pdf_total_gst);
        totalAmountAfterTaxTv = (TextView) findViewById(R.id.pdf_total_amount_after_tax);
        totalAmountInWordsTv = (TextView) findViewById(R.id.pdf_total_amount_in_words);
        billPageNumber = (TextView) findViewById(R.id.pdf_bill_page_number);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        businessNameTv.setText(prefs.getString(SetupPasswordActivity.SETUP_BUSINESS_NAME_KEY, getString(R.string.app_name)));
        businessGSTTv.setText(prefs.getString(SetupPasswordActivity.SETUP_BUSINESS_GST_KEY, "" ));

        businessAddressTv.setText(prefs.getString(SetupPasswordActivity.SETUP_BUSINESS_ADDRESS_KEY, "Address"));
        String authorisedSignatory = "For " + prefs.getString(SetupPasswordActivity.SETUP_BUSINESS_NAME_KEY, getString(R.string.app_name));
        authorisedSignatoryTv.setText(authorisedSignatory);
        businessContactTv.setText(prefs.getString(SetupPasswordActivity.SETUP_BUSINESS_CONTACT_KEY, ""));

        customerNameTv.setText(customerName);
        customerContactTv.setText(customerContact);
        gstNumberTv.setText(gstNumber);
        invoiceTv.setText(invoiceNumber);
        motTv.setText(mot);
        vehNumberTv.setText(vehNumber);
        uniqueIdTv.setText(uniqueId);



        Cursor billDateCursor = getContentResolver().query(
                GSTBillingContract.GSTBillingEntry.CONTENT_URI,
                new String[]{GSTBillingContract.GSTBillingEntry.PRIMARY_COLUMN_DATE},
                GSTBillingContract.GSTBillingEntry._ID + "=" + billId,
                null,
                null
        );
        billDateCursor.moveToFirst();
        String billDate = billDateCursor.getString(0);
        invoiceDateTv.setText(billDate);

        inr = getString(R.string.inr) + " ";

        contentResolver = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pdfMaker = new PDFUtils();
        }

        itemsShown = 0;
        pdfCursor = updatePdfCursor();

        pdfRecyclerView = (RecyclerView) findViewById(R.id.pdf_recycler_view);
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pdfRecyclerView.setHasFixedSize(true);
        adapter = new SavePDFAdapter(this, pdfCursor);
        pdfRecyclerView.setAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void automaticSavePDF() {
        pdfMaker.addPageToPDF(thisActivity.findViewById(R.id.pdf_view));
        if(moreItemsPresent){
            pdfCursor = updatePdfCursor();
            adapter.swapCursor(pdfCursor);
        }else {
            pdfMaker.createPdf(thisActivity, invoiceNumber + " " + customerName);
            thisActivity.finish();
        }
    }

    private static Cursor updatePdfCursor() {
        Cursor cursor = contentResolver.query(
                GSTBillingContract.GSTBillingEntry.CONTENT_URI.buildUpon().appendPath(billId).build(),
                null,
                GSTBillingContract.GSTBillingCustomerEntry._ID + " between " + (itemsShown + 1) + " and " + (itemsShown + MAX_ITEMS_PER_PAGE),
                null,
                GSTBillingContract.GSTBillingCustomerEntry._ID
        );
        itemsShown += MAX_ITEMS_PER_PAGE;
        moreItemsPresent = areMoreItemsPresent();
        setBillPageNumber();
        return cursor;
    }

    public static boolean areMoreItemsPresent(){
        if((totalItems - itemsShown) > 0){
            return true;
        }else {
            return false;
        }
    }

    private static void setBillPageNumber(){
        pageNumber++;
        billPageNumber.setText("Page " + pageNumber + " of " + totalPages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_pdf, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_save_pdf_file:
                automaticSavePDF();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void printTotalDetails(int totalQty, float totalTaxableValue, float totalSingleGst, float totalAmount){
        totalQtyTv.setText(String.valueOf(totalQty));
        totalTaxableValueTv.setText(String.format("%.2f", totalTaxableValue));
        totalCgstTv.setText(String.format("%.2f", totalSingleGst));
        totalSgstTv.setText(String.format("%.2f", totalSingleGst));
        totalAmountBeforeTaxTv.setText(inr + String.format("%.2f", totalTaxableValue));
        totalTaxAmountTv.setText(inr + String.format("%.2f", (totalSingleGst+totalSingleGst)));
        totalAmountAfterTaxTv.setText(inr + String.format("%.2f", totalAmount));
        totalAmountInWordsTv.setText("Rupees. " + NumberToWord.getNumberInWords(String.valueOf((int)totalAmount)));

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                automaticSavePDF();
            }
        }, 1000);

    }

    public static String billNumberGenerator(){
        String billNumber = null;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        year = 2020 - year + 65;
        String syear = Character.toString((char) year);
        month = month + 65;
        String smonth = Character.toString((char) month);
        hour = hour + 65;
        String shour = Character.toString((char) hour);

        billNumber = syear + min + smonth + sec + shour  + day  ;
        return billNumber;
    }


}
