package br.com.resoluteit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import resoluteit.com.br.R;


public class LoginAct extends AppCompatActivity {

    Button btnLogin;

    EditText login;
    EditText senha;

    ProgressDialog ringProgressDialog;

    TextView registrarUsuarioBtn;

    TextView esqueciASenhaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    private void navToInicio() {

        Intent i = new Intent(this, LoginAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }


}
