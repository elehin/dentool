var rows_selected;
var paciente;
var pacienteId;
var currentDate;

$(document).ready(function() {

	$("#bFacturasMes").click(function() {
		getZipFacturasMes();
		return false;
	});

	$("#bFacturasTrimestre").click(function() {
		getZipFacturasTrimestre();
		return false;
	});

	$("#bFacturasYear").click(function() {
		getZipFacturasYear();
		return false;
	});

	findFacturas();

	currentDate = new Date();
	setCombosValues()

});

function setCombosValues() {
	$("#mesesDropdown").val(currentDate.getMonth());

	var q = 0;
	if (currentDate.getMonth() >= 3 && currentDate.getMonth() <= 5) {
		q = 3;
	} else if (currentDate.getMonth() >= 6 && currentDate.getMonth() <= 8) {
		q = 6;
	} else if (currentDate.getMonth() >= 9 && currentDate.getMonth() <= 11) {
		q = 9;
	}
	$("#trimestreDropdown").val(q);

	$('#yearDropdown').append(
			$('<option>').text(currentDate.getFullYear()).attr('value',
					currentDate.getFullYear()));
	$('#yearDropdown').append(
			$('<option>').text(currentDate.getFullYear() - 1).attr('value',
					currentDate.getFullYear() - 1));
	$("#yearDropdown").val(currentDate.getFullYear());

}

function findFacturas() {
	$.ajax({
		type : 'GET',
		url : facturaURL + "lastFacturas",
		success : function(data) {
			populateTable(data);
			updatePanels();
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
		dataset.push(renderFacturaTableRow(item));
	});

	tableFacturas = $('#tableFacturas')
			.DataTable(
					{
						"retrieve" : true,
						"paging" : true,
						"pageLength" : 50,
						"searching" : true,
						"info" : false,
						"ordering" : true,
						"order" : [ [ 5, "desc" ] ],
						"data" : dataset,
						"columns" : [ {
							"title" : "id"
						}, {
							"title" : ""
						}, {
							"title" : "Paciente"
						}, {
							"title" : "NIF"
						}, {
							"title" : "Número"
						}, {
							"title" : "Fecha"
						}, {
							"title" : "Importe"
						} ],
						"columnDefs" : [ {
							"className" : "never",
							"targets" : [ 0 ],
							"visible" : false
						}, {
							"className" : "text-right",// "dt-right",
							"targets" : 6
						} ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron resultados",
							"sEmptyTable" : "No hay ningna facturar para mostrar",
							"sInfo" : "Mostrando facturas de la _START_ a la _END_ de un total de _TOTAL_",
							"sInfoEmpty" : "Mostrando facturas de la 0 a la 0 de un total de 0",
							"oPaginate" : {
								"sFirst" : "Primera",
								"sLast" : "Última",
								"sNext" : "Siguiente",
								"sPrevious" : "Anterior"
							}
						}
					});

	$('#tableFacturas tbody tr').off('click');
	$('#tableFacturas tbody tr').on('click', 'button', function(evt) {
		evt.stopPropagation();
		evt.preventDefault();

		row = $(this).parents('tr');

		var data = tableFacturas.row($(this).parents('tr')).data();
		descargaFactura(data);
	});
}

function renderFacturaTableRow(item) {
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-download-alt"></span></button>';

	row = [ item.id, lupa, item.nombreFactura, item.nifFactura, item.numero,
			item.fecha, formatCurrency(item.importe) ];

	return row;
}

function updatePanels() {
	$.ajax({
		type : 'GET',
		url : facturaURL + "importesFacturados",
		success : function(data) {
			var currenDate = new Date();
			if (data.stringMesCurso == undefined) {
				$("#sMesCurso").text(meses[currenDate.getMonth()]);
			} else {
				$("#sMesCurso").text(data.stringMesCurso);
			}
			if (data.stringMesAnterior == undefined) {
				$("#sMesAnterior").text(meses[currenDate.getMonth(-1)]);
			} else {
				$("#sMesAnterior").text(data.stringMesAnterior);
			}

			if (data.mes < data.mesAnterior) {
				$("#hmPanel").removeClass('text-success');
				$("#hmPanel").addClass('text-warning');
			}

			$("#hmPanel").text(formatCurrency(data.mes));
			$("#hmAnteriorPanel").text(formatCurrency(data.mesAnterior));
			$("#hqPanel").text(formatCurrency(data.trimestre));
			$("#hyPanel").text(formatCurrency(data.year));
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

function emitirFacturas() {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : facturaURL + 'emiteFacturas',
		data : JSON.stringify(rows_selected),
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

function descargaFactura(data) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', facturaURL + 'pdf/' + data[0], true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie('restTokenC'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var myBlob = this.response;
			var blob = new Blob([ myBlob ]);
			var link = document.createElement('a');
			var fileName = this.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
		}
		if (this.status == 401) {
			window.location.replace(serverURL + 'login.html');
		}
	};
	xhr.send();
}

function getZipFacturasMes() {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', facturaURL + 'pdf/mes/' + $("#mesesDropdown").val(), true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie('restTokenC'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var myBlob = this.response;
			var blob = new Blob([ myBlob ]);
			var link = document.createElement('a');
			var fileName = this.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
		}
		if (this.status == 401) {
			window.location.replace(serverURL + 'login.html');
		}
	};
	xhr.send();
}

function getZipFacturasTrimestre() {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', facturaURL + 'pdf/trimestre/' + $("#mesesDropdown").val(),
			true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie('restTokenC'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var myBlob = this.response;
			var blob = new Blob([ myBlob ]);
			var link = document.createElement('a');
			var fileName = this.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
		}
		if (this.status == 401) {
			window.location.replace(serverURL + 'login.html');
		}
	};
	xhr.send();
}

function getZipFacturasYear() {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', facturaURL + 'pdf/year/' + $("#yearDropdown").val(), true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + $.cookie('restTokenC'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var myBlob = this.response;
			var blob = new Blob([ myBlob ]);
			var link = document.createElement('a');
			var fileName = this.getResponseHeader('Content-Disposition');
			fileName = fileName.substring(fileName.lastIndexOf("=") + 1,
					fileName.length).trim();
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName;
			link.click();
		}
		if (this.status == 401) {
			window.location.replace(serverURL + 'login.html');
		}
	};
	xhr.send();
}
