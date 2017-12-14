package br.com.resoluteit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import br.com.resoluteit.adapter.PesquisaAdapter;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.sqllite.DataManipulator;
import resoluteit.com.br.R;

public class ListaProdutosAct extends AppCompatActivity {

    String concorrente;

    String secao;

    String produto;

    Integer idProd;

    ListView listView;

    DataManipulator dm;

    List<PesquisaPreco> lista;

    private TextView concorrenteT;

    private TextView        secaoT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            concorrente = extras.getString("concorrente");

            secao       = extras.getString("secao");

            idProd      = extras.getInt("produtoColetado");

        }


        instanceObjects();

        this.lista = this.dm.listaPesquisaByConcorrenteAndSecao(concorrente,secao);

        // verifica se veio parametro pra jogar pro final da lista

        if(idProd != null && idProd != 0){

            PesquisaPreco pp = this.dm.getProdutoById(idProd.toString());

            // joga produto pro final da lista

            for(PesquisaPreco p2 : this.lista){


                if(p2.getId().equals(pp.getId())){

                    this.lista.remove(p2);

                    this.lista.add(pp);

                    break;

                }
            }




        }

        populateListaProdutos();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Boolean sessaoCompleta = this.dm.verificaSessaoCompleta(concorrente,secao);

        if(sessaoCompleta){
            sessaoCompleta();
        }
    }


    private void populateListaProdutos(){

        if(lista != null && !lista.isEmpty()){

            PesquisaAdapter msgAdapter = new PesquisaAdapter(this,lista);

            listView.setAdapter(msgAdapter);

        }
    }


    private void instanceObjects(){

        listView = (ListView) findViewById(R.id.list);

        this.dm = new DataManipulator(this);

        concorrenteT = (TextView) findViewById(R.id.concorrente);

        secaoT       = (TextView)  findViewById(R.id.secao);

        concorrenteT.setText("Concorrente: "+concorrente);
        secaoT.setText("Seção:"+secao);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PesquisaPreco pp = (PesquisaPreco)listView.getAdapter().getItem(i);

                produto = pp.getEan();

                navToProd();
            }
        });


    }


    private void navToProd() {

        Intent i = new Intent(this, ProdutoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",concorrente);

        i.putExtra("secao",secao);

        i.putExtra("produto",produto);

        this.startActivity(i);

    }

    private void sessaoCompleta(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ListaProdutosAct.this);

        builder.setMessage("Sessão Completa!")
                .setCancelable(true)

                .setNegativeButton("Finalizar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        navToSecao();

                    }
                })
                .setPositiveButton("Continuar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navToSecao() {

        Intent i = new Intent(this, SecaoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.putExtra("concorrente",concorrente);

        this.startActivity(i);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            navToSecao();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        navToSecao();
    }

    private void navToHome(){

        Intent i = new Intent(this, TelaInicial.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        this.startActivity(i);

    }


}
