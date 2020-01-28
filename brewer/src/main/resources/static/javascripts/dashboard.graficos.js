var Brewer = Brewer || {};

Brewer.GraficoPedidosPorMes = (function() {
	
	function GraficoPedidosPorMes() {
		this.ctx = $('#graficoPedidosPorMes')[0].getContext('2d');
	}
	
	GraficoPedidosPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'pedidos/totalPorMes',
			method: 'GET',
			success: onDadosRecebidos.bind(this),
		});
	}
	
	function onDadosRecebidos(pedidoMes) {
		var meses = [];
		var valores = [];
		
		pedidoMes.forEach(function(obj) {
			meses.unshift(obj.mes);
			valores.unshift(obj.total);
		});
		
		var graficoPedidosPorMes = new Chart(this.ctx, {
			type: 'line',
			data: {
				labels: meses,
				datasets: [{
					label: 'Pedidos por mês',
					backgroundColor: 'rgba(26,179,148,0.5)',
		            pointBorderColor: 'rgba(26,179,148,1)',
		            pointBackgroundColor: '#FFF',
		            data: valores
				}],
				
			},
		});
	}
	
	return GraficoPedidosPorMes;	
	
}());

$(function() {
	var graficoPedidosPorMes = new Brewer.GraficoPedidosPorMes();
	graficoPedidosPorMes.iniciar();
});