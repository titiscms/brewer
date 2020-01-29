package com.algaworks.brewer.service.event.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemPedido;
import com.algaworks.brewer.repository.Cervejas;

@Component
public class PedidoListener {
	
	@Autowired
	private Cervejas cervejas;

	@EventListener
	public void pedidoEmitida(PedidoEvent pedidoEvent) {
		for(ItemPedido item : pedidoEvent.getPedido().getItens()) {
			Cerveja cerveja = cervejas.findOne(item.getCerveja().getCodigo());
			cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() - item.getQuantidade());
			cervejas.save(cerveja);
		}
	}
}
