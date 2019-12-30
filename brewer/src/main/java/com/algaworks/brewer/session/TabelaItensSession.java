package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemPedido;

//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS) - configuração eclipse <= 4.2
@SessionScope
@Component
public class TabelaItensSession {

	private Set<TabelaItensPedido> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensPedido tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(cerveja, quantidade);
		tabelas.add(tabela);
	}

	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensPedido tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
	}

	public void removerItem(String uuid, Cerveja cerveja) {
		TabelaItensPedido tabela = buscarTabelaPorUuid(uuid);
		tabela.removerItem(cerveja);
	}

	public List<ItemPedido> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	public Object getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}
	
	private TabelaItensPedido buscarTabelaPorUuid(String uuid) {
		TabelaItensPedido tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensPedido(uuid));
		return tabela;
	}
}
