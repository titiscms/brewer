package com.algaworks.brewer.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.PedidoValidator;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.model.StatusPedido;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Pedidos;
import com.algaworks.brewer.repository.filter.PedidoFilter;
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

	@Autowired
	private PedidoValidator pedidoValidator;
	
	@Autowired
	private Pedidos pedidos;
		
	@InitBinder("pedido")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(pedidoValidator);
	} 
	
	@GetMapping("/novo")
	public ModelAndView novo(Pedido pedido) {
		ModelAndView mv = new ModelAndView("pedido/CadastroPedido");
		
		if(StringUtils.isEmpty(pedido.getUuid())) {
			pedido.setUuid(UUID.randomUUID().toString());
		}
		
		mv.addObject("itens", pedido.getItens());
		mv.addObject("valorFrete", pedido.getValorFrete());
		mv.addObject("valorDesconto", pedido.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(pedido.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/novo", params = "salvar")
	public ModelAndView salvar(Pedido pedido, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarPedido(pedido, result);
		if(result.hasErrors()) {
			return novo(pedido);
		}
		
		pedido.setUsuario(usuarioSistema.getUsuario());
		
		cadastroPedidoService.salvar(pedido);
		attributes.addFlashAttribute("mensagem", "Pedido salvo com sucesso!");
		return new ModelAndView("redirect:/pedidos/novo");	
	}
	
	@PostMapping(value = "/novo", params = "emitir")
	public ModelAndView emitir(Pedido pedido, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarPedido(pedido, result);
		if(result.hasErrors()) {
			return novo(pedido);
		}
		
		pedido.setUsuario(usuarioSistema.getUsuario());
		
		cadastroPedidoService.emitir(pedido);
		attributes.addFlashAttribute("mensagem", "Pedido emitido com sucesso!");
		return new ModelAndView("redirect:/pedidos/novo");	
	}
	
	@PostMapping(value = "/novo", params = "enviarEmail")
	public ModelAndView enviarEmail(Pedido pedido, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		validarPedido(pedido, result);
		if(result.hasErrors()) {
			return novo(pedido);
		}
		
		pedido.setUsuario(usuarioSistema.getUsuario());
		
		cadastroPedidoService.salvar(pedido);
		attributes.addFlashAttribute("mensagem", "Pedido salvo e email enviado com sucesso!");
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
	
	@GetMapping()
	public ModelAndView pesquisar(PedidoFilter pedidoFilter, BindingResult result, @PageableDefault(size = 4) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("pedido/PesquisaPedidos");
		mv.addObject("pedidos", pedidos.findAll());
		mv.addObject("todosStatus", StatusPedido.values());
		
		PageWrapper<Pedido> paginaWrapper = new PageWrapper<>(pedidos.filtrar(pedidoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}

	private ModelAndView mvTabelaItensPedido(String uuid) {
		ModelAndView mv = new ModelAndView("pedido/TabelaItensPedido");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void validarPedido(Pedido pedido, BindingResult result) {
		pedido.adicionarItens(tabelaItens.getItens(pedido.getUuid()));
		pedido.calcularValorTotal();
		
		pedidoValidator.validate(pedido, result);
	}
}
