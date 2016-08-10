// ################### document.ready() ##################################
$(document).ready(function() {
	getDatosComerciales();
	getDatosIngresosMes();

});

// ###################### Funciones #######################################
function getDatosComerciales() {
	// console.log('getTratamientosTop');
	$.ajax({
		type : 'GET',
		url : datosComercialesURL,
		success : function(data) {
			renderChartPipeline(data);
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

function renderChartPipeline(dataFromServer) {

	// ---------------------- Gráfico Pipeline -----------------------------
	// ----------------------------------------------------------------------
	var datos = new Array();
	var total = dataFromServer[0].diagnosticado
			+ dataFromServer[0].presupuestado
			+ dataFromServer[0].iniciadoSinPagar + dataFromServer[0].ingresos;

	datos.push([ 'Diagnosticado', total,
			formatCurrency(dataFromServer[0].diagnosticado),
			dataFromServer[0].clientesDiagnosticados,
			(dataFromServer[0].diagnosticado / total * 100) + ' %' ]);
	datos.push([
			'Presupuestado',
			dataFromServer[0].presupuestado
					+ dataFromServer[0].iniciadoSinPagar
					+ dataFromServer[0].ingresos,
			formatCurrency(dataFromServer[0].presupuestado),
			dataFromServer[0].clientesPresupuestados,
			(dataFromServer[0].presupuestado / total * 100) + ' %' ]);
	datos.push([ 'En tratamiento',
			dataFromServer[0].iniciadoSinPagar + dataFromServer[0].ingresos,
			formatCurrency(dataFromServer[0].iniciadoSinPagar),
			dataFromServer[0].clientesIniciadosSinPagar,
			(dataFromServer[0].iniciadoSinPagar / total * 100) + ' %' ]);
	datos.push([ 'Ingresado', dataFromServer[0].ingresos,
			formatCurrency(dataFromServer[0].ingresos),
			dataFromServer[0].clientesTratados,
			(dataFromServer[0].ingresos / total * 100) + ' %' ]);
	var ratio = dataFromServer[0].ingresos / total;

	$.jqplot('chartFunnel', [ datos ], {
		seriesColors : [ "#3399FF", "#3366CC", "#003366", "#79BEDB", "#10C8CD",
				"#5B7290" ],
		grid : {
			background : '#f9f9f9'
		},
		seriesDefaults : {
			renderer : $.jqplot.FunnelRenderer,
			rendererOptions : {
				widthRatio : dataFromServer[0].diagnosticado / total,
				sectionMargin : 5,
				// padding : {
				// top : 20,
				// right : 150,
				// bottom : 110,
				// left : 10
				// },
				showDataLabels : true,
				dataLabels : 'personalLabel'
			}
		},
		title : {
			text : 'Pipeline',
			show : true

		},
		legend : {
			show : true,
			location : 's',
			placement : 'outside',

		}
	});
}

// ------------------------- Datos de ingresos --------------------------
// ----------------------------------------------------------------------
function getDatosIngresosMes() {
	// console.log('getTratamientosTop');
	$.ajax({
		type : 'GET',
		url : reportIngresosURL,
		success : function(data) {
			renderChartIngresos(data);
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

// ----------------------- Gráfico de ingresos --------------------------
// ----------------------------------------------------------------------
function renderChartIngresos(dataFromServer) {

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
			currentYearData[itemDate.getMonth()] = item.ingresos;
		} else if (itemDate.getFullYear() == currentDate.getFullYear() - 1) {
			previousYearData[itemDate.getMonth()] = item.ingresos;
		} else if (itemDate.getFullYear() == currentDate.getFullYear() - 2) {
			firstYearData[itemDate.getMonth()] = item.ingresos;
		}
	});

	var xticks = new Array();

	$.each(meses, function(i, item) {
		xticks.push(item);
	});

	$.jqplot('chartIngresos', [ currentYearData, previousYearData,
			firstYearData ], {
		seriesColors : [ "#3366CC", "#5B7290", "#79BEDB", "#10C8CD" ],
		grid : {
			background : '#f9f9f9'
		},
		title : 'Ingresos',
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
				label : "Ingresos mensuales",
				tickOptions : {
					formatString : '%.2f €'
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
			location : 's',
			marginTop : "60px"
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
