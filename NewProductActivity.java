package com.zamer.zamer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zamer.parser.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends Activity {

    private ProgressDialog pDialog;

    SessionManager session;

    JSONParser jsonParser = new JSONParser();
    TextView txtName;
    TextView txtPrice;
    TextView txtDesc;
    TextView txtMan;
    EditText txtMetka;
    TextView txtFloor;
    TextView txtTelz;
    TextView txtImena;
    TextView txtDen;
    EditText txtTim;
    EditText txtCompany;
    TextView txtOderstates;
    EditText txtPaystates;
    EditText txtIP;
    EditText txtUrl;
    Button b;
    String mn;
    private static String url_create_product = "http://zamer.com.ua/phpapi/api";

    private static final String TAG_SUCCESS = "success";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        Intent i = getIntent();
        mn = i.getStringExtra("man");

        txtName = (EditText) findViewById(R.id.txtName);
        txtImena = (EditText) findViewById(R.id.txtImena);
        txtDesc = (EditText) findViewById(R.id.txtDesc);
        txtTelz = (EditText) findViewById(R.id.txtTelz);
        //txtDen = (EditText) findViewById(R.id.txtDen);
        //txtTim = (EditText) findViewById(R.id.txtTim);
        txtFloor = (EditText) findViewById(R.id.txtFloor);
        txtMetka = (EditText) findViewById(R.id.txtMetka);

        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new CreateNewProduct().execute();
            }
        });
    }




    /**
     * Фоновый Async Task создания нового продукта
     **/
    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Перед согданием в фоновом потоке показываем прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewProductActivity.this);
            pDialog.setMessage("Создание заявки...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Создание продукта
         **/
        protected String doInBackground(String... args) {
            // получаем обновленные данные с EditTexts
            String name = txtName.getText().toString();
            //String tim = txtTim.getText().toString();
            String description = txtDesc.getText().toString();
            String ime= txtImena.getText().toString();
            String tel = txtTelz.getText().toString();
            String floor = txtFloor.getText().toString();
            //String den = txtDen.getText().toString();
            String me = txtMetka.getText().toString();

           // String mn="slon";

            // формируем параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            //params.add(new BasicNameValuePair(TAG_TIM, tim));
            params.add(new BasicNameValuePair("description", description));
            params.add(new BasicNameValuePair("ime", ime));
            params.add(new BasicNameValuePair("floor", floor));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("tel", tel));
            params.add(new BasicNameValuePair("description", description));
            params.add(new BasicNameValuePair("metka", me));
            params.add(new BasicNameValuePair("mn", mn));



            // получаем JSON объект
            JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);

            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт удачно создан
                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    startActivity(i);

                    // закрываем это окно
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * После оконачния скрываем прогресс диалог
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }

    }

}
