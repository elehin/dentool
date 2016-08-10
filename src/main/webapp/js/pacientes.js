// ################### document.ready() ##################################
$(document).ready(function() {

	getLastChangedPacientes();

	$("#btnSearch").click(function() {
		buscarPaciente();
		return false;
	});

	$('#searchKey').keypress(function(e) {
		if (e.which == '13') {
			buscarPaciente();
		}
	});

	getDatosIngresosMes(); 
});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#ultimosPacientesBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + item.orden + '</td><td>'
				+ lupa + '</td><td>' + item.name + '</td><td>' + item.apellidos
				+ '</td><td>' + item.telefono + '</td><td>' + item.lastChange
				+ '</td></tr>';
	});
	$("#ultimosPacientesBody").append(trHTML);

	searchTable = $('#tableUltimosPacientes')
			.DataTable(
					{
						"retrieve" : false,
						"paging" : false,
						"searching" : false,
						"info" : false,
						"columnDefs" : [ {
							"targets" : [ 0, 1 ],
							"visible" : false
						} ],
						"order" : [ [ 1, "asc" ] ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "Ningún dato disponible en esta tabla",
							"sInfo" : "Mostrando registros del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando registros del 0 al 0 de un total de 0 registros",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						}
					});

	$('#tableUltimosPacientes tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'paciente.html?paciente=' + data[0];
		window.location.replace(url);
	});
}

function buscarPaciente() {
	key = $("#searchKey").val();
	if ($.isNumeric(key)) {
		if (key.length == 9) {
			var url = serverURL + 'pacienteMultiple.html?key=' + key;
			window.location.replace(url);
		} else {
			var url = serverURL + 'paciente.html?paciente=' + key;
			window.location.replace(url);
		}
	} else {
		var url = serverURL + 'pacienteMultiple.html?key=' + key;
		window.location.replace(url);
	}
	return false;
}

function getLastChangedPacientes() {
	$.ajax({
		type : 'GET',
		url : pacienteURL + 'lastChanges',
		// dataType : "json",
		success : function(data) {
			populateTable(data);
		},
		beforeSend : function(xhr, settings) {
			xhr.setRequestHeader('Authorization', 'Bearer '
					+ $.cookie('restTokenC'));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			window.location.replace(serverURL + 'login.html');
		}
	});
}

//------------------------- Datos de altas -----------------------------
//----------------------------------------------------------------------
function getDatosIngresosMes() {
	// console.log('getTratamientosTop');
	$.ajax({
		type : 'GET',
		url : pacienteURL + 'datosMensuales',
		success : function(data) {
			renderChartAltas(data);
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

//----------------------- Gráfico de altas -----------------------------
//----------------------------------------------------------------------
function renderChartAltas(dataFromServer) {

	$.jqplot.config.enablePlugins = true;

	var currentDate = new Date();

	var currentYearData = new Array();
	var previousYearData = new Array();
	var firstYearData = new Array();

	for (var i = 0; i < 12; i++) {
		currentYearData[i] = 0;
		previousYearData[i] = 0;
		firstYearData[i] = 0;
	}

	$.each(dataFromServer, function(i, item) {
		itemDate = new Date(item.fecha);
		if (itemDate.getFullYear() == currentDate.getFullYear()) {
			currentYearData[itemDate.getMonth()] = item.altas;
		} else if (itemDate.getFullYear() == currentDate.getFullYear() - 1) {
			previousYearData[itemDate.getMonth()] = item.altas;
		} else if (itemDate.getFullYear() == currentDate.getFullYear() - 2) {
			firstYearData[itemDate.getMonth()] = item.altas;
		}
	});

	var xticks = new Array();

	$.each(meses, function(i, item) {
		xticks.push(item);
	});

	$.jqplot('chartAltasMes', [ currentYearData, previousYearData,
			firstYearData ], {
		seriesColors : [ "#3366CC", "#5B7290", "#79BEDB", "#10C8CD" ],
		grid : {
			background : '#f9f9f9'
		},
		title : 'Altas de pacientes',
		axesDefaults : {
			labelRenderer : $.jqplot.CanvasAxisLabelRenderer
		},
		seriesDefaults : {
			rendererOptions : {
				smooth : true
			}
		},
		axes : {
			xaxis : {
				renderer : $.jqplot.CategoryAxisRenderer,
				ticks : xticks,
				tickOptions : {
					angle : -30
				},
				tickRenderer : $.jqplot.CanvasAxisTickRenderer
			},
			yaxis : {
				label : "Altas mensuales",
				tickOptions : {
					formatString : '%d pacs.'
				},
				min : 0
			}
		},
		highlighter : {
			sizeAdjust : 10,
			tooltipLocation : 'n',
			tooltipAxes : 'y',
			useAxesFormatters : true
		},
		legend : {
			show : true,
			renderer : $.jqplot.EnhancedLegendRenderer,
			placement : 'outside',
			rendererOptions : {
				numberRows : 3
			},
			location : 'se'
		},
		series : [ {
			label : currentDate.getFullYear(),
			showLabel : true
		}, {
			label : currentDate.getFullYear() - 1,
			showLabel : true
		}, {
			label : currentDate.getFullYear() - 2,
			showLabel : true
		} ]
	});

}