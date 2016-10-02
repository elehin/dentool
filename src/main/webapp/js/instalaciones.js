// ################### document.ready() ##################################
$(document).ready(function() {

	getGabinetes();

});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#tableGabinetesBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.nombre + '</td><td>' + item.especialidad + '</td></tr>';
	});
	$("#tableGabinetesBody").append(trHTML);

	searchTable = $('#tableGabinetes')
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
							"sZeroRecords" : "No se encontraron gabinetes",
							"sEmptyTable" : "No hay gabinetes",
							"sInfo" : "Mostrando gabinetes del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando gabinetes del 0 al 0 de un total de 0 gabinetes",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						}
					});

	$('#tableGabinetesBody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'gabinete.html?gabinete=' + data[0];
		window.location.replace(url);
	});
}

function getGabinetes() {
	$.ajax({
		type : 'GET',
		url : gabineteURL + 'list',
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