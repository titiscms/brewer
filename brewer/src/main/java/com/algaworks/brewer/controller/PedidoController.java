package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.session.TabelaItensPedido;


@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private TabelaItensPedido tabelaItensPedido; 
	
	@RequestMapping("/novo")
	public String novo() {
		return "pedido/CadastroPedido";
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensPedido.adicionarItem(cerveja, 1);
		ModelAndView mv = new ModelAndView("pedido/TabelaItensPedido");
		mv.addObject("itens", tabelaItensPedido.getItens());
		return mv;
	}
}
