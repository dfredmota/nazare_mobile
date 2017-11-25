package br.com.resoluteit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.resoluteit.adapter.SpinnerAdapter;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.sqllite.DataManipulator;
import resoluteit.com.br.R;

public class TelaInicial extends AppCompatActivity {

    DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));


    private TextView data;

    private Spinner spinnerConcorrente;

    private DataManipulator dm;

    private Button   btnSelecionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        instanceObjects();

        String dataExtenso = formatador.format(new Date());

        data.setText(dataExtenso);
    }

    private void instanceObjects(){

        this.dm = new DataManipulator(this);

        //recupera os concorrentes

        List<PesquisaPreco> all = this.dm.all();

        List<String> listaConcorrentes = this.dm.listaConcorrentes();

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
    }


    private void navToSecao() {

        Intent i = new Intent(this, SecaoAct.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        i.addCategory(Intent.CATEGORY_HOME);

        i.putExtra("concorrente",spinnerConcorrente.getSelectedItem().toString());

        this.startActivity(i);

    }
}
