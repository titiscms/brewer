package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemPedido;

//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS) - configuração eclipse <= 4.2
@SessionScope
@Component
public class TabelaItensPedido {
	
	private List<ItemPedido> itens = new ArrayList<>();
	
	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(ItemPedido::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setCerveja(cerveja);
		itemPedido.setQuantidade(quantidade);
		itemPedido.setValorUnitario(cerveja.getValor());
		
		itens.add(itemPedido);
	}
	
	public int total() {
		return itens.size();
	}

	public Object getItens() {
		return itens;
	}

}
