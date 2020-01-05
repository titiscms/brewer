package com.algaworks.brewer.repository.helper.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.repository.filter.PedidoFilter;

public interface PedidosQueries {
	
	public Page<Pedido> filtrar(PedidoFilter filtro, Pageable pageable);
}
