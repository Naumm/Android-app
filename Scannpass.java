package com.zamer.zamer;

import android.util.Log;

import com.zamer.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 19.01.2017.
 */
public class Scannpass {

    private static String url_proverka = "http://zamer.com.ua/api";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PASS = "pass";
    private static final String TAG_SID = "login";
    private static final String TAG_SOLT = "solt";


    JSONParser jsonParser = new JSONParser();

    public HashMap<String, String> getUserServer(String username){

        HashMap<String, String> userserv = new HashMap<String, String>();
        // проверяем статус success тега
        int success;
        try {

            // Список параметров
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", username));

            // получаем продукт по HTTP запросу
            JSONObject json = jsonParser.makeHttpRequest(url_proverka, "POST", params);

            Log.d("Single Product Details", json.toString());

            success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // Успешно получинна детальная информация о продукте
                JSONArray productObj = json.getJSONArray(TAG_PRODUCT);

                // получаем первый обьект с JSON Array
                JSONObject product = productObj.getJSONObject(0);

                // продукт с pid найден
                String pass = product.getString(TAG_PASS);
                String s = product.getString(TAG_SOLT);
                String sid = product.getString(TAG_SID);

                userserv.put(TAG_PASS, pass);

                // user email id
                userserv.put(TAG_SOLT, s);

                userserv.put(TAG_SID, sid);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userserv;
    }
}

