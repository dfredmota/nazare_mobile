package br.com.resoluteit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.sqllite.DataManipulator;
import br.com.resoluteit.util.Data;
import br.com.resoluteit.util.MascaraMonetaria;
import resoluteit.com.br.R;

public class ProdutoAct extends AppCompatActivity {


    private DataManipulator dm;

    private TextView        concorrente;

    private TextView        secao;

    private TextView        produto;

    private TextView        ean;

    String concorrenteS,secaoS,produtoS;

    private PesquisaPreco   prod;

    private Button         btnCodigoBarras;

    private Button         btnNaoEcontrado;

    String re = "";

    String preco = "";

    Spinner spinnerSituacao;

    DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));

    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    SharedPreferences sh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            concorrenteS = extras.getString("concorrente");

            secaoS       = extras.getString("secao");

            produtoS      =  extras.getString("produto");

        }

        instanceObjects();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Nazare Mobile");

        sh = PreferenceManager.getDefaultSharedPreferences(this);


    }

    private void instanceObjects(){

        this.dm = new DataManipulator(this);

        concorrente    = (TextView) findViewById(R.id.txt_concorrente);

        secao          = (TextView)  findViewById(R.id.txt_secao);

        produto        = (TextView) findViewById(R.id.txt_produto);

        ean            = (TextView) findViewById(R.id.txt_ean);

        concorrente.setText("Concorrente: "+concorrenteS);
        secao.setText("Seção: "+secaoS);
        ean.setText("EAN: "+produtoS);

        this.prod = this.dm.retornaProduto(concorrenteS,secaoS,produtoS);

        produto.setText("Produto: "+this.prod.getDescricao());

        btnCodigoBarras = (Button) findViewById(R.id.btnCodigoBarras);

        btnCodigoBarras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(ProdutoAct.this);
                integrator.setMessage("Posicione corretamente o código de barras...");
                integrator.initiateScan();
            }
        });

        spinnerSituacao = (Spinner) findViewById(R.id.spinnerSituacao);

        List<String> lista = new ArrayList<String>();

        lista.add("Normal");
        lista.add("Oferta");
        lista.add("Outros");

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, lista, getResources());

        spinnerSituacao.setAdapter(adapter);

        btnNaoEcontrado = (Button) findViewById(R.id.btnNaoEcontrado);

        btnNaoEcontrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // verifica no shared preferences se marcou 3 produtos como não encontrado
                Integer qtdNaoEncontrado = Data.getQuantidadeProdutoNaoEncontrado(PreferenceManager.getDefaultSharedPreferences(ProdutoAct.this));


                Boolean mostrarAlerta = false;


                if(qtdNaoEncontrado != null && qtdNaoEncontrado <= 3){

                    qtdNaoEncontrado = qtdNaoEncontrado + 1;

                    Data.insertProdutoNaoEcontrado(PreferenceManager.getDefaultSharedPreferences(ProdutoAct.this),qtdNaoEncontrado);

                }else if(qtdNaoEncontrado != null && qtdNaoEncontrado >= 3){

                    mostrarAlerta = true;
                }



                if(mostrarAlerta){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoAct.this);

                    builder.setMessage("Você já marcou vários produtos como não encontrado. Por favor procure com cuidado!")
                            .setCancelable(true)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Data.insertProdutoNaoEcontrado(PreferenceManager.getDefaultSharedPreferences(ProdutoAct.this),0);


                                            dm.updateProdutoNaoEcontrado(prod.getId().toString());

                                            Toast.makeText(ProdutoAct.this,"Produto Coletado com Sucesso!",Toast.LENGTH_LONG).show();

                                            navToLista(false);
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                }else{

                    dm.updateProdutoNaoEcontrado(prod.getId().toString());

                    Toast.makeText(ProdutoAct.this,"Produto Coletado com Sucesso!",Toast.LENGTH_LONG).show();

                    navToLista(false);
                }

            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {


            re = scanResult.getContents();

            // verifica se o codigo de barras é igual se for abre o preço
            if(re != null && re.equalsIgnoreCase(this.prod.getEan())){

                dialogPreco();


            }else if(re != null){

                String dataExtenso = formatador.format(new Date());


                //gravar o relatório de EAN divergente no sqllite
                this.dm.insertRelatorio(this.prod.getConcorrente(),this.prod.getSecao(),
                        this.prod.getDescricao(),this.prod.getEan(),
                        re,fmt.format(new Date()),this.prod.getId().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoAct.this);

                builder.setMessage("EAN difere do cadastrado")
                        .setCancelable(true)
                        .setNegativeButton("Voltar",null)
                        .setPositiveButton("Digitar Preço",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialogPreco();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();


            }

        }
        // else continue with any other code you need in the method

    }

    private void dialogPreco(){


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setWidth(30);
        alert.setMessage("Digite o preço do produto:");
        alert.setView(input);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        input.addTextChangedListener(new MascaraMonetaria(input));

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String value = input.getText().toString().trim();

                preco = value;

                String situacao = spinnerSituacao.getSelectedItem().toString();

                if(situacao.equalsIgnoreCase("Normal"))
                    situacao="n";
                if(situacao.equalsIgnoreCase("Oferta"))
                    situacao="p";
                if(situacao.equalsIgnoreCase("Outros"))
                    situacao="o";


                dm.updateProduto(prod.getId().toString(),preco,situacao);

                Toast.makeText(ProdutoAct.this,"Produto Coletado com Sucesso!",Toast.LENGTH_LONG).show();

                navToLista(true);

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();

    }


    private void navToLista(Boolean coletado) {

        Intent i = new Intent(this, ListaProdutosAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",concorrenteS);

        i.putExtra("secao",secaoS);

        if(coletado)
        i.putExtra("produtoColetado",this.prod.getId());

        this.startActivity(i);

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

        navToSecao();
    }


    private void navToSecao() {

        Intent i = new Intent(this, SecaoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",concorrenteS);

        this.startActivity(i);

    }

    private void navToHome(){

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }

}
