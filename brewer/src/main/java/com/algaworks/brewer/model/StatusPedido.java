package com.algaworks.brewer.model;

public enum StatusPedido {

	ORCAMENTO("Orçamento"),
	EMITIDA("Emitida"),
	CANCELADA("Cancelada");
	
	private String descricao;
	
	StatusPedido (String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
