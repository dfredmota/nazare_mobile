package br.com.resoluteit.model;

import java.io.Serializable;
import java.util.Date;



public class Arquivo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2968409882467967076L;


	private Integer id;

	private String nome;

	private Date data;

	private TipoArquivo tipoArquivo;
	
	private String sincronizado;
	
	public String getSincronizado() {
		return sincronizado;
	}

	public void setSincronizado(String sincronizado) {
		this.sincronizado = sincronizado;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

}
