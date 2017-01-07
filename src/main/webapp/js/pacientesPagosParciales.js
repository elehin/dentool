$(document).ready(function() {
	findPacientesConPagosFacturables();

});

function findPacientesConPagosFacturables() {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "pagosNoFacturados",
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

function populateTable(pacientes) {
	var dataset = [];

	$.each(pacientes, function(i, item) {
		dataset.push(renderDiagTableRow(item));
	});

	pacientesTable = $('#tablePacientes')
			.DataTable(
					{
						"retrieve" : true,
						"paging" : false,
						"searching" : false,
						"info" : false,
						"ordering" : false,
						"data" : dataset,
						"columns" : [ {
							"title" : "pacienteId"
						}, {
							"title" : ""
						}, {
							"title" : "Paciente"
						} ],
						"columnDefs" : [ {
							"className" : "never",
							"targets" : [ 0 ],
							"visible" : false
						} ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay ningún paciente con pagos parciales para facturar",
							"sInfo" : "Mostrando pacientes del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando pacientes del 0 al 0 de un total de 0",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						}
					});

	$('#tablePacientes tbody tr').off('click');

	$('#tablePacientes tbody').on(
			'click',
			'button',
			function(e) {
				var data = pacientesTable.row($(this).parents('tr')).data();
				url = serverURL + 'factura.html?paciente=' + data[0]
						+ '&origin=pacientesPagosParciales.html';
				window.location.replace(url);

				// Prevent click event from propagating to parent
				e.stopPropagation();
			});
}

function renderDiagTableRow(item) {
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';

	row = [ item.id, lupa, item.name + ' ' + item.apellidos ];

	return row;
}
