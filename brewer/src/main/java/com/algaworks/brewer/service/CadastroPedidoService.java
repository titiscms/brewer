package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.model.StatusPedido;
import com.algaworks.brewer.repository.Pedidos;
import com.algaworks.brewer.service.event.pedido.PedidoEvent;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroPedidoService {

	@Autowired
	private Pedidos pedidos;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Transactional
	public Pedido salvar(Pedido pedido) {
		if(pedido.isSalvarProibido()) {
			throw new RuntimeException("Usuário tentando salvar um pedido proibido");
		}
		
		if(pedido.isNovo()) {
			pedido.setDataCriacao(LocalDateTime.now());
		} else {
			Pedido pedidoExistente = pedidos.getOne(pedido.getCodigo());
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
		
		publisher.publishEvent(new PedidoEvent(pedido));
	}

	@PreAuthorize("#pedido.usuario == principal.usuario or hasRole('CANCELAR_PEDIDO')")
	@Transactional
	public void cancelar(Pedido pedido) {
		Pedido pedidoExistente = pedidos.getOne(pedido.getCodigo());
		
		pedidoExistente.setStatus(StatusPedido.CANCELADA);
		pedidos.save(pedidoExistente);
	}

	@Transactional
	public void excluir(Pedido pedido) {
		try {
			this.pedidos.delete(pedido);
			this.pedidos.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar pedido.");
		}
	}
}