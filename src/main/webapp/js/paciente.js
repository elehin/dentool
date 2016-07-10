var currentPaciente;
var searchTable;
var searchDialog;
var activeDiagnostico;

var lupa = '<button class="btn btn-info padding-0-4 detalle" role="button"><span class="glyphicon glyphicon-search"></span></button>';
var descarga = '<button class="btn btn-info padding-0-4 detalle" role="button"><span class="glyphicon glyphicon-download-alt"></span></button>';
var sinPagar = '<button class="btn btn-danger padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var pagado = '<button class="btn btn-success padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var pagadoPacial = '<button class="btn btn-warning padding-0-4 pagar" role="button"><span class="glyphicon glyphicon-euro"></span></button>';
var statusSinEmpezar = '<button class="btn btn-default padding-0-4 sinEmpezar" role="button"><span class="glyphicon glyphicon-expand"></span></button>';
var statusEmpezado = '<button class="btn btn-warning padding-0-4 empezado" role="button"><span class="glyphicon glyphicon-adjust"></span></button>';
var statusFinalizado = '<button class="btn btn-success padding-0-4 finalizado" role="button"><span class="glyphicon glyphicon-check"></span></button>';

$(document).ready(
		function() {
			// console.log('$(document).ready');

			$("input:text, #telefono").focus(function() {
				$(this).select();
				return false;
			});

			$("#cantidadSaldo").focus(function() {
				$(this).select();
				return false;
			})

			if (getUrlParameter("paciente") != '') {
				findPaciente(getUrlParameter("paciente"));
			}

			$("#btnSave").click(function() {
				// console.log('$("#btnSave").click');
				$('html, body').animate({
					scrollTop : $("#btnSave").offset().top
				}, 500);
				updatePaciente();
				return false;
			});

			$("#btnAddDiagnostico").click(function() {
				// console.log('$("#btnAddDiagnostico").click');
				addDiagnostico();
				return false;
			});

			$("#ttbtn1").click(function() {
				// console.log('$("#ttbtn1").click');
				addDiagnostico(1);
				return false;
			});

			$("#ttbtn2").click(function() {
				// console.log('$("#ttbtn2").click');
				addDiagnostico(2);
				return false;
			});

			$("#ttbtn3").click(function() {
				// console.log('$("#ttbtn3").click');
				addDiagnostico(3);
				return false;
			});

			$("#ttbtn4").click(function() {
				// console.log('$("#ttbtn4").click');
				addDiagnostico(4);
				return false;
			});

			$("#ttbtn5").click(function() {
				// console.log('$("#ttbtn5").click');
				addDiagnostico(5);
				return false;
			});

			$(".botonPieza").click(function(eventObject) {
				// console.log('$("#botonPieza").click');
				$(".botonPieza").removeClass("active");
				$(eventObject.target).toggleClass("active");
				return false;
			});

			$("#btnAddSaldo").click(function() {
				// console.log('$("#btnAddSaldo").click');
				updatePaciente();
				$('#addSaldoDiv').toggleClass("in");
				return false;
			});

			$("#btnCreatePresupuesto").click(
					function() {
						// console.log('$("#btnAddSaldo").click');
						window.location.replace(serverURL
								+ 'presupuesto.html?paciente='
								+ getUrlParameter("paciente"));
						return false;
					});

			// $("#linkPresupuestosTab").click(function() {
			// getPresupuestos();
			// return false;
			// });

			getTratamientosList();
			getTratamientosTop();
			getPresupuestos();

			// $("#addSaldoLink").click(function() {
			// $('html, body').animate({
			// scrollTop : $("#addSaldoLink").offset().top
			// }, 500);
			// });

		});

function getPresupuestos() {
	$.ajax({
		type : 'GET',
		url : presupuestoURL + "paciente/" + getUrlParameter("paciente"),
		success : function(data) {
			renderTablePresupuestos(data);
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

function renderTablePresupuestos(presupuestos) {
	var dataset = [];

	$.each(presupuestos, function(i, item) {

		dataset.push([ item.id, descarga, item.fecha, item.precio + ' €' ]);
	});

	presupuestosTable = $('#tablePresupuestos').DataTable({
		"retrieve" : true,
		"paging" : false,
		"searching" : false,
		"info" : false,
		"ordering" : false,
		"data" : dataset,
		"columns" : [ {
			"title" : "id"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "Fecha"
		}, {
			"title" : "Precio"
		} ],
		"columnDefs" : [ {
			"className" : "never",
			"targets" : [ 0 ],
			"visible" : false
		} ],
		"order" : [ [ 2, "desc" ] ]
	});

	$('#tablePresupuestos tbody tr').off('click');
	$('#tablePresupuestos tbody tr').on('click', 'button', function(evt) {
		evt.stopPropagation();
		evt.preventDefault();

		row = $(this).parents('tr');

		if ($(this).hasClass("detalle")) {
			var data = presupuestosTable.row($(this).parents('tr')).data();
			descargaPresupuesto(data);
		}
	});
}

function getTratamientosTop() {
	// console.log('getTratamientosTop');
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

function getTratamientosList() {
	// console.log('getTratamientosList');
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

function addDiagnostico(tratamientoTop) {
	// console.log('addDiagnostico');
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
					$('.botonPieza').removeClass('active');
					$('#addDiagForm')[0].reset();
					$
							.when($
									.ajax(
											{
												type : 'GET',
												url : jqXHR
														.getResponseHeader('Location'),
												success : function(data) {
													activeDiagnostico = data;
												},
												error : function(jqXHR,
														textStatus, errorThrown) {
													if (errorThrown == 'Unauthorized') {
														window.location
																.replace(serverURL
																		+ 'login.html');
													}
												},
												beforeSend : function(xhr,
														settings) {
													xhr
															.setRequestHeader(
																	'Authorization',
																	'Bearer '
																			+ $
																					.cookie('restTokenC'));
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

												setTableButtonsClickListeners();
											}));

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

function findDiagnosticoByUrl(url) {
	// console.log('findDiagnosticoByUrl');
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			activeDiagnostico = data;
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

function findPaciente(id) {
	// console.log('findPaciente');
	$.ajax({
		type : 'GET',
		url : pacienteURL + id,
		// dataType : "json",
		success : function(data) {
			currentPaciente = data;
			$("#btnSave").attr('value', 'Modificar');
			renderDetails(currentPaciente);
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

function renderDetails(paciente) {
	// console.log('renderDetails');
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
	renderSaldo();
	renderPagosPendientes();
}

function renderPagosPendientes() {
	// console.log('renderPagosPendientes');
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + "pagosPendientes/" + currentPaciente.id,
		// dataType : "json",
		success : function(data) {
			if (data > 0) {
				$("#pagosPendientesPanel").addClass("in");
				// $("#hPagosPendientes").text("Deuda -" + data + " €");
				$("#hPagosPendientes").html(
						"<span class='glyphicon glyphicon-flag'></span> Deuda -"
								+ data + " €");
			} else {
				$("#pagosPendientesPanel").removeClass("in");
			}
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

function populateLastDiagnosticos() {
	// console.log('populateLastDiagnosticos');
	getDiagnosticosByPaciente(currentPaciente.id);

}

function renderDiagTableRow(item) {
	// console.log('renderDiagTableRow');

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
	// console.log('renderTableDiagnosticos');

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

	setTableButtonsClickListeners();

}

function setTableButtonsClickListeners() {
	var row;

	$('#tableUltimosTratamientos tbody tr').off('click');
	$('#tableUltimosTratamientos tbody tr').on(
			'click',
			'button',
			function(evt) {
				evt.stopPropagation();
				evt.preventDefault();

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

function descargaPresupuesto(data) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', presupuestoURL + 'pdf/' + data[0], true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie('restTokenC'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var myBlob = this.response;
			var blob = new Blob([ myBlob ]);
			var link = document.createElement('a');
			var fileName = this.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
		}
		if (this.status == 401) {
			window.location.replace(serverURL + 'login.html');
		}
	};
	xhr.send();
	// $.ajax({
	// type : 'GET',
	// contentType : 'application/json',
	// url : presupuestoURL + 'pdf/' + data[0],
	// dataType : 'native',
	// // data : formToJSON(),
	// xhrFields : {
	// responseType : 'native'
	// },
	// success : function(rdata, textStatus, jqXHR) {
	// var blob = new Blob([ rdata ]);
	// var link = document.createElement('a');
	// var fileName = jqXHR.getResponseHeader('Content-Disposition');
	// fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
	// fileName.length).trim();
	// link.href = window.URL.createObjectURL(blob);
	// link.download = fileName;
	// link.click();
	// },
	// error : function(jqXHR, textStatus, errorThrown) {
	// if (errorThrown == 'Unauthorized') {
	// window.location.replace(serverURL + 'login.html');
	// } else {
	// console.log(errorThrown);
	// }
	// },
	// beforeSend : function(xhr, settings) {
	// xhr.setRequestHeader('Authorization', 'Bearer '
	// + $.cookie('restTokenC'));
	// }
	// });
}

function setFinalizado(row) {
	// console.log('setFinalizado');

	var dData = diagsTable.row(row).data();
	var actionFTJSON = 'updateEstadoDiagnostico';

	if (currentPaciente.saldo > 0) {
		actionFTJSON = 'updateSaldoParcialDiagnostico';
	}

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'update',
		data : formToJSON(actionFTJSON, dData),
		success : function(rdata, textStatus, jqXHR) {
			showDiagnosticoUpdateSuccessMessage();
			updateSaldoWell(dData);
			$.when(
					$.ajax({
						type : 'GET',
						url : jqXHR.getResponseHeader('Location'),
						success : function(data) {
							activeDiagnostico = data;
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
						diagsTable.row(row).data(
								renderDiagTableRow(activeDiagnostico)).draw();
						renderSaldo();
						renderPagosPendientes();
					})

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

function setPagado(row) {
	// console.log('setPagado');

	var dData = diagsTable.row(row).data();
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'update',
		data : formToJSON('updateDiagnostico', dData),
		success : function(rdata, textStatus, jqXHR) {
			showDiagnosticoUpdateSuccessMessage();
			updateSaldoWell(dData);
			$.when(
					$.ajax({
						type : 'GET',
						url : jqXHR.getResponseHeader('Location'),
						success : function(data) {
							activeDiagnostico = data;
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
						diagsTable.row(row).data(
								renderDiagTableRow(activeDiagnostico)).draw();
						renderSaldo();
						renderPagosPendientes();
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

function updateSaldoWell(data) {
	// console.log('updateSaldoWell');

	if (currentPaciente.saldo > 0) {
		if (currentPaciente.saldo <= (data[1] - data[2])) {
			currentPaciente.saldo = 0;
		} else {
			currentPaciente.saldo = currentPaciente.saldo - (data[1] - data[2]);
		}
	}

	renderSaldo();
}

function renderSaldo() {
	// console.log('renderSaldo');
	if (currentPaciente.saldo > 0) {
		$("#hSaldo").empty();
		$("#saldoPanel").addClass("in");
		$("#hSaldo").text("Saldo : +" + currentPaciente.saldo + " €");
		$("#cantidadSaldo").val(0);
	} else {
		$("#hSaldo").empty();
		$("#saldoPanel").removeClass("in");
	}
}

function getDiagnosticosByPaciente(paciente) {
	// console.log('getDiagnosticosByPaciente');

	$.ajax({
		type : 'GET',
		url : diagnosticoURL + 'paciente/' + paciente,
		success : function(data) {
			renderTableDiagnosticos(data);
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

var getUrlParameter = function getUrlParameter(sParam) {
	// console.log('getUrlParameter');

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
	// console.log('updatePaciente');

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : pacienteURL + 'update',
		data : formToJSON('modificar'),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
			findPacienteByUrl(jqXHR.getResponseHeader('Location'));
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

function formToJSON(action, data) {
	// console.log('formToJSON() -> ' + action + ' data {' + data + '}');

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
		var pagado;
		if (currentPaciente.saldo > 0
				&& currentPaciente.saldo < (data[1] - data[2])) {
			pagado = data[2] + currentPaciente.saldo;
		} else {
			pagado = data[1];
		}
		return JSON.stringify({
			"id" : data[0],
			"pagado" : pagado,
			"precio" : data[1]
		});
	} else if (action == 'updateEstadoDiagnostico') {
		return JSON.stringify({
			"id" : data[0],
			"fechaFin" : new Date(),
			"pagado" : data[2],
			"precio" : data[1]
		});
	} else if (action == 'updateSaldoParcialDiagnostico') {
		var pagado;
		if (currentPaciente.saldo > 0
				&& currentPaciente.saldo < (data[1] - data[2])) {
			pagado = data[2] + currentPaciente.saldo;
		} else {
			pagado = data[1];
		}
		return JSON.stringify({
			"id" : data[0],
			"fechaFin" : new Date(),
			"pagado" : pagado,
			"precio" : data[1]
		});
	} else {
		var newSaldo;
		if ($("#cantidadSaldo").val() == 0) {
			newSaldo = currentPaciente.saldo;
		} else {
			newSaldo = parseFloat($("#cantidadSaldo").val())
					+ parseFloat(currentPaciente.saldo);
		}
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
			"enfermoGrave" : $('#enfermoGrave').prop('checked'),
			"saldo" : newSaldo
		});
	}
}

function showSuccessMessage() {
	// console.log('showSuccessMessage');
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#success-alert").hide();
		});
	}, 0);
}

function showDiagnosticoSuccessMessage() {
	// console.log('showDiagnosticoSuccessMessage');
	$("#diagnostico-success-alert").alert();
	window.setTimeout(function() {
		$("#diagnostico-success-alert").fadeTo(2000, 500).slideUp(500,
				function() {
					$("#diagnostico-success-alert").hide();
				});
	}, 0);
}

function showDiagnosticoUpdateSuccessMessage() {
	// console.log('showDiagnosticoUpdateSuccessMessage');
	$("#diagnostico-update-success-alert").alert();
	window.setTimeout(function() {
		$("#diagnostico-update-success-alert").fadeTo(2000, 500).slideUp(500,
				function() {
					$("#diagnostico-update-success-alert").hide();
				});
	}, 0);
}

function showErrorMessage(error) {
	// console.log('showErrorMessage');
	$("#error-alert").alert();
	window.setTimeout(function() {
		$("#error-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#error-alert").hide();
		});
	}, 0);
}

function showAlergicoMessage() {
	// console.log('showAlergicoMessage');
	if (currentPaciente.alergico == true) {
		$("#alergia-alert").show()
	}
}

function showEnfermoGraveMessage() {
	// console.log('showEnfermoGraveMessage');
	if (currentPaciente.enfermoGrave == true) {
		$("#enfermoGrave-alert").show()
	}
}

function findPacienteByUrl(url) {
	// console.log('findPacienteByUrl');
	$.ajax({
		type : 'GET',
		url : url,
		// dataType : "json",
		success : function(data) {
			currentPaciente = data;
			renderDetails(currentPaciente);
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
