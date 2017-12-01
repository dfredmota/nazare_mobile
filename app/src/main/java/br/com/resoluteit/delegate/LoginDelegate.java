package br.com.resoluteit.delegate;


import br.com.resoluteit.model.Usuario;

/**
 * Created by fred on 20/10/16.
 */


public interface LoginDelegate {

    void login(Usuario usuario);
    void carregaDialog();
}
