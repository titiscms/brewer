package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Pedido;

@Component
public class PedidoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Pedido.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Selecione um cliente na pesquisa rápdida");
		
		Pedido pedido = (Pedido) target;
		validarSeInformouApenasHorarioEntrega(errors, pedido);
		validarSeInformouItens(errors, pedido);
		validarValorTotal(errors, pedido);
	}

	private void validarValorTotal(Errors errors, Pedido pedido) {
		if(pedido.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			errors.reject("", "Valor total não pode ser negativo");
		}
	}

	private void validarSeInformouItens(Errors errors, Pedido pedido) {
		if(pedido.getItens().isEmpty()) {
			errors.reject("", "Adicione pelo menos uma cerveja no pedido");
		}
	}

	private void validarSeInformouApenasHorarioEntrega(Errors errors, Pedido pedido) {
		if(pedido.getHorarioEntrega() != null && pedido.getDataEntrega() == null) {
			errors.rejectValue("dataEntrega", "", "Informe uma data da entrega para um horário");
		}
	}

}
