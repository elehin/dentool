// ################### document.ready() ##################################
$(document).ready(function() {

	getTratamientos();

	$("#btnSearch").click(function() {
		buscarTratamiento();
		return false;
	});

	$('#searchKey').keypress(function(e) {
		if (e.which == '13') {
			buscarTratamiento();
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

	searchTable = $('#tableTratamientos')
			.DataTable(
					{
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
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "Ningún dato disponible en esta tabla",
							"sInfo" : "Mostrando registros del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando registros del 0 al 0 de un total de 0 registros",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
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