var rows_selected;
var pacienteId;

$(document).ready(
		function() {

			$("#btnCreaPresupuesto").click(function() {
				console.log(formToJSON());
				createPresupuesto();
				return false;
			});

			if (getUrlParameter("paciente") != '') {
				pacienteId = getUrlParameter("paciente");
				findPaciente(pacienteId);
				$('#backArrowLink').attr("href",
						serverURL + 'paciente.html?paciente=' + pacienteId);
			}

			rows_selected = [];

			findDiagnosticosParaPresupuesto(pacienteId);

		});

function findDiagnosticosParaPresupuesto(paciente) {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + "notStarted/paciente/" + paciente,
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
		rows_selected.push(item.id);
	});

	diagsTable = $('#tableDiagnosticos').DataTable({
		"retrieve" : true,
		"paging" : false,
		"searching" : false,
		"info" : false,
		"ordering" : false,
		"data" : dataset,
		"columns" : [ {
			"title" : "id"
		}, {
			"title" : ""
		}, {
			"title" : "Tratamiento"
		}, {
			"title" : "Pieza"
		}, {
			"title" : "Precio"
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
		} ],
		"select" : {
			"style" : "os",
			"selector" : "td:first-child"
		},
		'rowCallback' : function(row, data, dataIndex) {
			// Get row ID
			var rowId = data[0];

			// If row ID is in the list of selected row IDs
			if ($.inArray(rowId, rows_selected) !== -1) {
				$(row).find('input[type="checkbox"]').prop('checked', true);
				$(row).removeClass('selected');
			}
		}
	});

	$('input[type="checkbox"]').prop('checked', true);

	// Handle click on checkbox
	$('#tableDiagnosticos tbody').on('click', 'input[type="checkbox"]',
			function(e) {
				var $row = $(this).closest('tr');

				// Get row data
				var data = diagsTable.row($row).data();

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
				updateTotalPanel(diagsTable.data(), 'reload');

				// Prevent click event from propagating to parent
				e.stopPropagation();
			});
}

function renderDiagTableRow(item) {

	var pieza;
	if (item.pieza == '0') {
		pieza = '';
	} else {
		pieza = item.pieza;
	}

	row = [ item.id, '', item.tratamiento.nombre, pieza, item.precio + " €" ];

	return row;
}

function updateTotalPanel(data, action) {
	var total = 0;
	if (action === undefined) {
		$.each(data, function(i, item) {
			total += item.precio;
		});

		$("#hTotalPanel").text(total + " €");
	} else {
		$.each(data, function(i, item) {
			var index = $.inArray(item[0], rows_selected);
			if (index !== -1) {
				total += parseInt(item[4]);
			}
		});

		$("#hTotalPanel").text(total + " €");
	}
}

function createPresupuesto() {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : presupuestoURL,
		dataType : 'native',
		data : formToJSON(),
		xhrFields : {
			responseType : 'blob'
		},
		success : function(rdata, textStatus, jqXHR) {
			var blob = new Blob([ rdata ]);
			var link = document.createElement('a');
			var fileName = jqXHR.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
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

function findPaciente(id) {
	$.ajax({
		type : 'GET',
		url : pacienteURL + id,
		success : function(data) {
			var paciente = data.name + ' ' + data.apellidos;
			$('#backArrowLink').after(paciente);
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

function formToJSON() {

	var diagnosticos = new Array();

	$.each(rows_selected, function(i, item) {
		var diagnostico = {};
		diagnostico['id'] = item;
		diagnosticos.push(diagnostico);
	});

	return JSON.stringify({
		"diagnosticos" : diagnosticos,
		"pacienteId" : pacienteId
	});
}
