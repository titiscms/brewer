Brewer = Brewer || {}

Brewer.DialogoExcluir = (function() {
	
	function DialogoExcluir() {
		this.exclusaoBtn = $('.js-exclusao-btn');
	}
	
	DialogoExcluir.prototype.iniciar = function() {
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
		
		if(window.location.search.indexOf('excluido') > -1) {
			swal('Pronto!', 'Excluído com sucesso!', 'success');
		}
	}
	
	function onExcluirClicado(evento) {
		event.preventDefault();
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');

		swal({
			  title: 'Você tem certeza?',
			  text: 'Excluir "' + objeto + '"? Você não poderá recuperar depois.',
			  icon: 'warning',
			  animation: true,
			  confirmButtonColor: '#dd6b55',
			  confirmButtonText: 'Sim, excluir agora!',
			  showCancelButton: true,
			  closeOnConfirm: false
				
			}, onExcluirConfirmado.bind(this, url), 'warning');
		
		this.removeStyleDiv = $('div.sa-icon.sa-warning').removeAttr('style');
	}
	
	function onExcluirConfirmado(url) {
		$.ajax({
			url: url,
			method: 'DELETE',
			success: onExcluirSucesso.bind(this),
			error: onErroExcluir.bind(this)
		})
	}
	
	function onExcluirSucesso() {
		var urlAtual = window.location.href;
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';
		
		window.location = novaUrl;
	}
	
	function onErroExcluir(e) {
		swal('Oops!', e.responseText, 'error');
	}
	
	return DialogoExcluir;
	
}());

$(function() {
	
	var dialogoExcluir = new Brewer.DialogoExcluir();
	dialogoExcluir.iniciar();
	
});