package br.com.resoluteit.model;

import android.graphics.Bitmap;

/**
 * Created by Powerdroid on 19/08/2016.
 */
public class ProdutoBean {

    private String codigoBarras;
    private String titulo;
    private Bitmap imagem;


    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }
}
