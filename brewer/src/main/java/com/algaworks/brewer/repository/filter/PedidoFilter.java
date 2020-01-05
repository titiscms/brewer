package com.algaworks.brewer.repository.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.algaworks.brewer.model.StatusPedido;

public class PedidoFilter {
	
	private Long codigo;
	private StatusPedido status;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private BigDecimal valorMin;
	private BigDecimal valorMax;
	private String nomeCliente;
	private String cpfOuCnpjCliente;
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public StatusPedido getStatus() {
		return status;
	}
	public void setStatus(StatusPedido status) {
		this.status = status;
	}
	public LocalDate getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}
	public LocalDate getDataFim() {
		return dataFim;
	}
	public void setDataFim(LocalDate dataFim) {
		this.dataFim = dataFim;
	}
	public BigDecimal getValorMin() {
		return valorMin;
	}
	public void setValorMin(BigDecimal valorMin) {
		this.valorMin = valorMin;
	}
	public BigDecimal getValorMax() {
		return valorMax;
	}
	public void setValorMax(BigDecimal valorMax) {
		this.valorMax = valorMax;
	}
	public String getNomeCliente() {
		return nomeCliente;
	}
	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}
	public String getCpfOuCnpjCliente() {
		return cpfOuCnpjCliente;
	}
	public void setCpfOuCnpjCliente(String cpfOuCnpjCliente) {
		this.cpfOuCnpjCliente = cpfOuCnpjCliente;
	}
}
