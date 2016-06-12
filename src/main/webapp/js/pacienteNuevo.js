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

$(document).ready(function() {

	$("#btnSave").click(function() {
		createPaciente();
		return false;
	});

});

function createPaciente() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		// dataType : "json",
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			var location = jqXHR.getResponseHeader('Location');
			var split = location.split("/");
			$("#btnSave").prop('disabled', true);
			$(":input").prop('disabled', true);
			$("#notas").prop('disabled', true);
			showSuccessMessage(split[split.length - 1]);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(textStatus);
		}
	});
}

function formToJSON() {
	return JSON.stringify({
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
