package br.com.resoluteit.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import br.com.resoluteit.delegate.AdressFromGPSDelegate;
import br.com.resoluteit.delegate.CheckinDelegate;
import br.com.resoluteit.delegate.GerarArquivoDelegate;
import br.com.resoluteit.delegate.SincronismoDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.sqllite.DataManipulator;
import br.com.resoluteit.task.AddressFromGPSTask;
import br.com.resoluteit.task.CheckinTask;
import br.com.resoluteit.task.GeraArquivoTask;
import br.com.resoluteit.task.SincronizarTask;
import br.com.resoluteit.util.Data;
import resoluteit.com.br.R;

public class TelaInicial extends AppCompatActivity implements GerarArquivoDelegate,
        SincronismoDelegate,AdressFromGPSDelegate,CheckinDelegate {

    DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));


    private TextView data;

    private Spinner spinnerConcorrente;

    private DataManipulator dm;

    private Button   btnSelecionar;

    private Button   btnFinalizar;

    private Button   btnLogoff;

    private Button   btnSair;

    private Button   btnSincronizar;

    private Button   btnCheckin;

    private List<PesquisaPreco> listaSincronismo;

    ProgressDialog ringProgressDialog;

    Usuario usuarioLogado;

    Boolean pesquisaCompleta;

    String pesquisarFinalizados="";

    Boolean sincronizarSemSair = false;

    private Location location;

    private LocationListener locationListener;

    LocationManager mLocationManager;

    public static double netlistentime = 0 * 60 * 1000; // minutes * 60 sec/min * 1000 for milliseconds
    public static double netlistendistance = 0 * 1609.344; // miles * conversion to meters
    public static double gpslistentime = 30 * 60 * 1000; // minutes * 60 sec/min * 1000 for milliseconds
    public static double gpslistendistance = 0 * 1609.344; // miles * conversion to meters

    int MY_REQUEST_CODE = 999;

    String endereco = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            pesquisarFinalizados = extras.getString("pesquisarFinalizados");
        }

        instanceObjects();

        String dataExtenso = formatador.format(new Date());

        data.setText(dataExtenso);

        usuarioLogado = Data.getUsuario(PreferenceManager.getDefaultSharedPreferences(this));

        getSupportActionBar().setTitle("Funcionário: "+usuarioLogado.getNome());

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        if(pesquisarFinalizados != null && pesquisarFinalizados.equalsIgnoreCase("S")) {

            // verifica se pesquisa esta completa
            pesquisaCompleta = this.dm.verificaPesquisaCompleta();

            if (pesquisaCompleta) {

                pesquisaCompleta();
            }
        }




    }

    public boolean hasGPSDevice(Context context)
    {
        final LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if ( mgr == null ) return false;
        final List<String> providers = mgr.getAllProviders();
        if ( providers == null ) return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void checkinOk() {

        ringProgressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Check-in realizado no endereço: \n "+ endereco)
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                return;
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void carregaDialogCheckin() {
        ringProgressDialog.show();
    }

    @Override
    public void retrieveAdress(String result) {

        ringProgressDialog.dismiss();

        endereco = result;

        CheckinTask task = new CheckinTask(this);

        String[] parametros = new String[5];

        parametros[0] = usuarioLogado.getId().toString();
        parametros[1] = String.valueOf(location.getLatitude());
        parametros[2] = String.valueOf(location.getLongitude());
        parametros[3] = endereco;

        task.execute(parametros);

    }

    public Location getLocation() {

        Location location = null;

        locationListener = new LocListener();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_REQUEST_CODE);
        }




        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);

        for (String provider : providers) {

            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }

            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                // Found best last known location: %s", l);
                location = l;
            }
        }

        if(location != null)
            return location;

        try {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            Boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            Boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) netlistentime, (float) netlistendistance, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) gpslistentime, (float) gpslistendistance, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location latestlocation) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };



    @Override
    public void carregaDialog() {
        ringProgressDialog= ProgressDialog.show(this,"Realizando exportação dos dados!...","");
        ringProgressDialog.show();
    }

    @Override
    public void carregaDialogSincronismo() {
        ringProgressDialog= ProgressDialog.show(this,"Sincronizando Dados!...","");
        ringProgressDialog.show();
    }


    @Override
    public void carregaDialogAdress() {
        ringProgressDialog= ProgressDialog.show(this,"Recuperando Localização!...","");
        ringProgressDialog.show();
    }



    @Override
    public void gerouArquivo(Boolean sucesso) {

        ringProgressDialog.dismiss();

        if(sucesso){

            mensagemArquivoSucesso();
        }

    }

    @Override
    public void sincronizou(List<PesquisaPreco> lista) {

        ringProgressDialog.dismiss();

        if (lista != null && !lista.isEmpty()) {


            for (PesquisaPreco pp : lista) {

                this.dm.insertPesquisa(pp);

            }
        }


        if(lista != null && !lista.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

            builder.setMessage("Dados sincronizados com sucesso!")
                    .setCancelable(true)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    navToHome();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        }else{

            if(sincronizarSemSair){

                AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

                builder.setMessage("Não há dados para sincronismo!")
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        return;
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();



            }else{

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

        btnSincronizar = (Button) findViewById(R.id.btnSincronizar);

        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

                builder.setMessage("Existem pesquisas não finalizadas, Os dados da pesquisa anterior serão apagados." +
                        " deseja carregar nova pesquisa?")
                        .setCancelable(true)
                        .setNegativeButton("Voltar",null)
                        .setPositiveButton("Incluir",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        sincronizarSemSair = true;

                                        limparBaseESincronizar();

                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        btnCheckin = (Button) findViewById(R.id.btnCheckin);

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                location = getLocation();

                if(hasGPSDevice(TelaInicial.this)) {

                    if (location == null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

                        builder.setMessage("HABILITE O GPS POR FAVOR")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            }
                                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {

                        ringProgressDialog= ProgressDialog.show(TelaInicial.this,"Recuperando Localização!...","");
                        ringProgressDialog.show();

                        AddressFromGPSTask task = new AddressFromGPSTask(TelaInicial.this);

                        task.execute(new Double[]{location.getLatitude(), location.getLongitude()});
                    }
                }

            }
        });
    }

    private void limparBaseESincronizar(){

        this.dm.deleteAll();

        sincronizar();
    }


    private void navToSecao() {

        Intent i = new Intent(this, SecaoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",spinnerConcorrente.getSelectedItem().toString());

        this.startActivity(i);

    }


    private void navToHome() {

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

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

    private void sincronizar() {


        SincronizarTask task = new SincronizarTask(this);

        Integer idUsuario = usuarioLogado.getId();

        String params[] = new String[2];

        params[0] = idUsuario.toString();

        task.execute(params);

    }

    private void pesquisaCompleta(){


        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("A Pesquisa está completa. Deseja enviar a pesquisa?")
                .setCancelable(true)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Enviar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                populaListaSincronismo();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void concorrenteNaoFinalizado(){


        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

        builder.setMessage("Existem Concorrentes pendentes de Pesquisa, Deseja enviar pesquisa parcial?")
                .setCancelable(true)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Enviar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                populaListaSincronismo();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void populaListaSincronismo(){

        //TODO: finaliza por concorrente
        //this.listaSincronismo = this.dm.listaPraSincronizar(spinnerConcorrente.getSelectedItem().toString());

        this.listaSincronismo = this.dm.listaPraSincronizar();


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


        //verifica se ainda existem itens pendentes
        Boolean aindaTemItens = this.dm.verificaSeAindaHaProdutosNaoLidos();


        if(aindaTemItens){


            AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

            builder.setMessage("Arquivo foi gerado parcialmente, mais ainda existem produtos não coletados!")
                    .setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            return;
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();


        }

        // se todos os itens da pesquisa tiverem sido lidos apagar todos os sincronizados
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);

            builder.setMessage("Arquivo Completo Gerado Com Sucesso, Os dados dessa pesquisa serão apagados do Aplicativo!")
                    .setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            limpaBaseDeDadosApp();

                            navToHome();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();

        }

    }

    private void limpaBaseDeDadosApp(){

        this.dm.limpaBaseDeDados();
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

        builder.setMessage("Não há dados para exportar!")
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
