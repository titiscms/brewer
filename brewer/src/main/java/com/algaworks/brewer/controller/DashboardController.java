package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.repository.Pedidos;

@Controller
public class DashboardController {
	
	@Autowired
	private Pedidos pedidos;

	@GetMapping("/")
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		mv.addObject("pedidosNoAno", pedidos.valorTotalNoAno());
		mv.addObject("pedidosNoMes", pedidos.valorTotalNoMes());
		mv.addObject("ticketMedio", pedidos.valorTicketMedioNoAno());
		return mv;
	}
}
