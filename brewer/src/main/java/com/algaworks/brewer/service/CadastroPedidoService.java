package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.model.StatusPedido;
import com.algaworks.brewer.repository.Pedidos;

@Service
public class CadastroPedidoService {

	@Autowired
	private Pedidos pedidos;
	
	@Transactional
	public Pedido salvar(Pedido pedido) {
		if(pedido.isNovo()) {
			pedido.setDataCriacao(LocalDateTime.now());
		} else {
			Pedido pedidoExistente = pedidos.findOne(pedido.getCodigo());
			pedido.setDataCriacao(pedidoExistente.getDataCriacao());
		}
				
		if(pedido.getDataEntrega() != null) {
			pedido.setDataHoraEntrega(LocalDateTime.of(pedido.getDataEntrega(), pedido.getHorarioEntrega() != null ? pedido.getHorarioEntrega() : LocalTime.NOON));
		}
		
		return pedidos.save(pedido);
	}

	@Transactional
	public void emitir(Pedido pedido) {
		pedido.setStatus(StatusPedido.EMITIDA);
		salvar(pedido);
	}
}