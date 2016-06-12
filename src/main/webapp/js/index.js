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

// ################### document.ready() ##################################
$(document).ready(function() {

	getLastChangedPacientes();

	$("#btnSearch").click(function() {
		buscarPaciente();
		return false;
	});

	$('#searchKey').keypress(function(e) {
		if (e.which == '13') {
			buscarPaciente();
		}
	});

});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#ultimosPacientesBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.name + '</td><td>' + item.apellidos + '</td><td>'
				+ item.telefono + '</td><td>' + item.lastChange + '</td></tr>';
	});
	$("#ultimosPacientesBody").append(trHTML);

	searchTable = $('#tableUltimosPacientes').DataTable({
		"retrieve" : false,
		"paging" : false,
		"searching" : false,
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

	$('#tableUltimosPacientes tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'paciente.html?paciente=' + data[0];
		window.location.replace(url);
	});
}

function buscarPaciente() {
	key = $("#searchKey").val();
	if ($.isNumeric(key)) {
		if (key.length == 9) {
			var url = serverURL + 'pacienteMultiple.html?key=' + key;
			window.location.replace(url);
		} else {
			var url = serverURL + 'paciente.html?paciente=' + key;
			window.location.replace(url);
		}
	} else {
		var url = serverURL + 'pacienteMultiple.html?key=' + key;
		window.location.replace(url);
	}
	return false;
}

function getLastChangedPacientes() {
	$.ajax({
		type : 'GET',
		url : rootURL + 'lastChanges',
		// dataType : "json",
		success : function(data) {
			populateTable(data);
		}
	});
}