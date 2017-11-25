package br.com.resoluteit.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.sqllite.DataManipulator;
import resoluteit.com.br.R;

public class SecaoAct extends AppCompatActivity {

    private String concorrente;

    private DataManipulator dm;

    private Spinner spinnerSecao;

    private Button  btnVoltar;

    private Button  btnFinalizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secao);



        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            concorrente = extras.getString("concorrente");
        }

        instanceObjects();
    }

    private void instanceObjects(){

        this.dm = new DataManipulator(this);

        spinnerSecao = (Spinner) findViewById(R.id.spinnerSecao);

        btnVoltar    = (Button) findViewById(R.id.btnVoltar);

        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);

        List<String> lista = this.dm.listaSecoesConcorrente(this.concorrente);

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, lista, getResources());

        spinnerSecao.setAdapter(adapter);

    }
}
