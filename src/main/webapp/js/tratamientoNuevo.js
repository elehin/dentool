/**
 * 
 */

var rootURL = 'https://dentool-elehin.rhcloud.com/service/tratamiento/';
// var rootURL = 'http://localhost:8080/service/tratamiento/';
// var serverURL = 'http://localhost:8080/';
var serverURL = 'https://dentool-elehin.rhcloud.com/'

var currentPaciente;

$(document).ready(function() {

	$("#btnSave").click(function() {
		createTratamiento();
		return false;
	});

});

function createTratamiento() {
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
		"nombre" : $('#name').val(),
		"precio" : $('#precio').val()
	});
}

function showSuccessMessage(id) {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
			$("#success-alert").hide();
			url = serverURL + 'tratamiento.html?tratamiento=' + id;
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
