package com.algaworks.brewer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.ItemPedido;
import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.repository.Pedidos;

@Service
public class CadastroPedidoService {

	@Autowired
	private Pedidos pedidos;
	
	@Transactional
	public void salvar(Pedido pedido) {
		if(pedido.isNova()) {
			pedido.setDataCriacao(LocalDateTime.now());
		}
		
		BigDecimal valorTotalItens = pedido.getItens().stream()
				.map(ItemPedido::getValorTotal)
				.reduce(BigDecimal::add)
				.get();
		
		BigDecimal valorTotal = calcularValorTotal(valorTotalItens, pedido.getValorFrete(), pedido.getValorDesconto());
		pedido.setValorTotal(valorTotal);
		
		if(pedido.getDataEntrega() != null) {
			pedido.setDataHoraEntrega(LocalDateTime.of(pedido.getDataEntrega(), pedido.getHorarioEntrega()));
		}
		
		pedidos.save(pedido);
	}

	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
		return valorTotalItens
				.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO));
	}	
}