// ################### document.ready() ##################################
$(document).ready(function() {

	getTratamientos();
	getTratamientosTop();

	$("#btnSearch").click(function() {
		buscarTratamiento();
		return false;
	});

	$('#searchKey').keypress(function(e) {
		if (e.which == '13') {
			buscarTratamiento();
		}
	});

});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#tableTratamientosBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.nombre + '</td><td>' + item.precio + '</td></tr>';
	});
	$("#tableTratamientosBody").append(trHTML);

	searchTable = $('#tableTratamientos').DataTable({
		"retrieve" : false,
		"paging" : true,
		"searching" : false,
		"info" : true,
		"columnDefs" : [ {
			"targets" : [ 0 ],
			"visible" : false
		} ],
		"order" : [ 3, "asc" ],
		"language" : {
			"search" : "Buscar:",
			"sLengthMenu" : "Mostrar _MENU_ registros",
			"sZeroRecords" : "No se encontraron resultados",
			"sEmptyTable" : "Ningún dato disponible en esta tabla",
			"sInfo" : "Total: _TOTAL_ tratamientos",
			"sInfoEmpty" : "No hay ningún tratamiento",
			"oPaginate" : {
				"sFirst" : "Primero",
				"sLast" : "Último",
				"sNext" : "Siguiente",
				"sPrevious" : "Anterior"
			}
		}
	});

	$('#tableTratamientos tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'tratamiento.html?tratamiento=' + data[0];
		window.location.replace(url);
	});
}

function buscarTratamiento() {
	key = $("#searchKey").val();

	var url = serverURL + 'tratamientoMultiple.html?key=' + key;
	window.location.replace(url);
}

function getTratamientos() {
	$.ajax({
		type : 'GET',
		url : rootURL,
		// dataType : "json",
		success : function(data) {
			populateTable(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (errorThrown == 'Unauthorized') {
				window.location.replace(serverURL + 'login.html');
			} else {
				showErrorMessage(textStatus);
			}
		},
		beforeSend : function(xhr, settings) {
			xhr.setRequestHeader('Authorization', 'Bearer '
					+ $.cookie('restTokenC'));
		}
	});
}

function renderCharts(dataFromServer) {

	var tratamientos = new Array();
	var totalTratamientos;
	var porcentajeOtros = 100;
	var totalOtros = 0;

	$.each(dataFromServer,
			function(i, item) {
				tratamientos.push([ item.nombre, item.count,
						item.porcentajeLastYear ]);
				totalTratamientos = item.totalLastYear;
				porcentajeOtros -= item.porcentajeLastYear;
				totalOtros += item.count;
			});
	totalOtros = totalTratamientos - totalOtros;
	tratamientos.push([ 'Otros', totalOtros, porcentajeOtros ]);

	$.jqplot('chartdivMasUsados', [ tratamientos ], {
		gridPadding : {
			top : 0,
			bottom : 38,
			left : 0,
			right : 0
		},
		grid : {
			background : '#f9f9f9'
		},
		sortData : true,
		seriesColors : [ "#3399FF", "#3366CC", "#003366", "#79BEDB", "#10C8CD",
				"#5B7290" ],
		seriesDefaults : {
			renderer : $.jqplot.PieRenderer,
			trendline : {
				show : false
			},
			rendererOptions : {
				padding : 8,
				showDataLabels : true,
				dataLabels : 'value',
				sliceMargin : 5
			}
		},
		legend : {
			show : true,
			rendererOptions : {
				numberRows : 5
			},
			location : 'e'

		}
	});

	$(
			'<div class="my-jqplot-title" style="position:absolute;text-align:right;padding-top: 15px;padding-right: 5px;width:100%">Tratamientos más aplicados</div>')
			.insertAfter('#chartdivMasUsados .jqplot-grid-canvas');

	$('#chartdivMasUsados').bind(
			'jqplotDataHighlight',
			function(ev, seriesIndex, pointIndex, data) {
				var $this = $(this);
				var porcentaje = new Number(data[2]);
				$this.attr('title', data[0] + ": " + data[1] + " - "
						+ porcentaje.toFixed(1) + "%");
			});

	$('#chartdivMasUsados').bind('jqplotDataUnhighlight', function(ev) {
		var $this = $(this);

		$this.attr('title', "");
	});

	var facturacion = new Array();
	var totalFacturacionLastYear;
	var facturacionOtrosLastYear = 0;
	var porcentajeFactOtrosLastYear = 100;

	$.each(dataFromServer, function(i, item) {
		facturacion.push([ item.nombre, item.facturadoLastYear,
				item.porcentajeFacturacionLastYear ]);
		totalFacturacionLastYear = item.totalFacturadoLastYear;
		facturacionOtrosLastYear += item.facturadoLastYear
		porcentajeFactOtrosLastYear -= item.porcentajeFacturacionLastYear;
	});
	facturacionOtrosLastYear = totalFacturacionLastYear
			- facturacionOtrosLastYear;
	facturacion.push([ 'Otros', facturacionOtrosLastYear,
			porcentajeFactOtrosLastYear ]);

	$.jqplot('chartdivMayorFacturacion', [ facturacion ], {
		gridPadding : {
			top : 0,
			bottom : 38,
			left : 0,
			right : 0
		},
		grid : {
			background : '#f9f9f9'
		},
		sortData : true,
		seriesColors : [ "#3399FF", "#3366CC", "#003366", "#79BEDB", "#10C8CD",
				"#5B7290" ],
		seriesDefaults : {
			renderer : $.jqplot.PieRenderer,
			trendline : {
				show : false
			},
			rendererOptions : {
				padding : 8,
				showDataLabels : true,
				dataLabels : 'value',
				sliceMargin : 5,
				dataLabelFormatString : '%d €'
			}
		},
		legend : {
			show : true,
			rendererOptions : {
				numberRows : 5
			},
			location : 'e'
		}
	});

	$(
			'<div class="my-jqplot-title" style="position:absolute;text-align:right;padding-top: 15px;padding-right: 5px;width:100%">Facturación tratamientos top</div>')
			.insertAfter('#chartdivMayorFacturacion .jqplot-grid-canvas');

	$('#chartdivMayorFacturacion').bind(
			'jqplotDataHighlight',
			function(ev, seriesIndex, pointIndex, data) {
				var $this = $(this);
				var porcentaje = new Number(data[2]);
				$this.attr('title', data[0] + ": " + formatCurrency(data[1])
						+ " - " + porcentaje.toFixed(1) + "%");
			});

	$('#chartdivMayorFacturacion').bind('jqplotDataUnhighlight', function(ev) {
		var $this = $(this);

		$this.attr('title', "");
	});
}

function getTratamientosTop() {
	// console.log('getTratamientosTop');
	$.ajax({
		type : 'GET',
		url : tratamientosTopURL,
		// dataType : "json",
		success : function(data) {
			renderCharts(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (errorThrown == 'Unauthorized') {
				window.location.replace(serverURL + 'login.html');
			}
		},
		beforeSend : function(xhr, settings) {
			xhr.setRequestHeader('Authorization', 'Bearer '
					+ $.cookie('restTokenC'));
		}
	});
}