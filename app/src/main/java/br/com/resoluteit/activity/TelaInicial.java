package br.com.resoluteit.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.delegate.GerarArquivoDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.sqllite.DataManipulator;
import br.com.resoluteit.task.GeraArquivoTask;
import br.com.resoluteit.util.Data;
import resoluteit.com.br.R;

public class TelaInicial extends AppCompatActivity implements GerarArquivoDelegate {

    DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));


    private TextView data;

    private Spinner spinnerConcorrente;

    private DataManipulator dm;

    private Button   btnSelecionar;

    private Button   btnFinalizar;

    private Button   btnLogoff;

    private Button   btnSair;

    private List<PesquisaPreco> listaSincronismo;

    ProgressDialog ringProgressDialog;

    Usuario usuarioLogado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        instanceObjects();

        String dataExtenso = formatador.format(new Date());

        data.setText(dataExtenso);

        usuarioLogado = Data.getUsuario(PreferenceManager.getDefaultSharedPreferences(this));

        getSupportActionBar().setTitle("Funcionário: "+usuarioLogado.getNome());

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    @Override
    public void carregaDialog() {
        ringProgressDialog= ProgressDialog.show(this,"Realizando exportação dos dados!...","");
        ringProgressDialog.show();
    }

    @Override
    public void gerouArquivo(Boolean sucesso) {

        ringProgressDialog.dismiss();

        if(sucesso){

            mensagemArquivoSucesso();
        }

    }

    private void instanceObjects(){

        this.dm = new DataManipulator(this);

        //recupera os concorrentes

        List<PesquisaPreco> all = this.dm.all();

        List<String> listaConcorrentes = this.dm.listaConcorrentes();

        if(listaConcorrentes == null || listaConcorrentes.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

            builder.setMessage("Não há dados para sincronismo!")
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                        TelaInicial.this.finish();
                                        System.exit(0);
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        }

        data = (TextView) findViewById(R.id.txt_data);

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, listaConcorrentes, getResources());

        spinnerConcorrente = (Spinner) findViewById(R.id.spinnerConcorrente);
        spinnerConcorrente.setAdapter(adapter);

        btnSelecionar = (Button) findViewById(R.id.btnSelecionar);

        btnSelecionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToSecao();
            }
        });

        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalizarConcorrente();
            }
        });

        btnLogoff  = (Button) findViewById(R.id.btnLogout);

        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout();
            }
        });

        btnSair = (Button) findViewById(R.id.btnSair);

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sair();
            }
        });
    }


    private void navToSecao() {

        Intent i = new Intent(this, SecaoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",spinnerConcorrente.getSelectedItem().toString());

        this.startActivity(i);

    }

    private void navToLogin() {

        Intent i = new Intent(this, SplashScreen.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",spinnerConcorrente.getSelectedItem().toString());

        this.startActivity(i);

    }

    private void logout(){

        Data.loggoutUsuario(PreferenceManager.getDefaultSharedPreferences(this));

        navToLogin();

    }

    private void sair(){

        TelaInicial.this.finish();
        System.exit(0);
    }

    private void finalizarConcorrente(){

        Boolean finalizado = this.dm.concorrenteFinalizado(spinnerConcorrente.getSelectedItem().toString());

        if(finalizado){
            concorrenteFinalizado();
        }else{
            concorrenteNaoFinalizado();
        }

    }

    private void concorrenteNaoFinalizado(){


        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Existem Seções pendentes nesse concorrente!")
                .setCancelable(true)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Enviar Pesquisa Parcial",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                populaListaSincronismo();


                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void populaListaSincronismo(){

        this.listaSincronismo = this.dm.listaPraSincronizar(spinnerConcorrente.getSelectedItem().toString());

        if(this.listaSincronismo == null || this.listaSincronismo.isEmpty()){

            msgNaoHaDadosSincronismo();

        }else{

            // prepara a sincronizacao
            GeraArquivoTask task = new GeraArquivoTask(this,this);

            Object[] params = new Object[2];

            params[0] = this.listaSincronismo;

            params[1] = usuarioLogado.getId();

            task.execute(params);


        }
    }

    // TODO:sincroniza os registros para status = 'S'
    private void updateListaSincronismo(){

        if(this.listaSincronismo != null && !this.listaSincronismo.isEmpty()){

            for(PesquisaPreco pp : this.listaSincronismo){

                this.dm.updateProdutoSincronizado(pp.getId().toString());
            }

        }

    }

    private void concorrenteFinalizado(){


        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Concorrente Finalizado!")
                .setCancelable(true)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Enviar Pesquisa",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                populaListaSincronismo();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void mensagemArquivoSucesso(){

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Exportação Realizada com Sucesso!")
                .setCancelable(true).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            updateListaSincronismo();

                            return;
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }



    private void msgNaoHaDadosSincronismo(){


        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Concorrente Finalizado!")
                .setCancelable(true).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
