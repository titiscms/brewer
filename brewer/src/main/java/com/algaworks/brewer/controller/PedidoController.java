package com.algaworks.brewer.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroPedidoService;
import com.algaworks.brewer.session.TabelaItensSession;


@Controller
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private CadastroPedidoService cadastroPedidoService;
	
	@Autowired
	private TabelaItensSession tabelaItens; 
	
	@RequestMapping("/novo")
	public ModelAndView novo(Pedido pedido) {
		ModelAndView mv = new ModelAndView("pedido/CadastroPedido");
		pedido.setUuid(UUID.randomUUID().toString());
		return mv;
	}
	
	@PostMapping("/novo")
	public ModelAndView salvar(Pedido pedido, BindingResult result, RedirectAttributes attributes, 
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
//		if(result.hasErrors()) {
//			return novo(pedido);
//		}
		
		pedido.setUsuario(usuarioSistema.getUsuario());
		pedido.adicionarItens(tabelaItens.getItens(pedido.getUuid()));
		
		cadastroPedidoService.salvar(pedido);
		attributes.addFlashAttribute("mensagem", "Pedido salvo com sucesso!");
		return new ModelAndView("redirect:/pedidos/novo");	
	}
	
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mvTabelaItensPedido(uuid);
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, Integer quantidade, String uuid) {
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return mvTabelaItensPedido(uuid);
	}
	
	@DeleteMapping("item/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
		tabelaItens.removerItem(uuid, cerveja);
		return mvTabelaItensPedido(uuid);
	}

	private ModelAndView mvTabelaItensPedido(String uuid) {
		ModelAndView mv = new ModelAndView("pedido/TabelaItensPedido");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
}
