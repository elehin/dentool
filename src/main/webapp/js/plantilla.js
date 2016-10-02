// ################### document.ready() ##################################
$(document).ready(function() {

	getPlantilla();

});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#tablePersonalBody').empty();

	$.each(dataset, function(i, item) {
		var activo = '';
		if (!item.activo) {
			activo = '<span class="glyphicon glyphicon-ban-circle"></span>';
		}
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.nombre + '</td><td>' + item.apellidos + '</td><td>'
				+ item.puesto + '</td><td>' + activo + '</td></tr>';
	});
	$("#tablePersonalBody").append(trHTML);

	searchTable = $('#tablePersonal')
			.DataTable(
					{
						"retrieve" : false,
						"paging" : true,
						"searching" : true,
						"info" : true,
						"columnDefs" : [ {
							"targets" : [ 0 ],
							"visible" : false
						} ],
						"order" : [ 3, "asc" ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontró personal",
							"sEmptyTable" : "No hay personal",
							"sInfo" : "Mostrando personal del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando personal del 0 al 0 de un total de 0 usuarios",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						}
					});

	$('#tablePersonalBody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'personal.html?personal=' + data[0];
		window.location.replace(url);
	});
}

function getPlantilla() {
	$.ajax({
		type : 'GET',
		url : personalURL + 'list',
		success : function(data) {
			populateTable(data);
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