package com.zamer.zamer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zamer.parser.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllProductsActivity extends ListActivity {

    private ProgressDialog pDialog;
    SessionManager session;

    // Создаем JSON парсер
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url получения списка всех продуктов
    private static String url_all_products = "http://zamer.com.ua/api";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_METKA = "metka";
    private static final String TAG_PRICE = "price";

    // тут будет хранится список продуктов
    JSONArray products = null;

    String metk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_products);

        session = new SessionManager(getApplicationContext());
       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Загружаем продукты в фоновом потоке
        new LoadAllProducts().execute();

        // получаем ListView
        ListView lv = getListView();

        // на выбор одного продукта
        // запускается Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();




                // Запускаем новый intent который покажет нам Activity
                Intent in = new Intent(getApplicationContext(), EditProductActivity.class);
                // отправляем pid в следующий activity
                in.putExtra(TAG_PID, pid);

                // запуская новый Activity ожидаем ответ обратно
                startActivityForResult(in, 100);

            }
        });

    }

    // Ответ из Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если результующий код равен 100
        if (resultCode == 100) {
            // если полученный код результата равен 100
            // значит пользователь редактирует или удалил продукт
            // тогда мы перезагружаем этот экран
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Фоновый Async Task для загрузки всех продуктов по HTTP запросу
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllProductsActivity.this);
            pDialog.setMessage("Загрузка заявок. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Получаем все продукт из url
         * */
        protected String doInBackground(String... args) {

            // обновляем UI форму
            runOnUiThread(new Runnable() {
                public void run() {
                    // проверяем статус success тега

                    try {
                        HashMap<String, String> user = session.getUserDetails();

                        // name
                        final String name = user.get(SessionManager.KEY_NAME);
                        // Список параметров
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("sid", name));


                        JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

                        Log.d("All Products: ", json.toString());


                        // Получаем SUCCESS тег для проверки статуса ответа сервера
                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            // продукт найден
                            // Получаем масив из Продуктов
                            products = json.getJSONArray(TAG_PRODUCTS);

                            // перебор всех продуктов
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                // Сохраняем каждый json елемент в переменную
                                String id = c.getString(TAG_PID);
                                String name1 = c.getString(TAG_NAME);
                                 metk = c.getString(TAG_METKA);
                                String price = c.getString(TAG_PRICE);

                                // Создаем новый HashMap
                                HashMap<String, String> map = new HashMap<String, String>();

                                // добавляем каждый елемент в HashMap ключ => значение
                                map.put(TAG_PID, id);
                                map.put(TAG_NAME, name1);
                                map.put(TAG_METKA, metk);
                                map.put(TAG_PRICE, price);

                                // добавляем HashList в ArrayList
                                productsList.add(map);
                            }
                        } else {
                            // продукт не найден
                            // Запускаем Add New Product Activity
                            Intent i = new Intent(getApplicationContext(),
                                    NewProductActivity.class);
                            // Закрытие всех предыдущие activities
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllProductsActivity.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID,
                            TAG_NAME, TAG_METKA, TAG_PRICE},
                            new int[] { R.id.pid, R.id.name, R.id.metka, R.id.price });

                    // обновляем listview
                    setListAdapter(adapter);





                }
            });

        }

    }

}
