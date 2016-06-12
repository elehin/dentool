var rootURL = 'https://dentool-elehin.rhcloud.com/service/paciente/';
var diagnosticoURL = 'https://dentool-elehin.rhcloud.com/service/diagnostico/';
var tratamientoURL = 'https://dentool-elehin.rhcloud.com/service/tratamiento/';
var tratamientosTopURL = 'https://dentool-elehin.rhcloud.com/service/tratamientoTop';
var serverURL = 'https://dentool-elehin.rhcloud.com/';

//var rootURL = 'http://localhost:8080/service/paciente/';
//var diagnosticoURL = 'http://localhost:8080/service/diagnostico/';
//var tratamientoURL = 'http://localhost:8080/service/tratamiento/';
//var tratamientosTopURL = 'http://localhost:8080/service/tratamientoTop';
// var serverURL = 'http://localhost:8080/';

var currentPaciente;
var searchTable;
var searchDialog;
var activeDiagnostico;

var lupa = '<button class="btn btn-info padding-0-4 detalle" role="button"><span class="glyphicon glyphicon-search"></span></button>';
var sinPagar = '<button class="btn btn-danger padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var pagado = '<button class="btn btn-success padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var pagadoPacial = '<button class="btn btn-warning padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var statusSinEmpezar = '<button class="btn btn-default padding-0-4 sinEmpezar" role="button"><span class="glyphicon glyphicon-play"></span></button>';
var statusEmpezado = '<button class="btn btn-warning padding-0-4 empezado" role="button"><span class="glyphicon glyphicon-adjust"></span></button>';
var statusFinalizado = '<button class="btn btn-success padding-0-4 finalizado" role="button"><span class="glyphicon glyphicon-ok"></span></button>';

$(document).ready(function() {
	if (getUrlParameter("paciente") != '') {
		findPaciente(getUrlParameter("paciente"));
	}

	$("#btnSave").click(function() {
		updatePaciente();
		return false;
	});

	$("#btnAddDiagnostico").click(function() {
		addDiagnostico();
		return false;
	});

	$("#ttbtn1").click(function() {
		addDiagnostico(1);
		return false;
	});

	$("#ttbtn2").click(function() {
		addDiagnostico(2);
		return false;
	});

	$("#ttbtn3").click(function() {
		addDiagnostico(3);
		return false;
	});

	$("#ttbtn4").click(function() {
		addDiagnostico(4);
		return false;
	});

	$("#ttbtn5").click(function() {
		addDiagnostico(5);
		return false;
	});

	$(".botonPieza").click(function(eventObject) {
		$(".botonPieza").removeClass("active");
		$(eventObject.target).toggleClass("active");
		return false;
	});

	getTratamientosList();
	getTratamientosTop();

});

function getTratamientosTop() {
	$.ajax({
		type : 'GET',
		url : tratamientosTopURL,
		// dataType : "json",
		success : function(data) {
			$.each(data, function(i, item) {
				var j = 1 + i;
				var id = "tt" + j;
				var valor = item.nombre + " " + item.precio + " €";
				$("#" + id).attr("value", valor);
				$("#" + id).attr("tratamiento", item.tratamiento);
			});
		}
	});
}

function getTratamientosList() {
	$.ajax({
		type : 'GET',
		url : tratamientoURL,
		// dataType : "json",
		success : function(data) {
			$.each(data,
					function(i, item) {
						$('#tratamiento').append(
								$('<option>').text(item.nombre).attr('value',
										item.id));
					});
			$('#tratamiento').selectpicker('refresh');
		}
	});
}

function addDiagnostico(tratamientoTop) {
	if (tratamientoTop == null) {
		tratamientoTop = 'addDiagnostico';
	}

	$
			.ajax({
				type : 'POST',
				contentType : 'application/json',
				url : diagnosticoURL + 'add',
				data : formToJSON(tratamientoTop),
				success : function(rdata, textStatus, jqXHR) {
					showDiagnosticoSuccessMessage();
					$('#addDiagDiv').toggleClass("in");
					$
							.when($
									.ajax(
											{
												type : 'GET',
												url : jqXHR
														.getResponseHeader('Location'),
												success : function(data) {
													activeDiagnostico = data;
												}
											})
									.done(
											function() {
												var table = $(
														'#tableUltimosTratamientos')
														.DataTable({
															"retrieve" : true
														});
												var estadoPago;
												if (activeDiagnostico.pagado == 0) {
													estadoPago = sinPagar;
												} else if (activeDiagnostico.pagado == activeDiagnostico.precio) {
													estadoPago = pagado;
												} else {
													estestadoPagoado = pagadoPacial;
												}
												var estado
												if (activeDiagnostico.iniciado == false) {
													estado = statusSinEmpezar;
												} else if (activeDiagnostico.iniciado == true
														&& activeDiagnostico.finalizado == false) {
													estado = statusEmpezado;
												} else if (activeDiagnostico.finalizado == true) {
													estado = statusFinalizado;
												}

												var pieza;
												if (pieza == 0) {
													pieza = '';
												} else {
													pieza = activeDiagnostico.pieza;
												}

												table.row
														.add(
																[
																		activeDiagnostico.id,
																		activeDiagnostico.precio,
																		activeDiagnostico.pagado,
																		lupa,
																		estado,
																		estadoPago,
																		activeDiagnostico.tratamiento.nombre,
																		pieza ])
														.draw(false);
												$('#addDiagForm')[0].reset();
											}));

				},
				error : function(jqXHR, textStatus, errorThrown) {
					showErrorMessage(textStatus);
				}
			});
}

function findDiagnosticoByUrl(url) {
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			activeDiagnostico = data;
		}
	});
}

function findPaciente(id) {
	$.ajax({
		type : 'GET',
		url : rootURL + id,
		// dataType : "json",
		success : function(data) {
			currentPaciente = data;
			$("#btnSave").attr('value', 'Modificar');
			renderDetails(currentPaciente);
		}
	});
}

function renderDetails(paciente) {
	showAlergicoMessage();
	showEnfermoGraveMessage();

	$('#pacienteId').val(paciente.id);
	$('#name').val(paciente.name);
	$('#apellidos').val(paciente.apellidos);
	$('#direccion').val(paciente.direccion);
	$('#telefono').val(paciente.telefono);
	$('#fechaNacimiento').val(paciente.fechaNacimiento);
	$('#notas').val(paciente.notas);
	$('#alergico').prop('checked', paciente.alergico);
	$('#enfermoGrave').prop('checked', paciente.enfermoGrave);
	$('#dni').val(paciente.dni);
	$("#btnSave").attr('value', 'Modificar');

	populateLastDiagnosticos();
}

function populateLastDiagnosticos() {
	getDiagnosticosByPaciente(currentPaciente.id);

}

function renderDiagTableRow(item) {

	if (item.pagado == 0) {
		estadoPago = sinPagar;
	} else if (item.pagado == item.precio) {
		estadoPago = pagado;
	} else {
		estadoPago = pagadoPacial;
	}

	var estado;
	if (item.iniciado == false) {
		estado = statusSinEmpezar;
	} else if (item.iniciado == true && item.finalizado == false) {
		estado = statusEmpezado;
	} else if (item.finalizado == true) {
		estado = statusFinalizado;
	}

	var pieza;
	if (item.pieza == 0) {
		pieza = '';
	} else {
		pieza = item.pieza;
	}

	row = [ item.id, item.precio, item.pagado, lupa, estado, estadoPago,
			item.tratamiento.nombre, pieza ];

	return row;
}

function renderTableDiagnosticos(diagnosticos) {

	var dataset = [];

	$.each(diagnosticos, function(i, item) {
		dataset.push(renderDiagTableRow(item));
	});

	diagsTable = $('#tableUltimosTratamientos').DataTable({
		"retrieve" : true,
		"paging" : false,
		"searching" : false,
		"info" : false,
		"ordering" : false,
		"data" : dataset,
		"columns" : [ {
			"title" : "id"
		}, {
			"title" : "precio"
		}, {
			"title" : "pagado"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "Tratamiento"
		}, {
			"title" : "Pieza"
		} ],
		"columnDefs" : [ {
			"className" : "never",
			"targets" : [ 0, 1, 2 ],
			"visible" : false
		} ],
	});

	var row;
	$('#tableUltimosTratamientos tbody').on(
			'click',
			'button',
			function() {
				row = $(this).parents('tr');
				if ($(this).hasClass("pagar")) {
					setPagado(row);
				} else if ($(this).hasClass("detalle")) {
					var data = diagsTable.row($(this).parents('tr')).data();
					url = serverURL + 'diagnostico.html?paciente='
							+ currentPaciente.id + '&diagnostico=' + data[0];
					window.location.replace(url);
				} else if ($(this).is('.sinEmpezar, .empezado')) {
					setFinalizado(row);
				}

			});

}

function setFinalizado(row) {
	var data = diagsTable.row(row).data();
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'update',
		data : formToJSON('updateEstadoDiagnostico', data),
		success : function(rdata, textStatus, jqXHR) {
			showDiagnosticoUpdateSuccessMessage();
			$.when($.ajax({
				type : 'GET',
				url : jqXHR.getResponseHeader('Location'),
				success : function(data) {
					activeDiagnostico = data;
				}
			})).done(
					function() {
						diagsTable.row(row).data(
								renderDiagTableRow(activeDiagnostico)).draw();
					})

		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(textStatus);
		}
	});
}

function setPagado(row) {
	var data = diagsTable.row(row).data();
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'update',
		data : formToJSON('updateDiagnostico', data),
		success : function(rdata, textStatus, jqXHR) {
			showDiagnosticoUpdateSuccessMessage();
			$.when($.ajax({
				type : 'GET',
				url : jqXHR.getResponseHeader('Location'),
				success : function(data) {
					activeDiagnostico = data;
				}
			})).done(
					function() {
						diagsTable.row(row).data(
								renderDiagTableRow(activeDiagnostico)).draw();
					})

		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(textStatus);
		}
	});

}

function getDiagnosticosByPaciente(paciente) {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + 'paciente/' + paciente,
		success : function(data) {
			renderTableDiagnosticos(data);
		}
	});
}

var getUrlParameter = function getUrlParameter(sParam) {
	var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL
			.split('&'), sParameterName, i;

	for (i = 0; i < sURLVariables.length; i++) {
		sParameterName = sURLVariables[i].split('=');

		if (sParameterName[0] === sParam) {
			return sParameterName[1] === undefined ? true : sParameterName[1];
		}
	}
};

function updatePaciente() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL + 'update',
		data : formToJSON('modificar'),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
			findPacienteByUrl(jqXHR.getResponseHeader('Location'));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(textStatus);
		}
	});
}

function formToJSON(action, data) {
	if (action == '1') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tt1').attr("tratamiento")
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == '2') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tt2').attr("tratamiento")
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == '3') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tt3').attr("tratamiento")
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == '4') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tt4').attr("tratamiento")
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == '5') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tt5').attr("tratamiento")
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == 'addDiagnostico') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tratamiento').val()
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false,
			"pieza" : $(".botonPieza.active").text()
		});
	} else if (action == 'updateDiagnostico') {
		return JSON.stringify({
			"id" : data[0],
			"pagado" : data[1],
			"precio" : data[1]
		});
	} else if (action == 'updateEstadoDiagnostico') {
		return JSON.stringify({
			"id" : data[0],
			"fechaFin" : new Date(),
			"pagado" : data[2],
			"precio" : data[1]
		});
	} else {
		return JSON.stringify({
			"id" : $('#pacienteId').val(),
			"name" : $('#name').val(),
			"apellidos" : $('#apellidos').val(),
			"direccion" : $('#direccion').val(),
			"telefono" : $('#telefono').val(),
			"fechaNacimiento" : $('#fechaNacimiento').val(),
			"notas" : $('#notas').val(),
			"dni" : $('#dni').val(),
			"alergico" : $('#alergico').prop('checked'),
			"enfermoGrave" : $('#enfermoGrave').prop('checked')
		});
	}
}

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#success-alert").hide();
		});
	}, 0);
}

function showDiagnosticoSuccessMessage() {
	$("#diagnostico-success-alert").alert();
	window.setTimeout(function() {
		$("#diagnostico-success-alert").fadeTo(2000, 500).slideUp(500,
				function() {
					$("#diagnostico-success-alert").hide();
				});
	}, 0);
}

function showDiagnosticoUpdateSuccessMessage() {
	$("#diagnostico-update-success-alert").alert();
	window.setTimeout(function() {
		$("#diagnostico-update-success-alert").fadeTo(2000, 500).slideUp(500,
				function() {
					$("#diagnostico-update-success-alert").hide();
				});
	}, 0);
}

function showErrorMessage(error) {
	$("#error-alert").alert();
	window.setTimeout(function() {
		$("#error-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#error-alert").hide();
		});
	}, 0);
}

function showAlergicoMessage() {
	if (currentPaciente.alergico == true) {
		$("#alergia-alert").show()
	}
}

function showEnfermoGraveMessage() {
	if (currentPaciente.enfermoGrave == true) {
		$("#enfermoGrave-alert").show()
	}
}

function findPacienteByUrl(url) {
	$.ajax({
		type : 'GET',
		url : url,
		// dataType : "json",
		success : function(data) {
			currentPaciente = data;
			renderDetails(currentPaciente);
		}
	});
}
