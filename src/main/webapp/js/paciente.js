//var rootURL = 'https://dentool-elehin.rhcloud.com/service/paciente/';
var rootURL = 'http://localhost:8080/service/paciente/';
var diagnosticoURL = 'http://localhost:8080/service/diagnostico/';
var tratamientoURL = 'http://localhost:8080/service/tratamiento/';

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

	$("#btnAddDiagnostico").click(function() {
		addDiagnostico();
		return false;
	});

	getTratamientosList();

});

function getTratamientosList() {
	$.ajax({
		type : 'GET',
		url : tratamientoURL,
		// dataType : "json",
		success : function(data) {
			$.each(data,
					function(i, item) {
						$('#tratamiento').append(
								$('<option>').text(item.nombre).attr('value',
										item.id));
					});
			$('#tratamiento').selectpicker('refresh');
		}
	});
}

function addDiagnostico() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : diagnosticoURL + 'add',
		data : formToJSON('addDiagnostico'),
		success : function(rdata, textStatus, jqXHR) {
			showDiagnosticoSuccessMessage();
			$('#addDiagDiv').toggleClass("in");
			// findPaciente(currentPaciente.id);
			findDiagnosticoByUrl(jqXHR.getResponseHeader('Location'));
			$('#addDiagForm')[0].reset();

		},
		error : function(jqXHR, textStatus, errorThrown) {
			showErrorMessage(textStatus);
		}
	});
}

function findDiagnosticoByUrl(url) {
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	var pagado = '<button class="btn btn-danger padding-0-4" role="button"><span class="glyphicon glyphicon-euro"></span></button>';

	$.ajax({
		type : 'GET',
		url : url,
		// dataType : "json",
		success : function(data) {
			var table = $('#tableUltimosTratamientos').DataTable({
				"retrieve" : true
			});
			table.row.add([ data.id, lupa, pagado, data.tratamiento.nombre ])
					.draw(false);
		}
	});
}

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
	populateLastDiagnosticos();
}

function populateLastDiagnosticos() {
	getDiagnosticosByPaciente(currentPaciente.id);

}

function renderTableDiagnosticos(diagnosticos) {
	// var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	// $('#ultimosTratamientosBody').empty();

	/*
	 * $.each(diagnosticos, function(i, item) { trHTML += '<tr><td>' +
	 * item.id + '</td><td>' + lupa + '</td><td>' +
	 * item.tratamiento.nombre + '</td></tr>'; });
	 * $("#ultimosTratamientosBody").append(trHTML);
	 */

	var dataset = [];
	$.each(diagnosticos, function(i, item) {
		row = [ item.id, lupa, '', item.tratamiento.nombre ];
		dataset.push(row);
	});

	searchTable = $('#tableUltimosTratamientos').DataTable({
		"retrieve" : true,
		"paging" : false,
		"searching" : false,
		"info" : false,
		"data" : dataset,
		"columns" : [ {
			"title" : "id"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "&nbsp;"
		}, {
			"title" : "Tratamiento"
		}, ],
		"columnDefs" : [ {
			"className" : "never",
			"targets" : [ 0 ],
			"visible" : false
		} ],
	});

	$('#tableUltimosTratamientos tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'paciente.html?paciente=' + data[0];
		window.location.replace(url);
	});

}

function getDiagnosticosByPaciente(paciente) {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + 'paciente/' + paciente,
		// dataType : "json",
		success : function(data) {
			renderTableDiagnosticos(data);
		}
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

function formToJSON(action) {
	if (action == 'addDiagnostico') {
		return JSON.stringify({
			"tratamiento" : {
				"id" : $('#tratamiento').val()
			},
			"paciente" : {
				"id" : $('#pacienteId').val()
			},
			"iniciado" : false,
			"finalizado" : false
		});
	} else {
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
}

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#success-alert").hide();
		});
	}, 0);
}

function showDiagnosticoSuccessMessage() {
	$("#diagnostico-success-alert").alert();
	window.setTimeout(function() {
		$("#diagnostico-success-alert").fadeTo(2000, 500).slideUp(500,
				function() {
					$("#diagnostico-success-alert").hide();
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
	$.ajax({
		type : 'GET',
		url : url,
		// dataType : "json",
		success : function(data) {
			currentPaciente = data;
			renderDetails(currentPaciente);
		}
	});
}
