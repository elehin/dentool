// ################### document.ready() ##################################
$(document)
		.ready(
				function() {

					checkCurrentDate();
					initBarraNavegacion();

					getCitas();
					getMiniCalendario();

					$("#mesAnteriorButton")
							.on(
									'click',
									function() {
										if (fechaMiniCalendarioNav === undefined) {
											return false;
										}
										fechaMiniCalendarioNav
												.setMonth(fechaMiniCalendarioNav
														.getMonth() - 1);
										if (fechaMiniCalendarioNav.getMonth() == currentDate
												.getMonth()) {
											fechaMiniCalendarioNav
													.setMonth(fechaMiniCalendarioNav
															.getMonth() - 1);
										}
										getMiniCalendario('navegacion');
										return false;
									})
					$("#mesSiguienteButton")
							.on(
									'click',
									function() {
										if (fechaMiniCalendarioNav === undefined) {
											return false;
										}
										fechaMiniCalendarioNav
												.setMonth(fechaMiniCalendarioNav
														.getMonth() + 1);
										if (fechaMiniCalendarioNav.getMonth() == currentDate
												.getMonth()) {
											fechaMiniCalendarioNav
													.setMonth(fechaMiniCalendarioNav
															.getMonth() + 1);
										}
										getMiniCalendario('navegacion');
										return false;
									});

					initMobileObjects();

					$("#vistaDetalleButton").click(vistaDetalleClicked);

				});

var huecoClicado;
var currentDate, displayDate;
var ultimaHora = 20;
var currentCitas;
var fechaMiniCalendarioNav;
var agendaMobileTable;
var currentDia;
var duracionSlot;
var creatingCita = false;

// ###################### Funciones #######################################

function checkCurrentDate() {
	displayDate = new Date();
	currentDate = new TzDate();

	if (getUrlParameter("fecha") !== undefined
			&& getUrlParameter("fecha") != '') {
		var fechaParameter = getUrlParameter("fecha");
		var dia = fechaParameter.substr(0, fechaParameter.indexOf('-'));
		var mes = fechaParameter.substr(fechaParameter.indexOf('-') + 1,
				fechaParameter.lastIndexOf('-') - fechaParameter.indexOf('-')
						- 1);
		var year = fechaParameter.substr(fechaParameter.lastIndexOf('-') + 1,
				fechaParameter.length);

		displayDate.setDate(1); //para evitar que los días 31 cambien el mes
		displayDate.setYear(year);
		displayDate.setMonth(mes - 1);
		displayDate.setDate(dia);

		currentDate.setFecha(fechaParameter);
	}
	$('#hoyLink, #hoyLinkMobile').attr('href',
			'agenda.html?fecha=' + formatDate(new Date(), 'short'));
	$("#fechaMobile").val(
			displayDate.getFullYear() + '-'
					+ paddingLeft(displayDate.getMonth() + 1, 2) + '-'
					+ paddingLeft(displayDate.getDate(), 2));
}

// function checkCurrentDate() {
// if (getUrlParameter("fecha") !== undefined
// && getUrlParameter("fecha") != '') {
// var fechaParameter = getUrlParameter("fecha");
// var dia = fechaParameter.substr(0, fechaParameter.indexOf('-'));
// var mes = fechaParameter.substr(fechaParameter.indexOf('-') + 1,
// fechaParameter.lastIndexOf('-') - fechaParameter.indexOf('-')
// - 1);
// var year = fechaParameter.substr(fechaParameter.lastIndexOf('-') + 1,
// fechaParameter.length);
//
// currentDate = new Date();
// currentDate.setYear(year);
// currentDate.setMonth(mes - 1);
// currentDate.setDate(dia);
// } else {
// currentDate = new Date();
// }
// $('#hoyLink, #hoyLinkMobile').attr('href',
// 'agenda.html?fecha=' + formatDate(new Date(), 'short'));
// $("#fechaMobile").val(
// currentDate.getFullYear() + '-'
// + paddingLeft(currentDate.getMonth() + 1, 2) + '-'
// + paddingLeft(currentDate.getDate(), 2));
// }

function initBarraNavegacion() {
	$('.fecha').text(formatDate(displayDate));

	var siguiente = new Date(displayDate);
	siguiente.setTime(displayDate.getTime() + 86400000);

	$('#linkSiguiente').attr('href',
			'agenda.html?fecha=' + formatDate(siguiente, 'short'));

	var anterior = new Date(displayDate);
	anterior.setTime(displayDate.getTime() - 86400000);

	$('#linkAnterior').attr('href',
			'agenda.html?fecha=' + formatDate(anterior, 'short'));
}

function getCitas() {
	$.ajax({
		type : 'GET',
		url : citaURL + 'fecha/' + currentDate,
		success : function(data) {
			currentCitas = data;
			renderAgenda(data);
			populateMobileTable(data);
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

function renderAgenda(citas) {
	getDia(citas);
}

function drawTable(citas) {
	if (citas !== undefined) {
		citas.sort(compareDuracion);
		citas.reverse();

		$.each(citas, function(i, item) {
			if (item !== undefined) {
				var dateInicio = new Date(item.inicio);
				var cell;
				var minutos = dateInicio.getMinutes();
				var columnCell;
				var isNameWritten = false;

				var horaCell = dateInicio.getHours();

				duracionSlot = 30;
				var huecosOcupados;
				var slotAgenda;

				if (currentDia !== undefined && currentDia.agendaDetalle) {
					duracionSlot = 10;
				}
				huecosOcupados = Math.ceil((item.fin - item.inicio) / 60000
						/ duracionSlot);

				// Se define si en la columna hay suficientes
				// huecos
				// libres
				columnCell = '1';
				var horaCellAux = horaCell;
				var minutosAux = minutos;
				for (i = 0; i < huecosOcupados; i++) {
					if (!$(
							"#" + horaCellAux + "\\:"
									+ paddingLeft(minutosAux, 2) + "\\:" + "1")
							.hasClass('libre')) {
						columnCell = '2';
					}

					if (currentDia !== undefined && currentDia.agendaDetalle) {
						if (minutosAux == 50) {
							horaCellAux++;
							minutosAux = -10
						}
						minutosAux = minutosAux + 10;
					} else {
						if (minutosAux == 30) {
							horaCellAux++;
							minutosAux = -30
						}
						minutosAux = minutosAux + 30;
					}
				}

				slotAgenda = $("#" + horaCell + "\\:" + paddingLeft(minutos, 2)
						+ "\\:" + columnCell);

				slotAgenda.attr('rowspan', huecosOcupados);
				slotAgenda.addClass('ocupado');
				slotAgenda.removeClass('libre');
				slotAgenda.text(item.nombre);

				var tooltipDescripcion = '';
				var tooltipTelefono = '';
				if (item.descripcion !== undefined && item.descripcion != null
						&& item.descripcion != '') {
					tooltipDescripcion = item.descripcion;
					if (item.telefono !== undefined && item.telefono != null
							&& item.telefono != '') {
						tooltipDescripcion += ' - ';
					}
				}
				if (item.telefono !== undefined && item.telefono != null
						&& item.telefono != '') {
					tooltipTelefono += 'Telf.: '
							+ formatTelefono(item.telefono);
				}
				slotAgenda.attr('title', tooltipDescripcion + tooltipTelefono);
				slotAgenda.attr('cita', item.id);

				for (i = 0; i < huecosOcupados; i++) {
					if (currentDia !== undefined && currentDia.agendaDetalle) {

						if (i == 0 && minutos < 50) {
							minutos = minutos + 10;
							continue;
						} else if (i == 0 && minutos == 50) {
							minutos = 0
							horaCell++
							continue;
						}

						$(
								"#" + horaCell + "\\:"
										+ paddingLeft(minutos, 2) + "\\:"
										+ columnCell).remove();

						if (minutos == 50) {
							horaCell++;
							minutos = -10
						}
						minutos = minutos + 10;
					} else {
						if (i == 0 && minutos < 30) {
							minutos = minutos + 30;
							continue;
						} else if (i == 0 && minutos == 30) {
							minutos = 0
							horaCell++
							continue;
						}

						$(
								"#" + horaCell + "\\:"
										+ paddingLeft(minutos, 2) + "\\:"
										+ columnCell).remove();

						if (minutos == 30) {
							horaCell++;
							minutos = -30
						}
						minutos = minutos + 30;
					}

				}
			}
		});
	}

	initDragAndDrop();

}

function clearAgenda() {
	$('.ocupado').text('');
	$('.ocupado').addClass('libre');
	$('.ocupado').removeClass();
}

function initDiagNuevaCita() {
	// Inicialización del diálogo para nuevas citas
	$("#dialog").dialog({
		autoOpen : false,
		modal : false,
		draggable : false,
		close : function() {
			$("#nuevaCitaForm")[0].reset();
		},
		open : function(event) {
			$("#name").focus();
		}
	});

	$('#createCitaButton').on('click', function() {
		if (!creatingCita) {
			creatingCita = true;
			createCita();
		}
		return false;
	})

	$("#nuevaCitaForm").on('keydown', 'input', function(event) {
		var key = event.which;
		if (key == 13) {
			if (!creatingCita) {
				creatingCita = true;
				createCita();
			}
		}
	});

	// #########################################################################
	// #########################################################################
	// Inicialización del diágolo de edición de citas
	$("#editDialog").dialog({
		autoOpen : false,
		modal : false,
		draggable : false,
		close : function(event, ui) {
			initDragAndDrop();
			$("#nuevaCitaForm")[0].reset();
		}
	});

	$('#updateCitaButton').on('click', function() {
		updateCita();
	})

	$("#duracionEdit").change(function() {
		$('#duracionOutputEdit').val($("#duracionEdit").val());
	});

	// Se ajusta el intervalo de duración de la cita en
	// función del tipo de calendario
	// Se ajusta el valor por defecto en base al tipo de calendario
	if (currentDia !== undefined && currentDia.agendaDetalle) {
		$("#duracion").attr("step", 10);
		$("#duracion").attr("min", 10);
		$("#duracion").attr("value", 10);

		$("#duracionOutput").text(10);

		$("#duracionEdit").attr("step", 10);
		$("#duracionEdit").attr("min", 10);
		$("#duracionEdit").attr("value", 10);

		$("#duracionOutputEdit").text(10);

	} else {
		$("#duracion").attr("step", 15);
		$("#duracion").attr("min", 15);
		$("#duracion").attr("value", 30);

		$("#duracionOutput").text(30);

		$("#duracionEdit").attr("step", 15);
		$("#duracionEdit").attr("min", 15);
		$("#duracionEdit").attr("value", 30);

		$("#duracionOutputEdit").text(30);
	}

	$("#editCitaForm").on('keydown', 'input', function(event) {
		var key = event.which;
		if (key == 13) {
			updateCita();
		}
	});

	// #########################################################################
	// Asignacion del evento para mostrar el diálogo de nueva cita

	$('#cancelaCitaButton').on('click', function(ev) {
		$("#anularDialog").dialog("open");
	})

	$('.libre').off('click');
	// $("td[class*='libre']")
	// .click(
	$("#calendar")
			.on(
					'click',
					'.libre',

					function(event) {

						huecoClicado = $(this);

						// Se cierra el diálogo de editar cita si está abierto
						$("#editDialog").dialog("close");
						$("#dialog").dialog("close");

						var horaInicio = calculaInicio(event);

						$("#dialog")
								.dialog(
										"option",
										"title",
										"Nueva cita - "
												+ horaInicio
														.toString()
														.substr(
																0,
																horaInicio
																		.toString()
																		.lastIndexOf(
																				':',
																				horaInicio
																						.toString().length)));

						$("#inicio").val(horaInicio);
						$("#dialog").dialog("option", "position", {
							my : "top",
							at : "top",
							of : $(event.target)
						});
						$("#dialog").dialog("option", "width",
								$(event.target).width());

						var huecosLibres = calculaHuecosLibres(huecoClicado,
								horaInicio);

						$("#duracion").attr('max', huecosLibres * duracionSlot);

						$("#dialog").dialog("open");
					});

	$("#duracion").change(function() {
		$('#duracionOutput').val($("#duracion").val());
	});

	// #########################################################################
	// Asignacion del evento para mostrar el diálogo de edición de cita
	$('.ocupado').off('dblclick');
	$("#calendar")
			.on(
					// 'dblclick',
					'click',
					'.ocupado',

					function(event) {

						$('.ocupado').draggable('option', 'containment',
								event.target);
						$('.hueco').droppable('option', 'accept', function() {
							return false;
						})

						huecoClicado = $(this);
						$('#citaId').val(huecoClicado.attr('cita'));

						// Se cierra el diálogo de editar cita si está abierto
						$("#editDialog").dialog("close");
						$("#dialog").dialog("close");

						var horaInicio = calculaInicio(event);

						// $("#inicio").val(horaInicio);
						$("#editDialog").dialog("option", "position", {
							my : "top",
							at : "top",
							of : $(event.target)
						});
						$("#editDialog").dialog("option", "width",
								$(event.target).width());

						var cita = getCurrentCita($('#citaId').val());

						$('#nameEdit').val(cita.nombre);
						$('#descripcionEdit').val(cita.descripcion);
						$('#telefonoEdit').val(cita.telefono);
						var duracion = Math
								.ceil((cita.fin - cita.inicio) / 60000);
						$("#duracionEdit").val(duracion);
						$('#duracionOutputEdit').val(duracion);
						var fecha = new Date(cita.inicio);
						$('#fechaDatePicker')
								.val(
										fecha.getFullYear()
												+ '-'
												+ paddingLeft(
														(fecha.getMonth() + 1),
														2)
												+ '-'
												+ paddingLeft(fecha.getDate(),
														2));

						var huecosLibres = calculaHuecosLibres(huecoClicado,
								horaInicio);

						// TODO ajustar tamaño de la cita
						if (huecosLibres * 30 < duracion) {
							huecosLibres = duracion / 30;
						}
						$("#duracionEdit").attr('max', huecosLibres * 30);

						$("#editDialog").dialog("open");
					});

	$("#duracionEdit").change(function() {
		$('#duracionOutputEdit').val($("#duracionEdit").val());
	});
}

// #########################################################################
// #########################################################################
// Inicialización del diálogo de anulación de cita
$("#anularDialog").dialog({
	autoOpen : false,
	resizable : false,
	height : 170,
	modal : true,
	buttons : {
		"Confirmar" : function(ev) {
			$(this).dialog("close");
			deleteCita(ev);
		},
		Cancel : function() {
			$(this).dialog("close");
		}
	}
});

function createCita(origen) {
	if (origen === undefined) {
		origen = 'create';
	}
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : citaURL,
		data : formToJSON(origen),
		success : function(rdata, textStatus, jqXHR) {

			if (origen === 'crearMobile') {
				findCitaByUrl(jqXHR.getResponseHeader('Location'));
				$("#addCitaMobileDiv").toggleClass("in");
				$("#addCitaMobileForm")[0].reset();
			} else {
				getCitas();
				$("#dialog").dialog("close");
			}

			creatingCita = false;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (errorThrown == 'Unauthorized') {
				window.location.replace(serverURL + 'login.html');
			} else {
				showErrorMessage(textStatus);
			}

			creatingCita = false;
		},
		beforeSend : function(xhr, settings) {
			xhr.setRequestHeader('Authorization', 'Bearer '
					+ $.cookie('restTokenC'));
		}
	});
}

function findCitaByUrl(url) {
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			insertaCitaMobile(data);
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

function updateCita(event) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : citaURL,
		data : formToJSON('modificar', event),
		success : function(rdata, textStatus, jqXHR) {
			$("#editDialog").dialog("close");
			getCitas();
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

// #########################################################################
// #########################################################################
// Inicialización del calendario para cambiar la fecha de la cita
function initDatePicker() {
	$("#fechaDatePicker").datepicker(
			{
				dateFormat : 'yy-m-d',
				onSelect : function(dateText, inst) {
					$("#fechaEdit").val(dateText);
					var d = new Date(dateText);
					var fechaFormateada = d.getDate() + getMes(d.getMonth())
							+ d.getFullYear();
					$("#fechaDatePicker").val(fechaFormateada);
				}
			});

	$.datepicker.regional['es'] = {
		closeText : 'Cerrar',
		prevText : '<Ant',
		nextText : 'Sig>',
		currentText : 'Hoy',
		monthNames : [ 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
				'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre',
				'Diciembre' ],
		monthNamesShort : [ 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul',
				'Ago', 'Sep', 'Oct', 'Nov', 'Dic' ],
		dayNames : [ 'Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves',
				'Viernes', 'Sábado' ],
		dayNamesShort : [ 'Dom', 'Lun', 'Mar', 'Mi&eacute;', 'Juv', 'Vie',
				'Sáb' ],
		dayNamesMin : [ 'Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'S&aacute;' ],
		weekHeader : 'Sm',
		dateFormat : 'dd/mm/yy',
		firstDay : 1,
		isRTL : false,
		showMonthAfterYear : false,
		yearSuffix : ''
	};

	$.datepicker.setDefaults($.datepicker.regional['es']);
}

function deleteCita(event) {
	var id = huecoClicado.attr('cita');
	$.ajax({
		type : 'DELETE',
		contentType : 'application/json',
		url : citaURL + id,
		success : function(rdata, textStatus, jqXHR) {
			removeCitaFromCitas(id);
			$("#editDialog").dialog("close");
			renderAgenda(currentCitas);
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

function initDragAndDrop() {
	// #########################################################################
	// #########################################################################
	// Inicialización de los objetos draggable para cambiar las horas de citas
	$(".ocupado").draggable({
		scope : "citas",
		appendTo : $("#calendar tbody"),
		containment : $("#calendar tbody"),
		delay : 500,
		revert : "invalid",
		grid : [ $(".hueco").width() + 5, 20.5 ],
		stack : ".ocupado",
		start : draggableStart,
		scroll : true,
		snap : true,
		opacity : 0.5
	});

	// #########################################################################
	// #########################################################################
	// Inicialización de los objetos droppable para aceptar los cambios de hora
	$(".hueco").droppable({
		scope : "citas",
		accept : acceptDropEvent,
		tolerance : "pointer",
		drop : dropComplete,
		// activeClass : "drop-active",
		classes : {
			"ui-droppable-hover" : "drop-hover"
		}
	// hoverClass : "drop-hover"
	});
}

function acceptDropEvent(event) {
	if (huecoClicado !== undefined) {
		var c = getCurrentCita(huecoClicado.attr('cita'));
		if (c != undefined) {
			if (currentDia !== undefined && currentDia.agendaDetalle) {
				duracionSlot = 10;
			} else {
				duracionSlot = 30;
			}

			var huecosOcupados = Math.ceil((c.fin - c.inicio) / 60000
					/ duracionSlot);
			var huecosLibres = calculaHuecosLibres($(this),
					calculaInicio($(this)));
			var hayHueco = huecosLibres >= huecosOcupados;
			var estaLibre = $(this).hasClass("libre");
			var accept = estaLibre && hayHueco;

			// if (!accept) {
			// console.log($(this).prop('id'));
			// console.log('hayHueco: ' + hayHueco);
			// console.log('estaLibre: ' + estaLibre);
			// }
			return accept;
		} else {
			return false;
		}
	} else
		return false
}

function draggableStart(event, ui) {
	huecoClicado = $(event.target);
}

function dropComplete(draggable, ui) {
	var c = getCurrentCita(huecoClicado.attr('cita'));
	$('#nameEdit').val(c.nombre);
	$('#citaId').val(c.id);
	$('#telefonoEdit').val(c.telefono);
	$("#duracionEdit").val((c.fin - c.inicio) / 60000);
	// $("#fechaDatePicker").val(currentDate);
	updateCita(draggable);
}

function getMiniCalendario(target) {
	if (target === undefined || target == 'ambos' || target == 'principal') {
		$.ajax({
			type : 'GET',
			url : citaURL + 'minicalendario/' + displayDate,
			success : function(data) {
				renderMiniCalendario(data, 'miniCalendarTable');
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

	if (target === undefined || target == 'ambos' || target == 'navegacion') {
		var mesSiguiente;
		if (fechaMiniCalendarioNav === undefined) {
			mesSiguiente = new Date(displayDate);
			mesSiguiente.setDate(1);
			mesSiguiente.setMonth(displayDate.getMonth() + 1);
			fechaMiniCalendarioNav = new Date(mesSiguiente);
		} else {
			mesSiguiente = new Date(fechaMiniCalendarioNav);
			mesSiguiente.setDate(1);
		}

		$.ajax({
			type : 'GET',
			url : citaURL + 'minicalendario/' + mesSiguiente,
			success : function(data) {
				renderMiniCalendario(data, 'miniCalendarTableSiguiente');
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
}

function renderMiniCalendario(miniCalendario, object) {
	// console.log(miniCalendario);
	var row;
	var j = 1; // representa el día del mes
	var fecha = new Date(miniCalendario.fecha);

	// $('#' + object).empty();
	$('#' + object + ' > tbody').empty();

	row = '<tr>' + '<th>l</th>' + '<th>m</th>' + '<th>x</th>' + '<th>j</th>'
			+ '<th>v</th>' + '<th>s</th>' + '<th>d</th>' + '</tr>';
	$('#' + object + ' > tbody:last-child').append(row);

	row = '<tr>';
	if (miniCalendario.primerDiaMes == 1) {
		miniCalendario.primerDiaMes += 7;
	}
	for (var i = 2; i < miniCalendario.primerDiaMes; i++) {
		row += '<td></td>';
	}

	for (i; i < 9; i++) {
		if (miniCalendario.diasCita.indexOf(j) != -1) {
			row += '<td>' + '<a class="conCitas" href="agenda.html?fecha=' + j
					+ '-' + (fecha.getMonth() + 1) + '-' + fecha.getFullYear()
					+ '">' + j + '</a>' + '</td>';
		} else {
			row += '<td>' + '<a class="sinCitas" href="agenda.html?fecha=' + j
					+ '-' + (fecha.getMonth() + 1) + '-' + fecha.getFullYear()
					+ '">' + j + '</a>' + '</td>';
		}
		j++;
	}
	row += '</tr>';
	$('#' + object + ' > tbody:last-child').append(row);

	row = '<tr>';
	i = 0;
	for (j; j <= miniCalendario.ultimoDiaMes; j++) {
		if (i == 7) {
			i = 1;
			row += '</tr>';
			$('#' + object + ' > tbody:last-child').append(row);
			row = '<tr>';
		} else {
			i++;
		}

		if (miniCalendario.diasCita.indexOf(j) != -1) {
			row += '<td>' + '<a class="conCitas" href="agenda.html?fecha=' + j
					+ '-' + (fecha.getMonth() + 1) + '-' + fecha.getFullYear()
					+ '">' + j + '</a>' + '</td>';
		} else {
			row += '<td>' + '<a class="sinCitas" href="agenda.html?fecha=' + j
					+ '-' + (fecha.getMonth() + 1) + '-' + fecha.getFullYear()
					+ '">' + j + '</a>' + '</td>';
		}
	}

	row += '</tr>';
	$('#' + object + ' > tbody:last-child').append(row);

	// row = '<caption class="miniCalendar-caption">' + meses[fecha.getMonth()]
	// + '</caption>';
	$('#' + object + 'Caption').text(
			meses[fecha.getMonth()] + ' ' + fecha.getFullYear());

}

function formToJSON(action, event) {
	if (action == 'create') {

		var hora = calculaInicio();
		var inicio = new Date(displayDate);

		inicio
				.setHours(hora.toString().substr(0,
						hora.toString().indexOf(':')));
		inicio.setMinutes(hora.substr(hora.indexOf(':') + 1, 2), 0, 0);

		var fin = new Date(inicio);

		fin.setTime(inicio.getTime() + $("#duracion").val() * 60000);

		return JSON.stringify({
			"nombre" : $('#name').val(),
			// "pacienteId" : $('#apellidos').val(),
			"descripcion" : $("#descripcion").val(),
			"telefono" : $('#telefono').val(),
			"inicio" : inicio,
			"fin" : fin
		});
	} else if (action == 'crearMobile') {

		var horaInicio = $("#inicioMobile").val().toString();
		var hora = horaInicio.substr(0, horaInicio.indexOf(':'));
		var minutos = horaInicio.substr(horaInicio.indexOf(':') + 1,
				horaInicio.length);
		var inicio = new Date(displayDate.getFullYear(),
				displayDate.getMonth(), displayDate.getDate(), hora, minutos,
				0, 0);

		var horafin = $("#finMobile").val().toString();
		hora = horafin.substr(0, horafin.indexOf(':'));
		minutos = horafin.substr(horafin.indexOf(':') + 1, horafin.length);
		var fin = new Date(displayDate.getFullYear(), displayDate.getMonth(),
				displayDate.getDate(), hora, minutos, 0, 0);

		return JSON.stringify({
			"nombre" : $('#pacienteMobile').val(),
			// "pacienteId" : $('#apellidos').val(),
			"descripcion" : $("#descripcionMobile").val(),
			"telefono" : $('#telefonoMobile').val(),
			"inicio" : inicio,
			"fin" : fin
		});
	} else if (action == 'modificar') {

		var hora = calculaInicio(event);
		if (event !== undefined && event.type == 'drop') {
			var inicio = new Date(displayDate);
		} else {
			var inicio = new Date($('#fechaDatePicker').val());
		}

		inicio
				.setHours(hora.toString().substr(0,
						hora.toString().indexOf(':')));
		inicio.setMinutes(hora.substr(hora.indexOf(':') + 1, 2), 0, 0);

		var fin = new Date(inicio);

		if ($(event).attr('type') == 'drop') {
			var cita = getCurrentCita(huecoClicado.attr('cita'));
			fin.setTime(inicio.getTime() + cita.fin - cita.inicio);
		} else {
			fin.setTime(inicio.getTime() + $("#duracionEdit").val() * 60000);
		}

		return JSON.stringify({
			"nombre" : $('#nameEdit').val(),
			"id" : $('#citaId').val(),
			"descripcion" : $("#descripcionEdit").val(),
			"telefono" : $('#telefonoEdit').val(),
			"inicio" : inicio,
			"fin" : fin
		});
	} else if (action == 'vistaDetalle') {
		if (currentDia != undefined) {
			return JSON.stringify({
				"id" : currentDia.id,
				"agendaDetalle" : !currentDia.agendaDetalle,
				"fecha" : currentDate
			});
		} else {
			return JSON.stringify({
				"agendaDetalle" : true,
				"fecha" : currentDate
			});
		}
	}
}

function showSuccessMessage(id) {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
			$("#success-alert").hide();
			url = serverURL + 'paciente.html?paciente=' + id;
			window.location.replace(url);
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

function calculaInicio(event) {
	var currentInicio;

	if (event !== undefined) {
		if (event.target !== undefined) {
			currentInicio = $(event.target).prop("id").split(":");
		} else {
			currentInicio = $(event).prop("id").split(":");
		}
	} else {
		currentInicio = huecoClicado.prop("id").split(":");
	}

	var minutos = currentInicio[1];

	return currentInicio[0] + ':' + paddingLeft(minutos, 2) + ':'
			+ currentInicio[2];
}

function toggleMediasCell(mediasCell) {
	if (mediasCell == 'T') {
		mediasCell = 'B';
		return mediasCell;
	} else if (mediasCell == 'B') {
		mediasCell = 'T';
		return mediasCell;
	}
}

function compareDuracion(citaA, citaB) {
	var duracionA = citaA.fin - citaA.inicio;
	var duracionB = citaB.fin - citaB.inicio;
	return duracionA - duracionB;
}

// function drawTable() {
// getDia();
// var error = false;

//
// $.when(
// $.ajax({
// type : 'GET',
// url : diaURL + 'fecha/' + currentDate,
// success : function(data) {
// currentDia = data;
// return false;
// },
// error : function(jqXHR, textStatus, errorThrown) {
// if (errorThrown == 'Unauthorized') {
// window.location.replace(serverURL + 'login.html');
// }
// // error = true;
// return false;
// },
// beforeSend : function(xhr, settings) {
// xhr.setRequestHeader('Authorization', 'Bearer '
// + $.cookie('restTokenC'));
// }
// })).then(drawCalendarCells);
// if (error) {
// drawCalendarCells();
// }
// }

function drawCalendarCells(citas) {

	toggleVistaDetalleButton();

	var primera = 9;
	var ultima = 20;
	var row;
	var rowspan = 2;

	$('#calendar > tbody').empty();

	if (currentDia != undefined && currentDia.agendaDetalle) {
		rowspan = 6;
	}

	for (var actual = primera; actual <= ultima; actual++) {
		row = '<tr><td class="hora text-info" rowspan="' + rowspan + '">'
				+ actual + ':00</td>';
		row += '<td id="' + actual
				+ ':00:1" class="hueco hueco-top hueco-izda libre"></td>';
		row += '<td id="' + actual
				+ ':00:2" class="hueco hueco-top libre"></td></tr>';

		$('#calendar > tbody:last-child').append(row);

		for (var i = 1; i < rowspan; i++) {
			if (currentDia !== undefined && currentDia.agendaDetalle) {
				minutos = i * 10;
			} else {
				minutos = i * 30;
			}

			row = '<tr><td id="' + actual + ':' + minutos
					+ ':1" class="hueco hueco-izda libre"></td>';
			row += '<td id="' + actual + ':' + minutos
					+ ':2" class="hueco libre"></td></tr>';

			$('#calendar > tbody:last-child').append(row);
		}

	}

	drawTable(citas);
}

function getCurrentCita(citaId) {
	var cita;
	$.each(currentCitas, function(i, item) {
		if (item !== undefined && item.id == citaId) {
			cita = item;
		}
	});
	return cita;
}

function removeCitaFromCitas(citaId) {
	$.each(currentCitas, function(i, item) {
		if (item !== undefined && item.id == citaId) {
			delete currentCitas[i];
		}
	});
	return currentCitas;
}

function calculaHuecosLibres(huecoClicado, horaInicio) {
	var huecosLibres = 1;
	var estaLibre = true;
	var splittedId, minutos, horaCell;

	if (horaInicio === undefined) {
		splittedId = huecoClicado.prop("id").split(":");
		horaCell = splittedId[0];
		// substr(
		// 0,
		// huecoClicado.prop("id").length
		// - huecoClicado.prop("id").indexOf(mediasCell));
	} else {
		splittedId = horaInicio.split(":");
		horaCell = splittedId[0];
	}

	minutos = parseInt(splittedId[1]);

	while (estaLibre) {
		if (currentDia !== undefined && currentDia.agendaDetalle) {
			if (minutos == 50) {
				horaCell++;
				minutos = -10;
			}
			minutos = minutos + 10;
		} else {
			if (minutos == 30) {
				horaCell++;
				minutos = -30;
			}
			minutos = minutos + 30;
		}

		if ((!$("#" + horaCell + "\\:" + paddingLeft(minutos, 2) + "\\:" + "1")
				.hasClass("libre") && !$(
				"#" + horaCell + "\\:" + paddingLeft(minutos, 2) + "\\:" + "2")
				.hasClass("libre"))
				|| horaCell > ultimaHora) {
			estaLibre = false;
		} else {
			huecosLibres++;
		}
	}

	return huecosLibres;
}

// #########################################################################
// #########################################################################
// Funcionalidad para mobile

function renderAgendaMobileTableRow(item) {
	var fechaInicio = new Date(item.inicio);
	var fechaFin = new Date(item.fin);

	var inicio = formatDate(fechaInicio, 'hora');
	var fin = formatDate(fechaFin, 'hora');

	row = [ item.id, paddingLeft(inicio, 5) + ' - ' + paddingLeft(fin, 5),
			item.nombre, item.telefono, item.descripcion ];

	return row;
}

function populateMobileTable(citas) {
	var dataset = [];

	$.each(citas, function(i, item) {
		dataset.push(renderAgendaMobileTableRow(item));
	});

	agendaMobileTable = $('#agendaTableMobile')
			.DataTable(
					{
						"retrieve" : true,
						"paging" : false,
						"searching" : false,
						"info" : false,
						"ordering" : true,
						"order" : [ [ 1, "asc" ] ],
						"data" : dataset,
						"columns" : [ {
							"title" : "id"
						}, {
							"title" : "hora"
						}, {
							"title" : "nombre"
						}, {
							"title" : "telefono"
						}, {
							"title" : "descripcion"
						} ],
						"columnDefs" : [ {
							"className" : "never",
							"targets" : [ 0 ],
							"visible" : false
						} ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay tratamientos aún",
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

	agendaMobileTable.draw();

	// $('#tableTratamientos tbody').on('click', 'button', function() {
	// var data = searchTable.row($(this).parents('tr')).data();
	// url = serverURL + 'tratamiento.html?tratamiento=' + data[0];
	// window.location.replace(url);
	// });
}

function insertaCitaMobile(cita) {
	agendaMobileTable.row.add(renderAgendaMobileTableRow(cita));
	agendaMobileTable.draw();
	return false;
}

function initMobileObjects() {
	$("#fechaMobile").change(
			function() {
				var target = new Date($("#fechaMobile").val());

				window.location.replace(serverURL + 'agenda.html?fecha='
						+ formatDate(target, 'short'));
				return false;
			});

	$('.clockpicker').clockpicker({
		'default' : 'now',
		autoclose : true,
		placement : 'bottom',
		align : 'left',
	});

	$('#btnAddCitaMobile').on('click', function() {
		createCita('crearMobile');
		return false;
	});

	return false;
}

// $('#inicioMobileDiv').clockpicker(
// afterDone : function() {
// var horaInicio = $("#inicioMobile").val().toString();
// var hora = horaInicio.substr(0, horaInicio.indexOf(':'));
// var minutos = horaInicio.substr(
// horaInicio.indexOf(':') + 1, horaInicio.length);
// var currentTime = new Date(currentDate.getFullYear(),
// currentDate.getMonth(), currentDate.getDate(),
// hora, minutos, 0, 0);
// currentTime.setMinutes(currentTime.getMinutes() + 30);
// $("#finMobile").val(
// currentTime.getHours() + ':'
// + currentTime.getMinutes());
// }

// $('#finMobileDiv').clockpicker({
// 'default' : 'now',
// autoclose : true,
// placement : 'bottom',
// align : 'left'
// });

function getDia(citas) {
	$.ajax({
		type : 'GET',
		url : diaURL + 'fecha/' + currentDate,
		success : function(data) {
			currentDia = data;
			drawCalendarCells(citas);
			initDiagNuevaCita();
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (errorThrown == 'Unauthorized') {
				window.location.replace(serverURL + 'login.html');
			} else if (errorThrown == 'Not Found') {
				drawCalendarCells(citas);
				initDiagNuevaCita();
			}
			return false;
		},
		beforeSend : function(xhr, settings) {
			xhr.setRequestHeader('Authorization', 'Bearer '
					+ $.cookie('restTokenC'));
		}
	});
}

function vistaDetalleClicked() {
	if (currentDia == undefined) {

		$.ajax({
			type : 'PUT',
			contentType : 'application/json',
			url : diaURL,
			data : formToJSON('vistaDetalle'),
			success : function(rdata, textStatus, jqXHR) {
				getDia(currentCitas);
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
	} else {
		$.ajax({
			type : 'POST',
			contentType : 'application/json',
			url : diaURL,
			data : formToJSON('vistaDetalle'),
			success : function(rdata, textStatus, jqXHR) {
				getDia(currentCitas);
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
}

function toggleVistaDetalleButton() {
	if (currentDia == undefined
			|| (currentDia != undefined && !currentDia.agendaDetalle)) {
		$("#vistaDetalleButton")
				.html(
						'<span class="glyphicon glyphicon-zoom-in"></span> Vista de detalle');

	} else {
		$("#vistaDetalleButton")
				.html(
						'<span class="glyphicon glyphicon-zoom-out"></span> Vista normal');
	}
}