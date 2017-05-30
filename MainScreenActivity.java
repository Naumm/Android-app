package com.zamer.zamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainScreenActivity extends Activity {

    Button btnViewProducts;
    Button btnNewProduct;
    Button btnLogout;




    // Session Manager Class
    SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Session class instance
        session = new SessionManager(getApplicationContext());
       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        session.checkLogin();

        TextView lblName = (TextView) findViewById(R.id.lblName);
       // TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
         final String man = user.get(SessionManager.KEY_NAME);

        // email
        final String email = user.get(SessionManager.KEY_EMAIL);

        lblName.setText(Html.fromHtml("Name: <b>" + man + "</b>"));
        //lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));


        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);

        // обработчик на нажатиЯ кнопки View Products
        btnViewProducts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Запускаем Activity вывода всех продуктов
                Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);

                startActivity(i);

            }
        });

        // обработчик на нажатия кнопки Add New Products
        btnNewProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Запускаем Activity создания нового продукта
                Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
                i.putExtra("man", man);
                startActivity(i);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });
    }
}
