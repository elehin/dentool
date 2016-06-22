var currentPaciente;

$(document).ready(function() {
	if (getUrlParameter("tratamiento") != '') {
		findTratamiento(getUrlParameter("tratamiento"));
	}

	$("#btnSave").click(function() {
		updateTratamiento();
		return false;
	});

	$("input:text, #precio").focus(function() {
		$(this).select();
		return false;
	});

});

function updateTratamiento() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL + 'update',
		// dataType : "json",
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
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

function findTratamiento(id) {
	$.ajax({
		type : 'GET',
		url : rootURL + id,
		// dataType : "json",
		success : function(data) {
			renderDetails(data);
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

function renderDetails(tratamiento) {
	$('#tratamientoId').val(tratamiento.id);
	$('#name').val(tratamiento.nombre);
	$('#precio').val(tratamiento.precio);
}

function formToJSON() {
	return JSON.stringify({
		"id" : $('#tratamientoId').val(),
		"nombre" : $('#name').val(),
		"precio" : $('#precio').val()
	});
}

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
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
