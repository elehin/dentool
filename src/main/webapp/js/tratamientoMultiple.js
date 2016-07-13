$(document).ready(function() {
	var key = getUrlParameter("key");
	if (key != '') {
		findTratamiento(key);
	}

});

function findTratamiento(key) {
	$.ajax({
		type : 'GET',
		url : tratamientoURL + "nombre/" + key,
		// dataType : "json",
		success : function(data) {
			if (data.length == 1) {
				url = serverURL + 'tratamiento.html?tratamiento=' + data[0].id;
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
	$('#tableTratamientosBody').empty();

	$.each(dataset,
			function(i, item) {
				trHTML += '<tr><td>' + item.id + '</td><td>' + lupa
						+ '</td><td>' + item.nombre + '</td><td>' + item.precio
						+ ' €' + '</td></tr>';
			});
	$("#tableTratamientosBody").append(trHTML);

	searchTable = $('#tableTratamientos')
			.DataTable(
					{
						"retrieve" : false,
						"paging" : true,
						"pagingType" : "simple",
						"searching" : true,
						"info" : false,
						"columnDefs" : [ {
							"targets" : [ 0 ],
							"visible" : false
						} ],
						"order" : [ [ 2, "asc" ] ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "Ningún dato disponible en esta tabla",
							"sInfo" : "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
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
