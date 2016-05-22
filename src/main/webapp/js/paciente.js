/**
 * 
 */

// var rootURL = 'https://dentool-elehin.rhcloud.com/service/paciente/';
var rootURL = 'http://localhost:8080/service/paciente/';

var currentPaciente;
var searchTable;
var searchDialog;

$(document).ready(function() {
	if (getUrlParameter("paciente") != '') {
		findPaciente(getUrlParameter("paciente"));
	}

	$("#btnSave").click(function() {
		updatePaciente();
		return false;
	});

});

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
	$('#pacienteId').val(paciente.id);
	$('#name').val(paciente.name);
	$('#apellidos').val(paciente.apellidos);
	$('#direccion').val(paciente.direccion);
	$('#telefono').val(paciente.telefono);
	$('#fechaNacimiento').val(paciente.fechaNacimiento);
	$('#notas').val(paciente.notas);
	$('#alergico').prop('checked', paciente.alergico);
	$('#dni').val(paciente.dni);
	$("#btnSave").attr('value', 'Modificar');
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

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#success-alert").hide();
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

function findPacienteByUrl(url) {
	$
			.ajax({
				type : 'GET',
				url : url,
				// dataType : "json",
				success : function(data) {
					currentPaciente = data;
					renderDetails(currentPaciente);
				}
			});
}
