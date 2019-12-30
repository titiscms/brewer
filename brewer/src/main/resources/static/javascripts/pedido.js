Brewer.Pedido = (function() {
	
	function Pedido(tabelaItens) {
		this.tabelaItens = tabelaItens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		this.containerValorBox = $('.js-container-valor-box');
		
		this.valorTotalItens = 0;
		this.valorFrete = 0;
		this.valorDesconto = 0;
	}
	
	Pedido.prototype.iniciar = function () {
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));
		
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
	}
	
	function onTabelaItensAtualizada(evento, valorTotalItens) {
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	function onValorFreteAlterado(evento) {
		this.valorFrete = Brewer.recuperarValor($(evento.target).val());
	}
	
	function onValorDescontoAlterado(evento) {
		this.valorDesconto = Brewer.recuperarValor($(evento.target).val());
	}
	
	function onValoresAlterados() {
		var valorTotal = this.valorTotalItens + this.valorFrete - this.valorDesconto; 
		this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));
		
		this.containerValorBox.toggleClass('box-valor-negativo', valorTotal < 0);
	}

	return Pedido;
	
}());

$(function() {
		
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
	var pedido = new Brewer.Pedido(tabelaItens);
	pedido.iniciar();
	
});