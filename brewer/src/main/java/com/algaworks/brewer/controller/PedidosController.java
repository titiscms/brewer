package com.algaworks.brewer.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemPedido;
import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.model.StatusPedido;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Pedidos;
import com.algaworks.brewer.repository.filter.PedidoFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroPedidoService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.session.TabelaItensSession;


@Controller
@RequestMapping("/pedidos")
public class PedidosController {

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
	
	@Autowired
	private Mailer mailer;
		
	@InitBinder("pedido")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(pedidoValidator);
	} 
	
	@GetMapping("/novo")
	public ModelAndView novo(Pedido pedido) {
		ModelAndView mv = new ModelAndView("pedido/CadastroPedido");
		
		setUuid(pedido);
		
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
		
		pedido = cadastroPedidoService.salvar(pedido);
		mailer.enviar(pedido);
		
		attributes.addFlashAttribute("mensagem", String.format("Pedido nº %d salvo com sucesso e email enviado!", pedido.getCodigo()));
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
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
		tabelaItens.removerItem(uuid, cerveja);
		return mvTabelaItensPedido(uuid);
	}
	
	@GetMapping()
	public ModelAndView pesquisar(PedidoFilter pedidoFilter, BindingResult result, @PageableDefault(size = 4) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("pedido/PesquisaPedidos");
		mv.addObject("todosStatus", StatusPedido.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Pedido> paginaWrapper = new PageWrapper<>(pedidos.filtrar(pedidoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Pedido pedido = pedidos.buscarComItens(codigo);
		
		setUuid(pedido);
		for(ItemPedido item : pedido.getItens()) {
			tabelaItens.adicionarItem(pedido.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = novo(pedido);
		mv.addObject(pedido);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public ResponseEntity<?> excluir(@PathVariable("codigo") Pedido pedido) {
		try {
			this.cadastroPedidoService.excluir(pedido);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(value = "/novo", params = "cancelar")
	public ModelAndView cancelar(Pedido pedido, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroPedidoService.cancelar(pedido);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");
		}
		
		attributes.addFlashAttribute("mensagem", "Pedido cancelado com sucesso");
		return new ModelAndView("redirect:/pedidos/" + pedido.getCodigo());
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
	
	private void setUuid(Pedido pedido) {
		if(StringUtils.isEmpty(pedido.getUuid())) {
			pedido.setUuid(UUID.randomUUID().toString());
		}
	}
}
