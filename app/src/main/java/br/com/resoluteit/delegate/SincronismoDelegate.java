package br.com.resoluteit.delegate;

import java.util.List;

import br.com.resoluteit.model.PesquisaPreco;

/**
 * Created by fred on 24/11/17.
 */

public interface SincronismoDelegate {

    public void sincronizou(List<PesquisaPreco> lista);

    void carregaDialogSincronismo();


}
