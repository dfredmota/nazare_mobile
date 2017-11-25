package br.com.resoluteit.task;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import br.com.resoluteit.delegate.SincronismoDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.ws.WsDao;


/**
 * Created by fred on 20/10/16.
 */

public class SincronizarTask extends AsyncTask<String, String, String> {

    private SincronismoDelegate sincronizeDelegate;

    private List<PesquisaPreco> listaRetorno;

    public SincronizarTask(SincronismoDelegate activity){

        this.sincronizeDelegate = activity;
    }

    @Override
    protected String doInBackground(String... params) {

        WsDao ws = new WsDao();

        try {

            listaRetorno = ws.sincronizaPesquisaPreco();

        // se a lista nao for nula nem vazia setar os arquivos pra sincronizados

        List<Integer> idsArquivo = new ArrayList<Integer>();

        for(PesquisaPreco pp : listaRetorno){

            if(!idsArquivo.contains(pp.getIdArquivo())){
                idsArquivo.add(pp.getIdArquivo());
            }
        }

        // atualiza cada arquivo que foi sincronizado
            for(Integer i : idsArquivo)
            ws.atualizarArquivoParaSincronizado(i);


        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        this.sincronizeDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(String usuario) {

        this.sincronizeDelegate.sincronizou(listaRetorno);
    }
}
