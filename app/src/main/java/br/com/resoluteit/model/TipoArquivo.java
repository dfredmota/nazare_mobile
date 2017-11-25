package br.com.resoluteit.model;

public enum TipoArquivo {
	
	
	IMPORTACAO(1,"Importação"),
	EXPORTACAO(2,"Exportação");
	
	
	private int id;
	private String descricao;
	
	private TipoArquivo(int id, String descricao){
		this.id = id;
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
