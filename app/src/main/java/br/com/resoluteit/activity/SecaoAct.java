package br.com.resoluteit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.sqllite.DataManipulator;
import resoluteit.com.br.R;

public class SecaoAct extends AppCompatActivity {

    private String concorrente;

    private DataManipulator dm;

    private Spinner spinnerSecao;

    private Button  btnVoltar;

    private Button  btnSecao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secao);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            concorrente = extras.getString("concorrente");
        }


        this.dm = new DataManipulator(this);

        // verifica se esse concorrente já esta completo
        Boolean concorrenteCompleto = this.dm.verificaConcorrenteCompleto(this.concorrente);

        if(concorrenteCompleto){

            concorrenteFinalizado();

        }

        instanceObjects();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Nazare Mobile");



    }


    private void concorrenteFinalizado(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SecaoAct.this);

        builder.setMessage("Concorrente Finalizado!")
                .setCancelable(true)

                .setNegativeButton("Finalizar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        navToHomeWithParam();


                    }
                })
                .setPositiveButton("Continuar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                    return;
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void instanceObjects(){

        spinnerSecao = (Spinner) findViewById(R.id.spinnerSecao);

        btnVoltar = (Button) findViewById(R.id.btnVoltar);

        btnSecao     = (Button) findViewById(R.id.btnSecao);

        btnSecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToProd();
            }
        });

        List<String> lista = this.dm.listaSecoesConcorrente(this.concorrente);

       List<String> listaEnumerada = enumeraSecoes(lista);

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, listaEnumerada, getResources());

        spinnerSecao.setAdapter(adapter);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToHomeWithParam();
            }
        });

    }

    //TODO: pode ocorrer problema se tiver mais de 10 secoes
    private List<String> enumeraSecoes(List<String> lista){

        int contador = 1;

        List<String> novaLista = new ArrayList<String>();

        for(String secao : lista){

            secao = contador + "-" + secao;

            contador = contador + 1;

            novaLista.add(secao);
        }

        return novaLista;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            navToHome();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        navToHomeWithParam();
    }

    private void navToHome(){

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }


    private void navToHomeWithParam(){

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("pesquisarFinalizados","S");

        this.startActivity(i);

    }


    private void navToProd() {

        Intent i = new Intent(this, ListaProdutosAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",concorrente.toString());

        String[] secaoNaoNumerada = spinnerSecao.getSelectedItem().toString().split("-");

        i.putExtra("secao",secaoNaoNumerada[1]);

        this.startActivity(i);

    }
}
