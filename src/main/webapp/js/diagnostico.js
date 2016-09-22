var currentDiagnostico;
var currentPaciente;
var activePago;
var pagosTable;

var suprimir = '<button class="btn btn-danger padding-0-4 eliminar" role="button"><span class="glyphicon glyphicon-remove"></span></button>';
var devolver = '<button class="btn btn-warning padding-0-4 devolver" role="button"><span class="glyphicon glyphicon-chevron-left"></span></button>';

$(document).ready(
		function() {
			if (getUrlParameter("diagnostico") != '') {
				findTratamiento(getUrlParameter("diagnostico"));
			}

			if (getUrlParameter("paciente") != '') {
				findPaciente(getUrlParameter("paciente"));
				$('#backArrowLink').attr(
						"href",
						serverURL + 'paciente.html?paciente='
								+ getUrlParameter("paciente"));
			}

			$("#btnSave").click(function() {
				updateTratamiento();
				return false;
			});

			$("#btnDelete").click(function() {
				deleteTratamiento();
				return false;
			});

			$("#btnAddPago").click(function() {
				addPago();
				return false;
			});
			pagoRestanteBtn

			$("#pagoRestanteBtn").click(function() {
				addPagoRestante();
				return false;
			});

			$(".botonPieza").click(function(eventObject) {
				$(".botonPieza").removeClass("active");
				$(eventObject.target).toggleClass("active");
				return false;
			});

			$("#precio, #cantidad").focus(function() {
				$(this).select();
				return false;
			})

			$("#cambiarOdontogramaBtn").click(function() {
				cambiaOdontograma();
				return false;
			});
		});

function addPagoRestante() {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : pagosURL + 'create',
		data : formToJSON('addPagoRestante'),
		success : function(rdata, textStatus, jqXHR) {
			showPagoSuccessMessage();
			$.when(
					$.ajax({
						type : 'GET',
						url : jqXHR.getResponseHeader('Location'),
						success : function(data) {
							activePago = data;
						},
						error : function(jqXHR, textStatus, errorThrown) {
							if (errorThrown == 'Unauthorized') {
								window.location.replace(serverURL
										+ 'login.html');
							}
						},
						beforeSend : function(xhr, settings) {
							xhr.setRequestHeader('Authorization', 'Bearer '
									+ $.cookie('restTokenC'));
						}
					})).done(
					function() {
						var table = $('#tablePagos').DataTable({
							"retrieve" : true
						});

						table.row.add(
								[ activePago.id, activePago.fecha,
										activePago.cantidad, '€',
										suprimir + ' ' + devolver ])
								.draw(false);

						$.ajax({
							type : 'GET',
							url : diagnosticoURL + currentDiagnostico.id,
							success : function(data) {
								currentDiagnostico = data;
								renderDetails(data);
								actualizarPagoRestante();
							},
							error : function(jqXHR, textStatus, errorThrown) {
								if (errorThrown == 'Unauthorized') {
									window.location.replace(serverURL
											+ 'login.html');
								}
							},
							beforeSend : function(xhr, settings) {
								xhr.setRequestHeader('Authorization', 'Bearer '
										+ $.cookie('restTokenC'));
							}
						});

					});
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

function addPago() {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : pagosURL + 'create',
		data : formToJSON('addPago'),
		success : function(rdata, textStatus, jqXHR) {
			showPagoSuccessMessage();
			// $('#addPagoDiv').toggleClass("in");
			$.when(
					$.ajax({
						type : 'GET',
						url : jqXHR.getResponseHeader('Location'),
						success : function(data) {
							activePago = data;
						},
						error : function(jqXHR, textStatus, errorThrown) {
							if (errorThrown == 'Unauthorized') {
								window.location.replace(serverURL
										+ 'login.html');
							}
						},
						beforeSend : function(xhr, settings) {
							xhr.setRequestHeader('Authorization', 'Bearer '
									+ $.cookie('restTokenC'));
						}
					})).done(
					function() {
						var table = $('#tablePagos').DataTable({
							"retrieve" : true
						});

						table.row.add(
								[ activePago.id, activePago.fecha,
										activePago.cantidad, '€',
										suprimir + ' ' + devolver ])
								.draw(false);
						$("#addPagoForm")[0].reset();

						$.ajax({
							type : 'GET',
							url : diagnosticoURL + currentDiagnostico.id,
							success : function(data) {
								currentDiagnostico = data;
								renderDetails(data);
								actualizarPagoRestante();
							},
							error : function(jqXHR, textStatus, errorThrown) {
								if (errorThrown == 'Unauthorized') {
									window.location.replace(serverURL
											+ 'login.html');
								}
							},
							beforeSend : function(xhr, settings) {
								xhr.setRequestHeader('Authorization', 'Bearer '
										+ $.cookie('restTokenC'));
							}
						});

					});
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

function deleteTratamiento() {
	$.ajax({
		type : 'DELETE',
		contentType : 'application/json',
		url : diagnosticoURL + 'delete/' + $('#diagnosticoId').val(),
		success : function(rdata, textStatus, jqXHR) {
			url = serverURL + 'paciente.html?paciente='
					+ getUrlParameter("paciente");
			window.location.replace(url);
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

function updateTratamiento() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'update',
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
			findTratamiento(getUrlParameter("diagnostico"));
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

function findPaciente(id) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + id,
		success : function(data) {
			currentPaciente = data;
			var paciente = data.name + ' ' + data.apellidos;
			$('#backArrowLink').after(paciente);
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

function findTratamiento(id) {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + id,
		success : function(data) {
			currentDiagnostico = data;
			renderDetails(data);
			getPagos();
			actualizarPagoRestante();
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

function actualizarPagoRestante() {
	var pagoRestante = currentDiagnostico.precio - currentDiagnostico.pagado;
	$("#pagoRestante").attr('pago', pagoRestante);
	$("#pagoRestante").val("Añadir pago por " + pagoRestante + " €");

	if (pagoRestante == 0) {
		$("#hPanelPagoRestante").text(
				'+' + formatCurrency(currentDiagnostico.precio));
		$("#hPanelPagoRestante").removeClass('text-danger');
		$("#hPanelPagoRestante").addClass('text-success');
	} else if (pagoRestante < 0) {
		var saldoPositivo = currentDiagnostico.pagado;
		$("#hPanelPagoRestante").text('+' + formatCurrency(saldoPositivo));
		$("#hPanelPagoRestante").removeClass('text-danger');
		$("#hPanelPagoRestante").addClass('text-success');
	} else {
		$("#hPanelPagoRestante").text('-' + formatCurrency(pagoRestante));
		$("#hPanelPagoRestante").removeClass('text-success');
		$("#hPanelPagoRestante").addClass('text-danger');
	}
}

function renderDetails(diagnostico) {
	$('#diagnosticoId').val(diagnostico.id);
	$('#tratamiento').val(diagnostico.tratamiento.nombre);
	$('#diagnosticado').val(diagnostico.diagnosticado);
	$('#fechaInicio').val(diagnostico.fechaInicio);
	$('#fechaFin').val(diagnostico.fechaFin);
	$('#precio').val(diagnostico.precio);
	$('#pagado').val(diagnostico.pagado);
	$('#notas').val(diagnostico.notas);
	$('#descuento').val(formatPorcentaje(diagnostico.descuento));
	if (diagnostico.pieza != 0) {
		if (diagnostico.pieza > 50) {
			cambiaOdontograma();
		}
		$("#piezaBtn" + diagnostico.pieza).addClass("active");
	}
}

function formToJSON(action) {
	if (action == 'addPago') {
		return JSON.stringify({
			"cantidad" : $('#cantidad').val(),
			"fecha" : new Date(),
			"diagnosticoId" : $('#diagnosticoId').val()
		});
	} else if (action == 'addPagoRestante') {
		return JSON.stringify({
			"cantidad" : $('#pagoRestante').attr('pago'),
			"fecha" : new Date(),
			"diagnosticoId" : $('#diagnosticoId').val()
		});
	} else {
		return JSON.stringify({
			"id" : $('#diagnosticoId').val(),
			"precio" : $('#precio').val(),
			"pagado" : $('#pagado').val(),
			"diagnosticado" : $('#diagnosticado').val(),
			"fechaInicio" : $('#fechaInicio').val(),
			"fechaFin" : $('#fechaFin').val(),
			"pieza" : $(".botonPieza.active").text(),
			"notas" : $("#notas").val()
		});
	}
}

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
			$("#success-alert").hide();
		});
	}, 0);
}

function showPagoSuccessMessage() {
	$("#success-pago-alert").alert();
	window.setTimeout(function() {
		$("#success-pago-alert").fadeTo(1000, 500).slideUp(500, function() {
			$("#success-pago-alert").hide();
		});
	}, 0);
}

function showErrorMessage(error) {
	$("#error-alert").alert();
	window.setTimeout(function() {
		$("#error-alert").fadeTo(10000, 500).slideUp(500, function() {
			$("#error-alert").hide();
		});
	}, 0);
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

function getPagos() {
	$.ajax({
		type : 'GET',
		url : pagosURL + 'diagnostico/' + $("#diagnosticoId").val(),
		success : function(data) {
			renderTablePagos(data);
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

function renderTablePagos(pagos) {

	var dataset = [];

	$.each(pagos, function(i, item) {
		dataset.push([ item.id, item.fecha, item.cantidad, '€',
				suprimir + ' ' + devolver ]);
	});

	pagosTable = $('#tablePagos').DataTable({
		"retrieve" : true,
		"paging" : false,
		"searching" : false,
		"info" : false,
		"ordering" : false,
		"data" : dataset,
		"columns" : [ {
			"title" : "id"
		}, {
			"title" : "Fecha"
		}, {
			"title" : "Cantidad"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "&nbsp;"
		} ],
		"columnDefs" : [ {
			"className" : "never",
			"targets" : [ 0 ],
			"visible" : false
		}, {
			"className" : "dt-right",
			"targets" : [ 2 ]
		}, {
			"className" : "dt-left",
			"targets" : [ 3 ]
		} ],
	});

	var row;
	$('#tablePagos tbody').on('click', 'button', function() {
		row = $(this).parents('tr');
		if ($(this).hasClass("eliminar")) {
			deletePago(row);
		} else if ($(this).hasClass("devolver")) {
			devuelvePago(row);
		}

	});

}

function deletePago(row) {
	var data = pagosTable.row(row).data();
	$.ajax({
		type : 'DELETE',
		contentType : 'application/json',
		url : pagosURL + 'delete/' + data[0],
		success : function(rdata, textStatus, jqXHR) {
			pagosTable.row(row).remove().draw();
			showPagoSuccessMessage();
			$.ajax({
				type : 'GET',
				url : diagnosticoURL + currentDiagnostico.id,
				success : function(data) {
					currentDiagnostico = data;
					renderDetails(data);
					actualizarPagoRestante();
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

function devuelvePago(row) {
	var data = pagosTable.row(row).data();
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : pagosURL + 'enviaPagoASaldo/' + data[0],
		success : function(rdata, textStatus, jqXHR) {
			pagosTable.row(row).remove().draw();
			showPagoSuccessMessage();
			$.ajax({
				type : 'GET',
				url : diagnosticoURL + currentDiagnostico.id,
				success : function(data) {
					currentDiagnostico = data;
					renderDetails(data);
					actualizarPagoRestante();
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

function cambiaOdontograma() {
	$(".odontograma").toggleClass("in");
}
