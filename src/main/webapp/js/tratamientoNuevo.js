var currentPaciente;

$(document).ready(function() {

	$("#btnSave").click(function() {
		createTratamiento('back');
		return false;
	});

	$("#btnSaveAndNew").click(function() {
		createTratamiento('new');
		return false;
	});

	$("#name").focus();

	$("input:text, #precio").focus(function() {
		$(this).select();
		return false;
	});

});

function createTratamiento(nextAction) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		// dataType : "json",
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			// var location = jqXHR.getResponseHeader('Location');
			// var split = location.split("/");
			// $("#btnSave").prop('disabled', true);
			// $(":input").prop('disabled', true);
			// $("#notas").prop('disabled', true);
			// showSuccessMessage(split[split.length - 1]);
			showSuccessMessage(nextAction);
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

function formToJSON() {
	return JSON.stringify({
		"nombre" : $('#name').val(),
		"precio" : $('#precio').val()
	});
}

function showSuccessMessage(nextAction) {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
			$("#success-alert").hide();
			var url;
			if (nextAction == 'back') {
				url = serverURL + 'tratamientos.html';
			} else if (nextAction == 'new') {
				url = serverURL + 'tratamientoNuevo.html';
			}
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
