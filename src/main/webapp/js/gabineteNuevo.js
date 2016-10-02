var currentPersonal;

$(document).ready(function() {

	$("#btnSave").click(function() {
		createGabinete();
		return false;
	});

	$("#nombre").focus();

});

function createGabinete() {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : gabineteURL,
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
		"nombre" : $('#nombre').val(),
		"especialidad" : $('#especialidad').val(),
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
