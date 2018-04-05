package br.com.resoluteit.task;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        String dia = getDayToday();

        try {

            listaRetorno = ws.sincronizaPesquisaPreco(Integer.parseInt(params[0]),dia);

        // se a lista nao for nula nem vazia setar os arquivos pra sincronizados

        List<Integer> idsArquivo = new ArrayList<Integer>();

        for(PesquisaPreco pp : listaRetorno){

            if(!idsArquivo.contains(pp.getIdArquivo())){
                idsArquivo.add(pp.getIdArquivo());
            }
        }

        // atualiza cada arquivo que foi sincronizado
            for(Integer i : idsArquivo)
            ws.atualizarArquivoParaSincronizado(i,Integer.parseInt(params[0]));


        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        this.sincronizeDelegate.carregaDialogSincronismo();
    }

    @Override
    protected void onPostExecute(String usuario) {

        this.sincronizeDelegate.sincronizou(listaRetorno);
    }


    private String getDayToday(){

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        String dia = "";

        switch (day){


            case 0:

                dia = "sabado";
                break;

            case 1:

                dia = "domingo";
                break;

            case 2:

                dia = "segunda";
                break;

            case 3:

                dia = "terca";
                break;

            case 4:

                dia = "quarta";
                break;

            case 5:

                dia = "quinta";
                break;

            case 6:

                dia = "sexta";
                break;

        }

        return dia;

    }

}
