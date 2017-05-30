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

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Reedite extends Activity {

    TextView textView;
    EditText txtName;
    EditText txtPrice;
    EditText txtDesc;
    EditText txtMan;
    EditText txtMetka;
    EditText txtFloor;
    EditText txtTelz;
    EditText txtImena;
    EditText txtDen;
    EditText txtTim;
    EditText txtCompany;
    EditText txtOderstates;
    EditText txtPaystates;
    EditText txtIP;
    EditText txtUrl;
    Button btnSave;
    Button btnDelete;


    String pid;


    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url для получения одного продукта
    private static final String url_product_detials = "http://zamer.com.ua/api";

    // url для обновления продукта
    private static final String url_update_product = "http://zamer.com.ua/api";

    // url для удаления продукта
    private static final String url_delete_product = "http://zamer.com.ua/api";

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
    private static final String TAG_NAMER = "imena";
    private static final String TAG_DEN = "den";
    private static final String TAG_TIM = "tim";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_ODERSTATES = "oderstates";
    private static final String TAG_PAYSTATES = "paystates";
    private static final String TAG_IP = "ip";
    private static final String TAG_URL = "url";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_edit);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);


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
                                               new SaveProductDetails().execute();
                                           }
                                       });

        // обработчик на кнопку удаление продукта
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // удалем продукт в фоновом потоке
                new DeleteProduct().execute();

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
            pDialog = new ProgressDialog(Reedite.this);
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
                            txtName = (EditText) findViewById(R.id.txtName);
                            txtImena = (EditText) findViewById(R.id.txtImena);
                            txtDesc = (EditText) findViewById(R.id.txtDesc);
                            txtTelz = (EditText) findViewById(R.id.txtTelz);
                            //txtDen = (EditText) findViewById(R.id.txtDen);
                            //txtTim = (EditText) findViewById(R.id.txtTim);
                            txtFloor = (EditText) findViewById(R.id.txtFloor);
                            txtMetka = (EditText) findViewById(R.id.txtMetka);

                            // покаываем данные о продукте в EditText
                            txtName.setText(product.getString(TAG_NAME));
                            txtFloor.setText(product.getString(TAG_FLOOR));
                            txtMetka.setText(product.getString(TAG_METKA));
                            txtDesc.setText(product.getString(TAG_DESCRIPTION));
                            txtTelz.setText(product.getString(TAG_TELZ));
                            txtImena.setText(product.getString(TAG_NAMER));
                            //txtDen.setText(product.getString(TAG_DEN));
                           // txtTim.setText(product.getString(TAG_TIM));
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

    /**
     * В фоновом режиме выполняем асинхроную задачу на сохранение продукта
     **/
    class SaveProductDetails extends AsyncTask<String, String, String> {

        /**
         * Перед началом показываем в фоновом потоке прогрксс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Reedite.this);
            pDialog.setMessage("Сохранение заявки ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Сохраняем продукт
         **/
        protected String doInBackground(String[] args) {

            // получаем обновленные данные с EditTexts
            String name = txtName.getText().toString();
            //String tim = txtTim.getText().toString();
            String description = txtDesc.getText().toString();
            String ime = txtImena.getText().toString();
            String tel = txtTelz.getText().toString();
            String floor = txtFloor.getText().toString();
            //String den = txtDen.getText().toString();
            String met = txtMetka.getText().toString();

            // формируем параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            //params.add(new BasicNameValuePair(TAG_TIM, tim));
            params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));
            params.add(new BasicNameValuePair(TAG_NAMER, ime));
            params.add(new BasicNameValuePair(TAG_TELZ, tel));
            params.add(new BasicNameValuePair(TAG_FLOOR, floor));
            // params.add(new BasicNameValuePair("den", den));
            params.add(new BasicNameValuePair(TAG_METKA, met));

            Log.d("tag params", params.toString());

            // отправляем измененные данные через http запрос
            JSONObject json = jsonParser.makeHttpRequest(url_update_product, "POST", params);

            // проверяем json success тег

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Запускаем Activity вывода всех продуктов
                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);

                    startActivity(i);
                    finish();
                } else {
                    // продукт не обновлен
                }
                }catch(JSONException e){
                    e.printStackTrace();
                }


            return null;
        }

        /**
         * После окончания закрываем прогресс диалог
         **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.dismiss();
        }
    }

    /**
     * Фоновая асинхронная задача на удаление продукта
     **/
    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * На начале показываем прогресс диалог
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Reedite.this);
            pDialog.setMessage("Удаляем заявку...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Удаление продукта
         **/
        protected String doInBackground(String[] args) {

            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // получение продукта используя HTTP запрос
                JSONObject json = jsonParser.makeHttpRequest(url_delete_product, "POST", params);

                Log.d("Delete Product", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Продукт удачно удален
                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    // отправляем результирующий код 100 для уведомления об удалении продукта
                    setResult(100, i);
                    startActivity(i);
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
