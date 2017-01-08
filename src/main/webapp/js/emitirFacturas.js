var rows_selected;
var paciente;
var pacienteId;

$(document).ready(function() {

	$("#btnFactura").click(function() {
		emitirFacturas();
		return false;
	});

	$("#btnSave").click(function() {
		updatePaciente();
		return false;
	});

	rows_selected = [];

	findPacientesParaFactura();
	checkNoFacturables();
	hayPacientesConPagosFacturables();

});

function findPacientesParaFactura() {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + "noFacturados",
		success : function(data) {
			populateTable(data);
			updateTotalPanel(data);
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

function populateTable(diagnosticos) {
	var dataset = [];

	$.each(diagnosticos, function(i, item) {
		dataset.push(renderDiagTableRow(item));
		rows_selected.push(item.pacienteId);
	});

	pacientesTable = $('#tablePacientes')
			.DataTable(
					{
						"retrieve" : true,
						"paging" : false,
						"searching" : false,
						"info" : false,
						"ordering" : true,
						"data" : dataset,
						"columns" : [ {
							"title" : "pacienteId"
						}, {
							"title" : ""
						}, {
							"title" : ""
						}, {
							"title" : "Paciente"
						}, {
							"title" : "NIF"
						}, {
							"title" : "Importe"
						}, {
							"title" : "Tratado"
						} ],
						"columnDefs" : [ {
							"className" : "never",
							"targets" : [ 0 ],
							"visible" : false
						}, {
							'targets' : 1,
							'searchable' : false,
							'orderable' : false,
							'width' : '1%',
							'className' : 'dt-body-center',
							'render' : function(data, type, full, meta) {
								return '<input type="checkbox">';
							}
						}, {
							'targets' : 2,
							'searchable' : false,
							'orderable' : false
						} ],
						"select" : {
							"style" : "os",
							"selector" : "td:first-child"
						},
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay ningún paciente con tratamientos para facturar",
							"sInfo" : "Mostrando pacientes del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando pacientes del 0 al 0 de un total de 0",
							"oPaginate" : {
								"sFirst" : "Primero",
								"sLast" : "Último",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						},
						'rowCallback' : function(row, data, dataIndex) {
							// Get row ID
							var rowId = data[0];

							// If row ID is in the list of selected row IDs
							if ($.inArray(rowId, rows_selected) !== -1) {
								$(row).find('input[type="checkbox"]').prop(
										'checked', true);
								$(row).removeClass('selected');
							}
						}
					});

	$('input[type="checkbox"]').prop('checked', true);

	// Handle click on checkbox
	$('#tablePacientes tbody tr').off('click');
	$('#tablePacientes tbody').on('click', 'input[type="checkbox"]',
			function(e) {
				var $row = $(this).closest('tr');

				// Get row data
				var data = pacientesTable.row($row).data();

				// Get row ID
				var rowId = data[0];

				// Determine whether row ID is in the list of selected row IDs
				var index = $.inArray(rowId, rows_selected);

				// If checkbox is checked and row ID is not in list of selected
				// row IDs
				if (this.checked && index === -1) {
					rows_selected.push(rowId);

					// Otherwise, if checkbox is not checked and row ID is in
					// list of
					// selected row IDs
				} else if (!this.checked && index !== -1) {
					rows_selected.splice(index, 1);
				}

				if (this.checked) {
					$row.removeClass('selected');
				} else {
					$row.addClass('selected');
				}

				// Actualizar el total del presupuesto
				updateTotalPanel(pacientesTable.data(), 'reload');

				// Prevent click event from propagating to parent
				e.stopPropagation();
			});

	$('#tablePacientes tbody').on(
			'click',
			'button',
			function(e) {
				var data = pacientesTable.row($(this).parents('tr')).data();
				url = serverURL + 'factura.html?paciente=' + data[0]
						+ '&origin=emitirFacturas.html';
				window.location.replace(url);

				// Prevent click event from propagating to parent
				e.stopPropagation();
			});
}

function renderDiagTableRow(item) {
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';

	row = [ item.pacienteId, '', lupa, item.name + ' ' + item.apellidos,
			item.dni, formatCurrency(item.importe), item.fecha ];

	return row;
}

function updateTotalPanel(data, action) {
	var total = 0;
	if (action === undefined) {
		$.each(data, function(i, item) {
			total += item.importe;
		});

		$("#hTotalPanel").text(formatCurrency(total));
	} else {
		$.each(data, function(i, item) {
			var index = $.inArray(item[0], rows_selected);
			if (index !== -1) {
				total += parseInt(item[5]);
			}
		});

		$("#hTotalPanel").text(formatCurrency(total));
	}
}

function checkNoFacturables() {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + "noFacturables",
		success : function(data) {
			if (data !== undefined && data.length > 0) {
				showNoFactMessage();
				populateNoFacturablesTable(data);
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

function populateNoFacturablesTable(diagnosticos) {
	var dataset = [];

	$.each(diagnosticos, function(i, item) {
		dataset.push(renderNoFacturablesTableRow(item));
	});

	noFacturablesTable = $('#tableNoFacturables')
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
							"sEmptyTable" : "No hay ningún paciente con tratamientos para facturar",
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

	$('#tableNoFacturables tbody tr').off('click');
	$('#tableNoFacturables tbody')
			.on(
					'click',
					'button',
					function(e) {
						var data = noFacturablesTable
								.row($(this).parents('tr')).data();
						url = serverURL + 'factura.html?paciente=' + data[0]
								+ '&origin=emitirFacturas.html';
						window.location.replace(url);

						// Prevent click event from propagating to parent
						e.stopPropagation();
					});
}

function renderNoFacturablesTableRow(item) {
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';

	row = [ item.pacienteId, lupa, item.name + ' ' + item.apellidos ];

	return row;
}

function emitirFacturas() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : facturaURL + 'emiteFacturas',
		data : JSON.stringify({
			"pacientes" : rows_selected,
			"fechaFactura" : $("#otherDate").val()
		}),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
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

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#success-alert").hide();
		});
	}, 0);
}

function showNoFactMessage() {
	$("#noFacturables-alert").show();
}

function hayPacientesConPagosFacturables() {
	$.ajax({
		type : 'GET',
		url : pacienteURL + "hayPagosNoFacturados",
		success : function(response) {
			if (response) {
				$("#pagosFacturablesDiv").addClass("in");
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