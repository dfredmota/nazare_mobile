package br.com.resoluteit.task;

import android.os.AsyncTask;

import br.com.resoluteit.delegate.CheckinDelegate;
import br.com.resoluteit.delegate.LoginDelegate;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.ws.WsDao;


/**
 * Created by fred on 20/10/16.
 */

public class CheckinTask extends AsyncTask<String, String, String> {

    private CheckinDelegate checkinDelegate;

    public CheckinTask(CheckinDelegate activity){

        this.checkinDelegate = activity;
    }

    @Override
    protected String doInBackground(String... params) {

        WsDao ws = new WsDao();

        try {

           ws.insertCheckin(params[0],params[1],params[2],params[3]);

        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPreExecute() {
        this.checkinDelegate.carregaDialogCheckin();
    }

    @Override
    protected void onPostExecute(String s) {

        this.checkinDelegate.checkinOk();
    }
}
