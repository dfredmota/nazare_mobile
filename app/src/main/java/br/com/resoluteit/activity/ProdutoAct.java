package br.com.resoluteit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.sqllite.DataManipulator;
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

    String re = "";

    String preco = "";

    Spinner spinnerSituacao;



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
        lista.add("Não Encontrado");
        lista.add("Outros");

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, lista, getResources());

        spinnerSituacao.setAdapter(adapter);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {


            re = scanResult.getContents();

            // verifica se o codigo de barras é igual se for abre o preço
            if(re.equalsIgnoreCase(this.prod.getEan())){

                dialogPreco();


            }else{

                dialogPreco();
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
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String value = input.getText().toString().trim();

                preco = value;

                String situacao = spinnerSituacao.getSelectedItem().toString();

                if(situacao.equalsIgnoreCase("Normal"))
                    situacao="N";
                if(situacao.equalsIgnoreCase("Oferta"))
                    situacao="P";
                if(situacao.equalsIgnoreCase("Não Encontrado"))
                    situacao="S";
                if(situacao.equalsIgnoreCase("Outros"))
                    situacao="O";


                dm.updateProduto(concorrenteS,secaoS,produtoS,preco,situacao);

                Toast.makeText(ProdutoAct.this,"Produto Coletado com Sucesso!",Toast.LENGTH_LONG).show();

                navToLista();

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();

    }


    private void navToLista() {

        Intent i = new Intent(this, ListaProdutosAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",concorrenteS);

        i.putExtra("secao",secaoS);

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
        navToHome();
    }

    private void navToHome(){

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }

}
