package com.algaworks.brewer.service.event.pedido;

import com.algaworks.brewer.model.Pedido;

public class PedidoEvent {

	private Pedido pedido;
	
	public PedidoEvent(Pedido pedido) {
		this.pedido = pedido;
	}
	
	public Pedido getPedido() {
		return pedido;
	}
}
