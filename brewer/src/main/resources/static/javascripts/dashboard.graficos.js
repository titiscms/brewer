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

Brewer.GraficoPedidosPorOrigem = (function() {
	
	function GraficoPedidosPorOrigem() {
		this.ctx = $('#graficoPedidosPorOrigem')[0].getContext('2d');
	}
	
	GraficoPedidosPorOrigem.prototype.iniciar = function() {
		$.ajax({
			url: 'pedidos/porOrigem',
			method: 'GET', 
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(pedidoOrigem) {
		var meses = [];
		var cervejasNacionais = [];
		var cervejasInternacionais = [];
		
		pedidoOrigem.forEach(function(obj) {
			meses.unshift(obj.mes);
			cervejasNacionais.unshift(obj.totalNacional);
			cervejasInternacionais.unshift(obj.totalInternacional)
		});
		
		var graficoPedidosPorOrigem = new Chart(this.ctx, {
		    type: 'bar',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Nacional',
		    		backgroundColor: "rgba(220,220,220,0.5)",
	                data: cervejasNacionais
		    	},
		    	{
		    		label: 'Internacional',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                data: cervejasInternacionais
		    	}]
		    },
		});
	}
	
	return GraficoPedidosPorOrigem;
	
}());

$(function() {
	
	var graficoPedidosPorMes = new Brewer.GraficoPedidosPorMes();
	graficoPedidosPorMes.iniciar();
	
	var graficoPedidosPorOrigem = new Brewer.GraficoPedidosPorOrigem();
	graficoPedidosPorOrigem.iniciar();
	
});