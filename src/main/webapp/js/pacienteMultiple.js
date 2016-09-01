$(document).ready(function() {
	var key = getUrlParameter("key");
	if (key != '') {
		if ($.isNumeric(key)) {
			findPacienteByTelefono(key);
		} else if (isDni(key)) {
			findPacienteByDni(key);
		} else {
			// findPacienteByApellidos(key);
			findPacienteByFullName(key);
		}
	}

});

function findPacienteByFullName(fullName) {
	var nombre, apellido, completeURL, buscaPorNombre, buscaPorNombreCompuesto;

	var tokens = new Array();
	tokens = fullName.toString().split(' ');

	if (tokens.length == 1) {
		apellido = tokens[0];
		completeURL = pacienteURL + "apellido/" + apellido;
		buscaPorNombre = true;

	} else if (tokens.length == 2) {
		nombre = tokens[0];
		apellido = tokens[1];
		completeURL = pacienteURL + "nombre/" + nombre + "/apellido/"
				+ apellido
	} else if (tokens.length == 3) {
		nombre = tokens[0];
		apellido = tokens[1] + ' ' + tokens[2];
		completeURL = pacienteURL + "nombre/" + nombre + "/apellido/"
				+ apellido;
		buscaPorNombreCompuesto = true;
	}

	$.ajax({
		type : 'GET',
		url : completeURL,
		// dataType : "json",
		success : function(data) {
			if (data.length == 0 && buscaPorNombre) {
				findPacienteByNombre(fullName);
			} else if (data.length == 0 && buscaPorNombreCompuesto) {
				findPacienteByNombre(tokens[0] + ' ' + tokens[1]);
			} else if (data.length == 1) {
				url = serverURL + 'paciente.html?paciente=' + data[0].id;
				window.location.replace(url);
			} else if (data.length > 1) {
				populateTable(data);
			}
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

function findPacienteByNombre(nombre) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "nombre/" + nombre,
		// dataType : "json",
		success : function(data) {
			if (data.length == 1) {
				url = serverURL + 'paciente.html?paciente=' + data[0].id;
				window.location.replace(url);
			} else if (data.length > 1) {
				populateTable(data);
			}
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

function findPacienteByApellidos(apellidos) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "apellido/" + apellidos,
		// dataType : "json",
		success : function(data) {
			if (data.length == 1) {
				url = serverURL + 'paciente.html?paciente=' + data[0].id;
				window.location.replace(url);
			} else if (data.length > 1) {
				populateTable(data);
			}
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

function findPacienteByTelefono(telefono) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "telefono/" + telefono,
		// dataType : "json",
		success : function(data) {
			if (data.length == 1) {
				url = serverURL + 'paciente.html?paciente=' + data[0].id;
				window.location.replace(url);
			} else if (data.length > 1) {
				populateTable(data);
			}
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

function findPacienteByDni(dni) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "dni/" + dni,
		// dataType : "json",
		success : function(data) {
			if (data.length == 1) {
				url = serverURL + 'paciente.html?paciente=' + data[0].id;
				window.location.replace(url);
			} else if (data.length > 1) {
				populateTable(data);
			}
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

function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#pacientesBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.name + '</td><td>' + item.apellidos + '</td><td>'
				+ item.telefono + '</td><td>' + item.direccion + '</td><td>'
				+ item.lastChange + '</td></tr>';
	});
	$("#pacientesBody").append(trHTML);

	searchTable = $('#tablePacientes').DataTable({
		"retrieve" : false,
		"paging" : false,
		"pagingType" : "simple",
		"searching" : true,
		"info" : false,
		"columnDefs" : [ {
			"targets" : [ 0 ],
			"visible" : false
		} ],
		"order" : [ [ 5, "desc" ], [ 0, "desc" ] ],
		"language" : {
			"search" : "Buscar:"
		}
	});

	$('#tablePacientes tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'paciente.html?paciente=' + data[0];
		window.location.replace(url);
	});
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

function isDni(str) {
	var patt = new RegExp(/^\d{8}[a-zA-Z]$/);
	var res = patt.test(str);
	return res;
}