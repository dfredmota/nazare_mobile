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

import br.com.resoluteit.delegate.LoginDelegate;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.task.LoginTask;
import br.com.resoluteit.util.Data;
import resoluteit.com.br.R;


public class LoginAct extends AppCompatActivity implements LoginDelegate{

    Button btnLoginEntrar;

    EditText login;
    EditText senha;

    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instanceObjects();
    }

    @Override
    public void login(Usuario usuario) {
        ringProgressDialog.dismiss();

        if(usuario != null) {

            Data.insertUsuario(PreferenceManager.getDefaultSharedPreferences(this),usuario);
            navToSincronismo();
        }else{
            Toast.makeText(this, "Login Inválido!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void login() {

        String matricula = ((EditText) findViewById(R.id.editTextUsuario)).getText().toString();
        String senha = ((EditText) findViewById(R.id.editTextSenha)).getText().toString();

        if (matricula == null || matricula.isEmpty()) {
            Toast.makeText(this,"Matrícula inválido",Toast.LENGTH_LONG).show();
            return;
        }

        if (senha == null || senha.isEmpty()) {
            Toast.makeText(this,"A Senha obrigatória",Toast.LENGTH_LONG).show();
            return;
        }

        Usuario user = new Usuario();

        user.setMatricula(matricula);
        user.setSenha(senha);

        LoginTask task = new LoginTask(this);

        task.execute(user);

    }

    @Override
    public void carregaDialog() {

        ringProgressDialog= ProgressDialog.show(this,"Aguarde...","");
        ringProgressDialog.show();

    }

    private void navToSincronismo() {

        Intent i = new Intent(this, SplashScreen.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }

    private void instanceObjects(){


        login          = (EditText) findViewById(R.id.editTextUsuario);
        senha          = (EditText) findViewById(R.id.editTextSenha);
        btnLoginEntrar = (Button) findViewById(R.id.btnLoginEntrar);


        btnLoginEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });



    }


}
