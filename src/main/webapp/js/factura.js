var rows_selected;
var paciente;
var pacienteId;

var pagos_rows_selected;
var muestraPagos = false;

$(document)
		.ready(
				function() {

					$("#btnCreaFactura").click(function() {
						// console.log(formToJSON());
						createFactura();
						return false;
					});

					$("#btnSave").click(function() {
						updatePaciente();
						return false;
					});

					if (getUrlParameter("paciente") != '') {
						pacienteId = getUrlParameter("paciente");
						findPaciente(pacienteId);
						if (getUrlParameter("origin") !== undefined) {
							$('#backArrowLink').attr("href",
									serverURL + getUrlParameter("origin"));
						} else {
							$('#backArrowLink').attr(
									"href",
									serverURL + 'paciente.html?paciente='
											+ pacienteId);
						}
					}

					rows_selected = [];
					pagos_rows_selected = [];

					findDiagnosticosParaFactura(pacienteId);

					$("#btnMuestraPagos")
							.click(
									function() {
										if (muestraPagos) {
											findDiagnosticosParaFactura(pacienteId);
											$("#btnMuestraPagos")
													.html(
															"<span class='glyphicon glyphicon-zoom-in'></span> Mostrar pagos parciales");
										} else {
											findPagosParaFactura(pacienteId);
											$("#btnMuestraPagos")
													.html(
															"<span class='glyphicon glyphicon-zoom-out'></span> Mostrar tratamientos completados");
										}

										muestraPagos = !muestraPagos;
									})

				});

function checkNombreAndNif() {
	$('#name').removeClass("has-error has-feedback");
	$('#apellidos').removeClass("has-error has-feedback");
	$('#dni').removeClass("has-error has-feedback");

	if (!paciente.name || !paciente.apellidos || !paciente.dni) {
		$('#contentRow0').addClass('in');
		$('#name').val(paciente.name);
		$('#apellidos').val(paciente.apellidos);
		$('#dni').val(paciente.dni);

		if (!paciente.name) {
			$('#nameDiv').addClass("has-error has-feedback");
		}
		if (!paciente.apellidos) {
			$('#apellidosDiv').addClass("has-error has-feedback");
		}
		if (!paciente.dni) {
			$('#nifDiv').addClass("has-error has-feedback");
		}
	} else {
		if ($('#contentRow0').hasClass('in')) {
			$('#contentRow0').removeClass('in');
		}
	}
}

function findDiagnosticosParaFactura(paciente) {
	$.ajax({
		type : 'GET',
		url : diagnosticoURL + "noFacturados/paciente/" + paciente,
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

	diagsTable = $('#tableDiagnosticos')
			.DataTable(
					{
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
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay ningún tratamiento para facturar",
							"sInfo" : "Mostrando tratamientos del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "No hay trtamientos para facturar",
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

	$('#tableDiagnosticos input[type="checkbox"]').prop('checked', true);

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

	row = [ item.id, '', item.tratamiento.nombre, pieza,
			formatCurrency(item.precio) ];

	return row;
}

function updateTotalPanel(data, action) {
	var total = 0;
	var index;
	if (action === undefined) {
		$.each(data, function(i, item) {
			if (muestraPagos) {
				total += item.pago.cantidad;
			} else {
				total += item.precio;
			}
		});

		$("#hTotalPanel").text(formatCurrency(total));
	} else {
		$.each(data, function(i, item) {
			if (muestraPagos) {
				index = $.inArray(item[0], pagos_rows_selected);
			} else {
				index = $.inArray(item[0], rows_selected);
			}
			if (index !== -1) {
				total += parseInt(item[4]);
			}

		});

		$("#hTotalPanel").text(formatCurrency(total));
	}
}

function createFactura() {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : facturaURL + 'createandprint',
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
			paciente = data;
			var nombrePaciente = paciente.name + ' ' + paciente.apellidos;
			$('#backArrowLink').after(nombrePaciente);
			checkNombreAndNif();
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

function updatePaciente() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : pacienteURL + 'update',
		data : formToJSON('modificar'),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
			if (getUrlParameter("origin") != undefined) {
				window.location.replace(serverURL + getUrlParameter("origin"));
			}
			findPacienteByUrl(jqXHR.getResponseHeader('Location'));
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

function findPacienteByUrl(url) {
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			paciente = data;
			checkNombreAndNif();
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

function formToJSON(action) {
	if (action == 'modificar') {
		return JSON.stringify({
			"id" : paciente.id,
			"name" : $('#name').val(),
			"apellidos" : $('#apellidos').val(),
			"direccion" : paciente.direccion,
			"telefono" : paciente.telefono,
			"fechaNacimiento" : paciente.fechaNacimiento,
			"notas" : paciente.notas,
			"dni" : $('#dni').val(),
			"alergico" : paciente.alergico,
			"enfermoGrave" : paciente.enfermoGrave,
			"saldo" : paciente.saldo,
			"pacienteAnteriorADentool" : paciente.pacienteAnteriorADentool
		});
	} else {

		var lineasFactura = new Array();
		if (muestraPagos) {
			$.each(pagos_rows_selected, function(i, item) {
				var pago = {};
				pago['id'] = item;
				lineasFactura.push(pago);
			});

			return JSON.stringify({
				"pagos" : lineasFactura,
				"pacienteId" : pacienteId,
				"nombreFactura" : $('#otherName').val(),
				"nifFactura" : $('#otherDni').val(),
				"fecha" : $('#otherDate').val(),
			});

		} else {

			$.each(rows_selected, function(i, item) {
				var diagnostico = {};
				diagnostico['id'] = item;
				lineasFactura.push(diagnostico);
			});

			return JSON.stringify({
				"diagnosticos" : lineasFactura,
				"pacienteId" : pacienteId,
				"nombreFactura" : $('#otherName').val(),
				"nifFactura" : $('#otherDni').val(),
				"fecha" : $('#otherDate').val(),
			});
		}

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

function showErrorMessage(error) {
	$("#error-alert").alert();
	window.setTimeout(function() {
		$("#error-alert").fadeTo(2000, 500).slideUp(500, function() {
			$("#error-alert").hide();
		});
	}, 0);
}

function populateTablePagos(pagos) {
	var dataset = [];

	$.each(pagos, function(i, item) {
		dataset.push(renderDiagTablePagosRow(item));
		pagos_rows_selected.push(item.pago.id);
	});

	pagosTable = $('#tablePagos')
			.DataTable(
					{
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
							"title" : "Cantidad"
						}, {
							"title" : "%"
						}, {
							"title" : "Fecha"
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
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay ningún pago para facturar",
							"sInfo" : "Mostrando pagos del _START_ al _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando pagos de la 0 a la 0 de un total de 0 pagos",
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
							if ($.inArray(rowId, pagos_rows_selected) !== -1) {
								$(row).find('input[type="checkbox"]').prop(
										'checked', true);
								$(row).removeClass('selected');
							}
						}
					});

	$('#tablePagos input[type="checkbox"]').prop('checked', true);

	// Handle click on checkbox
	$('#tablePagos tbody').on('click', 'input[type="checkbox"]', function(e) {
		var $row = $(this).closest('tr');

		// Get row data
		var data = pagosTable.row($row).data();

		// Get row ID
		var rowId = data[0];

		// Determine whether row ID is in the list of selected row IDs
		var index = $.inArray(rowId, pagos_rows_selected);

		// If checkbox is checked and row ID is not in list of selected
		// row IDs
		if (this.checked && index === -1) {
			pagos_rows_selected.push(rowId);

			// Otherwise, if checkbox is not checked and row ID is in
			// list of
			// selected row IDs
		} else if (!this.checked && index !== -1) {
			pagos_rows_selected.splice(index, 1);
		}

		if (this.checked) {
			$row.removeClass('selected');
		} else {
			$row.addClass('selected');
		}

		// Actualiza el total de la factura
		updateTotalPanel(pagosTable.data(), 'reload');

		// Prevent click event from propagating to parent
		e.stopPropagation();
	});
}

function renderDiagTablePagosRow(item) {

	var pieza;
	if (item.diagnostico.pieza == '0') {
		pieza = '';
	} else {
		pieza = item.diagnostico.pieza;
	}

	row = [
			item.pago.id,
			'',
			item.diagnostico.tratamiento.nombre,
			pieza,
			formatCurrency(item.pago.cantidad),
			formatPorcentaje(item.pago.cantidad / item.diagnostico.precio * 100),
			item.pago.fecha ];

	return row;
}

function findPagosParaFactura(pacienteId) {
	$.ajax({
		type : 'GET',
		url : pagosURL + "noFacturados/paciente/" + pacienteId,
		success : function(data) {
			populateTablePagos(data);
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