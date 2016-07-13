var currentPaciente;

$(document).ready(function() {

	$("#btnSubmit").click(function() {
		login();
		return false;
	});

	if (getUrlParameter('action') == 'logout') {
		$.removeCookie('restTokenC');
	}

});

function login() {

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : authenticationURL,
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			var token = rdata.token;
			$.cookie('restTokenC', token, {
				expires : 3,
				path : '/'
			});
			window.location.replace(serverURL);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(errorThrown);
			$.removeCookie('restTokenC');
		}
	});

}

function formToJSON() {
	return JSON.stringify({
		"username" : $('#username').val(),
		"password" : $('#password').val()
	});
}

function showErrorMessage(error) {
	$("#error-alert").alert();
	window.setTimeout(function() {
		$("#error-alert").fadeTo(10000, 500).slideUp(500, function() {
			$("#error-alert").hide();
		});
	}, 0);
}
