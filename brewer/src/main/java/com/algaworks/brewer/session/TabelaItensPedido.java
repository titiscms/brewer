package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemPedido;

class TabelaItensPedido {
	
	private String uuid;
	private List<ItemPedido> itens = new ArrayList<>();
	
	public TabelaItensPedido(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUuid() {
		return uuid;
	}

	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(ItemPedido::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		Optional<ItemPedido> itemPedidoOptional = buscarItemPorCerveja(cerveja);
		
		ItemPedido itemPedido = null;
		if(itemPedidoOptional.isPresent()) {
			itemPedido = itemPedidoOptional.get();
			itemPedido.setQuantidade(itemPedido.getQuantidade() + quantidade);
		} else {
			itemPedido = new ItemPedido();
			itemPedido.setCerveja(cerveja);
			itemPedido.setQuantidade(quantidade);
			itemPedido.setValorUnitario(cerveja.getValor());
			itens.add(0, itemPedido);
		}
	}
	
	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		ItemPedido itemPedido = buscarItemPorCerveja(cerveja).get();
		itemPedido.setQuantidade(quantidade);
	}
	
	public void removerItem(Cerveja cerveja) {
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				.findAny().getAsInt();
		itens.remove(indice);
	}
	
	public int total() {
		return itens.size();
	}

	public List<ItemPedido> getItens() {
		return itens;
	}
	
	private Optional<ItemPedido> buscarItemPorCerveja(Cerveja cerveja) {
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensPedido other = (TabelaItensPedido) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
