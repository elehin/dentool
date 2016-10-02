var currentPersonal;

$(document).ready(function() {

	if (getUrlParameter("personal") != '') {
		getPersonal(getUrlParameter("personal"));
	}

	$("#btnSave").click(function() {
		updatePersonal();
		return false;
	});

});

function getPersonal(id) {
	$.ajax({
		type : 'GET',
		url : personalURL + id,
		success : function(data) {
			currentPersonal = data;
			renderDetails(currentPersonal);
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

function renderDetails(personal) {
	$('#personalId').val(personal.id);
	$('#apellidos').val(personal.apellidos);
	$('#nombre').val(personal.nombre);
	$('#puesto').val(personal.puesto);
	$('#activo').prop('checked', personal.activo);
}

function updatePersonal() {

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : personalURL + 'update',
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

function formToJSON() {
	return JSON.stringify({
		"id" : $("#personalId").val(),
		"nombre" : $('#nombre').val(),
		"apellidos" : $('#apellidos').val(),
		"puesto" : $('#puesto').val(),
		"activo" : $('#activo').prop('checked'),
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
