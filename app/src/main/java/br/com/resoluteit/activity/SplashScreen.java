package br.com.resoluteit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import br.com.resoluteit.delegate.SincronismoDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.sqllite.DataManipulator;
import br.com.resoluteit.task.SincronizarTask;
import resoluteit.com.br.R;

public class SplashScreen extends AppCompatActivity implements SincronismoDelegate {

    ProgressDialog ringProgressDialog;

    private DataManipulator dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.dm = new DataManipulator(this);

        sincronizar();
    }

    private void sincronizar() {


        SincronizarTask task = new SincronizarTask(this);

        task.execute();

    }


    @Override
    public void sincronizou(List<PesquisaPreco> lista) {
        ringProgressDialog.dismiss();

        if(lista != null && !lista.isEmpty()){


            for(PesquisaPreco pp : lista){

                this.dm.insertPesquisa(pp);

            }
        }

        navToHome();
    }

    @Override
    public void carregaDialog() {
        ringProgressDialog = ProgressDialog.show(this, "Sincronizando Dados...", "");
        ringProgressDialog.show();
    }


    private void navToHome() {

        Intent i = new Intent(this, TelaInicial.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);
    }
}
