/**
 * 
 */

// var rootURL = 'https://dentool-elehin.rhcloud.com/service/paciente/';
var rootURL = 'http://localhost:8080/service/paciente/';

var currentPaciente;
var searchTable;
var searchDialog;

$(document)
		.ready(
				function() {

					$("#btnSave").click(function() {
						if (!$('#pacienteId').val()) {
							createPaciente();
						} else {
							updatePaciente();
						}

						return false;
					});

					$("#btnSearch").click(function() {
						key = $("#searchKey").val();
						if ($.isNumeric(key)) {
							findPaciente(key);
						} else {
							findPacienteByApellidos(key);
						}
						return false;
					});

					$("#btnAdd").click(function() {
						currentPaciente = null;
						hideMessages();
						$("#pacienteForm")[0].reset();
						$("#mainArea").show();
						$("#leftArea").show();
						$("#rightArea").show();
						return false;
					});

					// Inicialización del diálogo para nuevas citas
					searchDialog = $("#searchDialog").dialog({
						autoOpen : false,
						modal : true,
						draggable : true,
						buttons : {
							"Cerrar" : function() {
								$("#searchDialog").dialog("close");
							}
						},
						close : function() {
							$("#searchPacienteForm")[0].reset();
							searchTable.destroy();
						}
					});

					if (currentPaciente != null
							&& currentPaciente.alergico == true) {
						showMessage(
								'Este paciente sufre algún tipo de alergia significativa',
								'warning');
					} else if (currentPaciente == null) {
						$("#mainArea").hide();
						$("#leftArea").hide();
						$("#rightArea").hide();
					}

				});

function createPaciente() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		// dataType : "json",
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			showMessage('Paciente creado con éxito', 'success');
			$('#pacienteId').val(rdata.id);
			findPacienteByUrl(jqXHR.getResponseHeader('Location'));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showMessage('Error al crear el paciente: ' + textStatus, 'error');
		}
	});
}

function updatePaciente() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL + 'update',
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			showMessage('Paciente actualizado', 'success');
			findPacienteByUrl(jqXHR.getResponseHeader('Location'));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showMessage('El paciente no se ha actualizado debido a un error: '
					+ textStatus, 'error');
		}
	});
}

function findPaciente(id) {
	$
			.ajax({
				type : 'GET',
				url : rootURL + id,
				// dataType : "json",
				success : function(data) {
					currentPaciente = data;
					$("#mainArea").show();
					$("#leftArea").show();
					$("#rightArea").show();
					renderDetails(currentPaciente);
					if (currentPaciente.alergico == true) {
						showMessage(
								'Este paciente sufre algún tipo de alergia significativa',
								'warning');
					}
				}
			});
}

function findPacienteByUrl(url) {
	$
			.ajax({
				type : 'GET',
				url : url,
				// dataType : "json",
				success : function(data) {
					currentPaciente = data;
					$("#mainArea").show();
					$("#leftArea").show();
					$("#rightArea").show();
					renderDetails(currentPaciente);
					if (currentPaciente.alergico == true) {
						showMessage(
								'Este paciente sufre algún tipo de alergia significativa',
								'warning');
					}
				}
			});
}

function findPacienteByApellidos(apellidos) {
	$
			.ajax({
				type : 'GET',
				url : rootURL + "apellido/" + apellidos,
				// dataType : "json",
				success : function(data) {
					if (data.length == 1) {
						console.log(data);
						currentPaciente = data[0];
						$("#mainArea").show();
						$("#leftArea").show();
						$("#rightArea").show();
						renderDetails(currentPaciente);
						if (currentPaciente.alergico == true) {
							showMessage(
									'Este paciente sufre algún tipo de alergia significativa',
									'warning');
						}
					} else if (data.length > 1) {

						populateTable(data);

						$("#searchDialog").dialog("option", "width", 400);
						$("#searchDialog").dialog("open");
					}
				}
			});
}

function populateTable(dataset) {
	var trHTML = '';
	$('#pacientesSearchTableBody').empty();

	$.each(dataset,
			function(i, item) {
				trHTML += '<tr><td>' + item.id + '</td><td>' + item.name + ' '
						+ item.apellidos + '</td><td>' + item.lastChange
						+ '</td></tr>';
			});
	$("#pacientesSearchTableBody").append(trHTML);

	searchTable = $('#pacientesSearchTable').DataTable({
		"retrieve" : false,
		"order" : [ [ 2, "desc" ] ],
		"pagingType" : "numbers",
		"lengthChange" : false,
		"info" : false,
		"language" : {
			"search" : "Buscar:",
		}
	});

	searchTable.on('click', 'tr', function() {
		findPaciente(searchTable.row(this).data()[0]);
		searchDialog.dialog("close");
	});

	/*
	 * $('#pacientesSearchTableBody').on('click', 'tr', function() { if
	 * ($(this).hasClass('selected')) { $(this).removeClass('selected'); } else {
	 * searchTable.$('tr.selected').removeClass('selected');
	 * $(this).addClass('selected'); } });
	 */
}

function renderDetails(paciente) {
	hideMessages();
	$('#pacienteId').val(paciente.id);
	$('#name').val(paciente.name);
	$('#apellidos').val(paciente.apellidos);
	$('#direccion').val(paciente.direccion);
	$('#telefono').val(paciente.telefono);
	$('#fechaNacimiento').val(paciente.fechaNacimiento);
	$('#notas').val(paciente.notas);
	$('#alergico').prop('checked', paciente.alergico);
	$('#dni').val(paciente.dni);
}

// Helper function to serialize all the form fields into a JSON string
function formToJSON() {
	return JSON.stringify({
		"id" : $('#pacienteId').val(),
		"name" : $('#name').val(),
		"apellidos" : $('#apellidos').val(),
		"direccion" : $('#direccion').val(),
		"telefono" : $('#telefono').val(),
		"fechaNacimiento" : $('#fechaNacimiento').val(),
		"notas" : $('#notas').val(),
		"dni" : $('#dni').val(),
		"alergico" : $('#alergico').prop('checked')
	});
}

function resize_to_fit() {
	var fontsize = $('div#outer div').css('font-size');
	$('div#outer div').css('fontSize', parseFloat(fontsize) - 1);

	if ($('div#outer div').height() >= $('div#outer').height()) {
		resize_to_fit();
	}
}

function showMessage(message, style) {

	style = style || 'notice'; // <== default style if it's not set

	// create message and show it
	$('<div>').attr('class', style).html(message).fadeIn('fast').insertBefore(
			$('#messagesDiv')) // <== wherever you want it to show
	.animate({
		opacity : 1.0
	}, 4000) // <== wait 3 sec before fading out
	.fadeOut('slow', function() {
		$(this).remove();
	});
}

function hideMessages() {
	$(".warning.error.success.info.validation").hide();
}
