package com.algaworks.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.repository.helper.pedido.PedidosQueries;

public interface Pedidos extends JpaRepository<Pedido, Long>, PedidosQueries {

}