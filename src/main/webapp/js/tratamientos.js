var rootURL = 'https://dentool-elehin.rhcloud.com/service/tratamiento/';
// var rootURL = 'http://localhost:8080/service/tratamiento/';
// var serverURL = 'http://localhost:8080/';
var serverURL = 'https://dentool-elehin.rhcloud.com/';

// ################### document.ready() ##################################
$(document).ready(function() {

	getTratamientos();

	$("#btnSearch").click(function() {
		buscarTratamiento();
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
	$('#tableTratamientosBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.nombre + '</td><td>' + item.precio + '</td></tr>';
	});
	$("#tableTratamientosBody").append(trHTML);

	searchTable = $('#tableTratamientos').DataTable({
		"retrieve" : false,
		"paging" : true,
		"searching" : false,
		"info" : true,
		"columnDefs" : [ {
			"targets" : [ 0 ],
			"visible" : false
		} ],
		"order" : [ 3, "asc" ],
		"language" : {
			"search" : "Buscar:"
		}
	});

	$('#tableTratamientos tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'tratamiento.html?tratamiento=' + data[0];
		window.location.replace(url);
	});
}

function buscarTratamiento() {
	key = $("#searchKey").val();

	var url = serverURL + 'tratamientoMultiple.html?key=' + key;
	window.location.replace(url);
}

function getTratamientos() {
	$.ajax({
		type : 'GET',
		url : rootURL,
		// dataType : "json",
		success : function(data) {
			populateTable(data);
		}
	});
}