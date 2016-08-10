var currentPaciente;

$(document).ready(function() {

	$("#btnSave").click(function() {
		createUser();
		return false;
	});

	$("#confirmation").click(function() {
		return false;
	});

	$("#username").focus();

	$('#username').focusout(function() {
		checkEmail();
		return false;
	})

	$('#password').focusout(function() {
		checkPwdStrength();
		checkPasswordConfirmation();
		return false;
	})

	$('#confirmation').focusout(function() {
		checkPasswordConfirmation();
		return false;
	})
	
	$("#activo").prop('checked', true);

});

function createUser() {
	var isCorrect = true;

	$('.form-control-feedback').remove();
	$('.form-group').removeClass("has-error has-feedback");

	if ($('#password').val() != $("#confirmation").val()) {
		$("#confirmationDiv").addClass("has-error has-feedback");
		$("#confirmationDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		isCorrect = false;
	} else {
		$('#confirmationDiv').addClass("has-success has-feedback");
		$('#confirmationDiv')
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	}

	if (!isValidEmailAddress($('#username').val())) {
		$('#emailDiv').addClass("has-error has-feedback");
		$('#emailDiv')
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		isCorrect = false;
	} else {
		$('#emailDiv').addClass("has-success has-feedback");
		$('#emailDiv')
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	}

	if (getPwdStrength($("#password").val()) == 'short') {
		$("#passwordDiv").addClass("has-error has-feedback");
		$("#passwordDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		isCorrect = false;
	} else if (getPwdStrength($("#password").val()) == 'weak') {
		$("#passwordDiv").addClass("has-error has-feedback");
		$("#passwordDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		isCorrect = false;
	} else {
		$('#passwordDiv').addClass("has-success has-feedback");
		$('#passwordDiv')
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	}

	if (isCorrect) {
		$.ajax({
			type : 'PUT',
			contentType : 'application/json',
			url : authenticationURL,
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
}

function formToJSON() {
	return JSON.stringify({
		"username" : $('#username').val(),
		"password" : $('#password').val(),
		"nombre" : $('#nombre').val(),
		"apellidos" : $('#apellidos').val(),
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

function checkPasswordConfirmation() {
	if ($('#password').val() != $("#confirmation").val()) {
		$('#confirmationDiv .glyphicon-ok.form-control-feedback').remove();
		$('#confirmationDiv').removeClass("has-success has-feedback");
		$("#confirmationDiv").addClass("has-error has-feedback");
		$("#confirmationDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
	} else {
		$('#confirmationDiv .glyphicon-remove.form-control-feedback').remove();
		$('#confirmationDiv').removeClass("has-error has-feedback");
		$("#confirmationDiv").addClass("has-success has-feedback");
		$("#confirmationDiv")
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	}
}

function checkEmail() {
	if (isValidEmailAddress($('#username').val())) {
		$('#emailDiv .glyphicon-remove.form-control-feedback').remove();
		$('#emailDiv').removeClass("has-error has-feedback");
		$('#emailDiv').addClass("has-success has-feedback");
		$('#emailDiv')
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	} else {
		$('#emailDiv .glyphicon-ok.form-control-feedback').remove();
		$('#emailDiv').removeClass("has-success has-feedback");
		$('#emailDiv').addClass("has-error has-feedback");
		$('#emailDiv')
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
	}
}

function isValidEmailAddress(emailAddress) {
	var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
	return pattern.test(emailAddress);
}

function checkPwdStrength() {
	if (getPwdStrength($("#password").val()) == 'short') {
		$('#passwordDiv .glyphicon-ok.form-control-feedback').remove();
		$('#passwordDiv').removeClass("has-success has-feedback");
		$("#passwordDiv").addClass("has-error has-feedback");
		$("#passwordDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
	} else if (getPwdStrength($("#password").val()) == 'weak') {
		$('#passwordDiv .glyphicon-ok.form-control-feedback').remove();
		$('#passwordDiv').removeClass("has-success has-feedback");
		$("#passwordDiv").addClass("has-error has-feedback");
		$("#passwordDiv")
				.append(
						'<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
	} else {
		$('#passwordDiv .glyphicon-remove.form-control-feedback').remove();
		$('#passwordDiv').removeClass("has-error has-feedback");
		$("#passwordDiv").addClass("has-success has-feedback");
		$("#passwordDiv")
				.append(
						'<span class="glyphicon glyphicon-ok form-control-feedback"></span>');
	}
}

function getPwdStrength(password) {
	// initial strength
	var strength = 0

	// if the password length is less than 6, return message.
	if (password.length < 6) {
		return 'short'
	}

	// length is ok, lets continue.

	// if length is 8 characters or more, increase strength value
	if (password.length > 7)
		strength += 1

		// if password contains both lower and uppercase characters, increase
		// strength value
	if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/))
		strength += 1

		// if it has numbers and characters, increase strength value
	if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/))
		strength += 1

		// if it has one special character, increase strength value
	if (password.match(/([!,%,&,@,#,$,^,*,?,_,~])/))
		strength += 1

		// if it has two special characters, increase strength value
	if (password.match(/(.*[!,%,&,@,#,$,^,*,?,_,~].*[!,%,&,@,#,$,^,*,?,_,~])/))
		strength += 1

		// now we have calculated strength value, we can return messages

		// if value is less than 2
	if (strength < 2) {
		return 'weak'
	} else if (strength == 2) {
		return 'good'
	} else {
		return 'strong'
	}
}
