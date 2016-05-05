/**
 * 
 */

var rootURL = 'http://localhost:8080/service/paciente/';

var currentPaciente;

$(document)
		.ready(
				function() {

					$("#btnSave").click(function() {
						createPaciente();
						return false;
					});

					$("#btnSearch").click(function() {
						id = $("#searchKey").val();
						findPaciente(id);
						return false;
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
	console.log('createPaciente');
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		// dataType : "json",
		data : formToJSON(),
		success : function(data, textStatus, jqXHR) {
			showMessage('Paciente creado con éxito', 'success');
			$('#btnDelete').show();
			$('#wineId').val(data.id);
			currentPaciente = data;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showMessage('Error al crear el paciente: ' + textStatus, 'error');
		}
	});
}

function findPaciente(id) {
	console.log('findById: ' + id);
	$
			.ajax({
				type : 'GET',
				url : rootURL + id,
				// dataType : "json",
				success : function(data) {
					$('#btnDelete').show();
					console.log('findById success: ' + data.name);
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

function renderDetails(paciente) {
	$('#pacienteId').val(paciente.id);
	$('#name').val(paciente.name);
	$('#apellidos').val(paciente.apellidos);
	$('#direccion').val(paciente.direccion);
	$('#telefono').val(paciente.telefono);
	$('#fechaNacimiento').val(paciente.fechaNacimiento);
	$('#alergico').val(paciente.alergico);
	$('#notas').val(paciente.notas);
}

// Helper function to serialize all the form fields into a JSON string
function formToJSON() {
	return JSON.stringify({
		"id" : $('#id').val(),
		"name" : $('#name').val(),
		"apellidos" : $('#apellidos').val(),
		"direccion" : $('#direccion').val(),
		"telefono" : $('#telefono').val(),
		"fechaNacimiento" : $('#fechaNacimiento').val(),
		"notas" : $('#notas').val(),
		"alergico" : $('#alergico').val()
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
	/*
	 * $messageDiv = $('#messagesDiv'); // get the reference of the div
	 * $messageDiv.show().html(message); // show and set the message
	 * setTimeout(function() { $messageDiv.hide().html(''); }, 4000); // 4
	 * seconds later, hide // and clear the message
	 */
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