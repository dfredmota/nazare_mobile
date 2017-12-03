package br.com.resoluteit.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import br.com.resoluteit.delegate.GerarArquivoDelegate;
import br.com.resoluteit.delegate.LoginDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.ws.WsDao;


/**
 * Created by fred on 20/10/16.
 */

public class GeraArquivoTask extends AsyncTask<Object, Boolean, Boolean> {

    private GerarArquivoDelegate gerarArquivoDelegate;

    private Context context;

    public GeraArquivoTask(GerarArquivoDelegate activity,Context context){

        this.gerarArquivoDelegate = activity;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        List<PesquisaPreco> lista = ((List<PesquisaPreco>)params[0]);

        StringBuilder s = new StringBuilder();


        for(PesquisaPreco pp : lista){

            String ean         = pp.getEan();

            String concorrente = pp.getConcorrente().substring(0,1);

            String preco       = pp.getPreco();

            String situacao    = pp.getSituacao();


            s.append(ean);
            s.append(concorrente);
            s.append(concorrente);
            s.append(preco);
            s.append(situacao);
            s.append("\n");

        }

        generateFileOnSD(context,"importacao_00.txt",s.toString());


        return null;

    }

    @Override
    protected void onPreExecute() {
        this.gerarArquivoDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(Boolean sucesso) {

        this.gerarArquivoDelegate.gerouArquivo(sucesso);
    }


    public void generateFileOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
