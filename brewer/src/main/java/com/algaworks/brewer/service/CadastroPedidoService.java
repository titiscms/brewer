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
	public void salvar(Pedido pedido) {
		if(pedido.isNova()) {
			pedido.setDataCriacao(LocalDateTime.now());
		}
				
		if(pedido.getDataEntrega() != null) {
			pedido.setDataHoraEntrega(LocalDateTime.of(pedido.getDataEntrega()
					, pedido.getHorarioEntrega() != null ? pedido.getHorarioEntrega() : LocalTime.NOON));
		}
		
		pedidos.save(pedido);
	}

	@Transactional
	public void emitir(Pedido pedido) {
		pedido.setStatus(StatusPedido.EMITIDA);
		salvar(pedido);
	}
}