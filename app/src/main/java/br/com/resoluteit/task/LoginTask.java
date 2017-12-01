package br.com.resoluteit.task;

import android.os.AsyncTask;

import br.com.resoluteit.delegate.LoginDelegate;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.ws.WsDao;


/**
 * Created by fred on 20/10/16.
 */

public class LoginTask extends AsyncTask<Usuario, Usuario, Usuario> {

    private LoginDelegate loginDelegate;

    public LoginTask(LoginDelegate activity){

        this.loginDelegate = activity;
    }

    @Override
    protected Usuario doInBackground(Usuario... params) {

        WsDao ws = new WsDao();

        Usuario usuario = null;

        try {

           usuario = ws.loginApp(params[0].getMatricula(),params[0].getSenha());

        }catch(Exception e){
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    protected void onPreExecute() {
        this.loginDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(Usuario usuario) {

        this.loginDelegate.login(usuario);
    }
}
