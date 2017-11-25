package br.com.resoluteit.model;




public class PesquisaPreco {
	
	

	private Integer id;
	
	private String  concorrente;
	
	private String  ean;
	
	private String  secao;
	
	private String  grupo;

	private String  subGrupo;
	
	private String  descricao;
	
	private String  preco;
	
	private String  flag;

	private Integer idArquivo;

	private Arquivo arquivo;

	private String  sincronizado;

	public String getSincronizado() {
		return sincronizado;
	}

	public void setSincronizado(String sincronizado) {
		this.sincronizado = sincronizado;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getConcorrente() {
		return concorrente;
	}

	public void setConcorrente(String concorrente) {
		this.concorrente = concorrente;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(String subGrupo) {
		this.subGrupo = subGrupo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	

}
