package com.zamer.zamer;

import java.util.ArrayList;
import java.util.List;

import com.zamer.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditProductActivity extends Activity {

    TextView txtName;
    TextView txtPrice;
    TextView txtDesc;
    TextView txtMan;
    TextView txtMetka;
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
    Button btnSave;
    Button btnDelete;
    Button zvonok;

    String pid;


    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url для получения одного продукта
    private static final String url_product_detials = "http://zamer.com.ua/api";

    // JSON параметры
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_MAN = "man";
    private static final String TAG_METKA = "metka";
    private static final String TAG_FLOOR = "floor";
    private static final String TAG_TELZ = "telz";
    private static final String TAG_IMENA = "imena";
    private static final String TAG_DEN = "den";
    private static final String TAG_TIM = "tim";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_ODERSTATES = "oderstates";
    private static final String TAG_PAYSTATES = "paystates";
    private static final String TAG_IP = "ip";
    private static final String TAG_URL = "url";
    public String mapas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        zvonok = (Button) findViewById(R.id.zvonok);

        // показываем форму про детальную информацию о продукте
        Intent i = getIntent();

        // получаем id продукта (pid) с формы
        pid = i.getStringExtra(TAG_PID);

        // Получение полной информации о продукте в фоновом потоке
        new GetProductDetails().execute();

        // обрабочик на кнопку сохранение продукта
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // запускаем выполнение задачи на обновление продукта
                //new SaveProductDetails().execute();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(("geo:0,0?q=" + mapas)));
                startActivity(i);


            }
        });

        // обработчик на кнопку удаление продукта
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Запускаем новый intent который покажет нам Activity
                Intent in = new Intent(getApplicationContext(), Reedite.class);
                // отправляем pid в следующий activity
                in.putExtra(TAG_PID, pid);

                // запуская новый Activity ожидаем ответ обратно
                startActivity(in);
                finish();
            }
        });

        zvonok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // запускаем выполнение задачи на обновление продукта
                //new SaveProductDetails().execute();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:+380971494275"));
                startActivity(i);


            }
        });


    }




    /**
     * Фоновая асинхронная задача для получения полной информации о продукте
     **/
    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Перед началом показать в фоновом потоке прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Получение детальной информации о продукте в фоновом режиме
         **/
        protected String doInBackground(String[] params) {

            // обновляем UI форму
            runOnUiThread(new Runnable() {
                public void run() {
                    // проверяем статус success тега
                    int success;
                    try {
                        // Список параметров
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("pid", pid));

                        // получаем продукт по HTTP запросу
                        JSONObject json = jsonParser.makeHttpRequest(url_product_detials, "GET", params);

                        Log.d("Single Product Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // Успешно получинна детальная информация о продукте
                            JSONArray productObj = json.getJSONArray(TAG_PRODUCT);

                            // получаем первый обьект с JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // продукт с pid найден
                            // Edit Text
                            txtName = (TextView) findViewById(R.id.inputName);
                            txtPrice = (TextView) findViewById(R.id.inputPrice);
                            txtDesc = (TextView) findViewById(R.id.inputDesc);
                            txtTelz = (TextView) findViewById(R.id.inputTelz);
                            txtDen = (TextView) findViewById(R.id.inputDen);
                            txtMetka = (TextView) findViewById(R.id.inputMetka);
                            mapas = (product.getString(TAG_NAME));
                            txtFloor = (TextView) findViewById(R.id.inputFloor);
                            txtOderstates = (TextView) findViewById(R.id.inputOderstate);


                            String n = product.getString(TAG_IMENA);
                            String d = product.getString(TAG_DEN);
                            String t = product.getString(TAG_TIM);
                            // покаываем данные о продукте в EditText
                            txtMetka.setText(product.getString(TAG_METKA));
                            txtName.setText("Адрес: " + product.getString(TAG_NAME));
                            txtFloor.setText("Квартира, этаж: " + product.getString(TAG_FLOOR));

                            txtPrice.setText("Цена: " + product.getString(TAG_PRICE));
                            txtDesc.setText("Коментарий: " + product.getString(TAG_DESCRIPTION));
                            txtTelz.setText(product.getString(TAG_TELZ) + " " + n);
                            txtDen.setText("Когда: " + product.getString(TAG_DEN) + " " + product.getString(TAG_TIM));
                            txtOderstates.setText("Cтатус: " + product.getString(TAG_ODERSTATES));

                            //txtMan.setText(product.getString(TAG_MAN) + " " + product.getString(TAG_COMPANY));

                        } else {
                            // продукт с pid не найден
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем диалог прогресс
         **/
        protected void onPostExecute(String file_url) {
            // закрываем диалог прогресс
            pDialog.dismiss();
        }
    }

}
