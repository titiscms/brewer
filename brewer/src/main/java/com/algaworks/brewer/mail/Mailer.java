package com.algaworks.brewer.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Pedido;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Async
	public void enviar(Pedido pedido) {
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("titisbackup@gmail.com");
		mensagem.setTo(pedido.getCliente().getEmail());
		mensagem.setSubject("Pedido Efetuado");
		mensagem.setText("Obrigado, seu pedido foi processado!");
		
		mailSender.send(mensagem);
	}
}
