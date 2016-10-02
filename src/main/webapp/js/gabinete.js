var currentGabinete;
var plantilla;

$(document).ready(function() {

	if (getUrlParameter("gabinete") != '') {
		getGabinete(getUrlParameter("gabinete"));
	}

	$("#btnSave").click(function() {
		updateGabinete();
		return false;
	});

	getPlantilla();

});

function getGabinete(id) {
	$.ajax({
		type : 'GET',
		url : gabineteURL + id,
		success : function(data) {
			currentGabinete = data;
			renderDetails(currentGabinete);
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

function renderDetails(gabinete) {
	$('#gabineteId').val(gabinete.id);
	$('#especialidad').val(gabinete.especialidad);
	$('#nombre').val(gabinete.nombre);
	populateTablePersonal(gabinete);
}

function populateTablePersonal(dataset) {

	var trHTML = '';
	$('#tablePersonalBody').empty();

	var m1;
	if (dataset.lunesMorning === undefined || dataset.lunesMorning == null) {
		m1 = "Libre";
	} else {
		m1 = dataset.lunesMorning.nombre;
	}
	var m2;
	if (dataset.martesMorning === undefined || dataset.martesMorning == null) {
		m2 = "Libre";
	} else {
		m2 = dataset.martesMorning.nombre;
	}
	var m3;
	if (dataset.miercolesMorning === undefined
			|| dataset.miercolesMorning == null) {
		m3 = "Libre";
	} else {
		m3 = dataset.miercolesMorning.nombre;
	}
	var m4;
	if (dataset.juevesMorning === undefined || dataset.juevesMorning == null) {
		m4 = "Libre";
	} else {
		m4 = dataset.juevesMorning.nombre;
	}
	var m5;
	if (dataset.viernesMorning === undefined || dataset.viernesMorning == null) {
		m5 = "Libre";
	} else {
		m5 = dataset.viernesMorning.nombre;
	}
	var m6;
	if (dataset.sabadoMorning === undefined || dataset.sabadoMorning == null) {
		m6 = "Libre";
	} else {
		m6 = dataset.sabadoMorning.nombre;
	}
	var m7;
	if (dataset.domingoMorning === undefined || dataset.domingoMorning == null) {
		m7 = "Libre";
	} else {
		m7 = dataset.domingoMorning.nombre;
	}

	franja = 'Mañana';
	trHTML += '<tr><td>' + franja + '</td><td>' + dataset.id
			+ '</td><td id="1m">' + m1 + '</td><td id="2m">' + m2
			+ '</td><td id="3m">' + m3 + '</td><td id="4m">' + m4
			+ '</td><td id="5m">' + m5 + '</td><td id="6m">' + m6
			+ '</td><td id="7m">' + m7 + '</td></tr>';

	var t1;
	if (dataset.lunesTarde === undefined || dataset.lunesTarde == null) {
		t1 = "Libre";
	} else {
		t1 = dataset.lunesTarde.nombre;
	}
	var t2;
	if (dataset.martesTarde === undefined || dataset.martesTarde == null) {
		t2 = "Libre";
	} else {
		t2 = dataset.martesTarde.nombre;
	}
	var t3;
	if (dataset.miercolesTarde === undefined || dataset.miercolesTarde == null) {
		t3 = "Libre";
	} else {
		t3 = dataset.miercolesTarde.nombre;
	}
	var t4;
	if (dataset.juevesTarde === undefined || dataset.juevesTarde == null) {
		t4 = "Libre";
	} else {
		t4 = dataset.juevesTarde.nombre;
	}
	var t5;
	if (dataset.viernesTarde === undefined || dataset.viernesTarde == null) {
		t5 = "Libre";
	} else {
		t5 = dataset.viernesTarde.nombre;
	}
	var t6;
	if (dataset.sabadoTarde === undefined || dataset.sabadoTarde == null) {
		t6 = "Libre";
	} else {
		t6 = dataset.sabadoTarde.nombre;
	}
	var t7;
	if (dataset.domingoTarde === undefined || dataset.domingoTarde == null) {
		t7 = "Libre";
	} else {
		t7 = dataset.domingoTarde.nombre;
	}

	franja = 'Tarde';
	trHTML += '<tr><td>' + franja + '</td><td>' + dataset.id
			+ '</td><td id="1t">' + t1 + '</td><td id="2t">' + t2
			+ '</td><td id="3t">' + t3 + '</td><td id="4t">' + t4
			+ '</td><td id="5t">' + t5 + '</td><td id="6t">' + t6
			+ '</td><td id="7t">' + t7 + '</td></tr>';

	$("#tablePersonalBody").append(trHTML);

	searchTable = $('#tablePersonal')
			.DataTable(
					{
						"retrieve" : false,
						"paging" : false,
						"searching" : false,
						"info" : false,
						"ordering" : false,
						"columnDefs" : [ {
							"targets" : [ 1 ],
							"visible" : false
						}, {
							"className" : "hueco",
							"targets" : [ 2, 3, 4, 5, 6, 7, 8 ]
						}, {
							"className" : "franja",
							"targets" : [ 0 ]
						} ],
						// "order" : [ 3, "asc" ],
						"language" : {
							"search" : "Buscar:",
							"sLengthMenu" : "Mostrar _MENU_ registros",
							"sZeroRecords" : "No se encontraron gabinetes",
							"sEmptyTable" : "No hay información",
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

	// $('#tablePersonalBody').on('click', 'button', function() {
	// var data = searchTable.row($(this).parents('tr')).data();
	// url = serverURL + 'gabinete.html?gabinete=' + data[0];
	// window.location.replace(url);
	// });
}

function initDragNDrop() {
	// #########################################################################
	// #########################################################################
	// Inicialización de los objetos draggable para cambiar las horas de citas
	$(".draggable").draggable({
		// scope : "personal",
		// appendTo : $("#calendar tbody"),
		// containment : $("#calendar tbody"),
		delay : 200,
		revert : true,
		// grid : [ $(".hueco").width() + 5, 20.5 ],
		// stack : ".ocupado",
		// start : draggableStart,
		// scroll : true,
		// snap : true,
		helper : "clone",
	// opacity : 0.5
	});

	// #########################################################################
	// #########################################################################
	// Inicialización de los objetos droppable para aceptar los cambios de
	// personal
	$("td.hueco").droppable({
		// scope : "personal",
		accept : $(".draggable"),
		// tolerance : "pointer",
		drop : dropComplete,
		activeClass : "drop-active",
		hoverClass : "drop-hover"
	});
}

function updateGabinete() {

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : gabineteURL,
		data : formToJSON(),
		success : function(rdata, textStatus, jqXHR) {
			showSuccessMessage();
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

function formToJSON() {
	// if(currentGabinete.lunesMorning === undefined ||
	// currentGabinete.lunesMorning == null){
	// lunesMorning =
	// }
	return JSON.stringify({
		"id" : $("#gabineteId").val(),
		"nombre" : $('#nombre').val(),
		"especialidad" : $('#especialidad').val(),
		"lunesMorning" : currentGabinete.lunesMorning,
		"martesMorning" : currentGabinete.martesMorning,
		"miercolesMorning" : currentGabinete.miercolesMorning,
		"juevesMorning" : currentGabinete.juevesMorning,
		"viernesMorning" : currentGabinete.viernesMorning,
		"sabadoMorning" : currentGabinete.sabadoMorning,
		"domingoMorning" : currentGabinete.domingoMorning,
		"lunesTarde" : currentGabinete.lunesTarde,
		"martesTarde" : currentGabinete.martesTarde,
		"miercolesTarde" : currentGabinete.miercolesTarde,
		"juevesTarde" : currentGabinete.juevesTarde,
		"viernesTarde" : currentGabinete.viernesTarde,
		"sabadoTarde" : currentGabinete.sabadoTarde,
		"domingoTarde" : currentGabinete.domingoTarde,
	});
}

function showSuccessMessage() {
	$("#success-alert").alert();
	window.setTimeout(function() {
		$("#success-alert").fadeTo(1000, 500).slideUp(500, function() {
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

function getPlantilla() {
	$.ajax({
		type : 'GET',
		url : personalURL + 'list',
		success : function(data) {
			plantilla = data;
			populateTablePlantilla(plantilla);
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

function populateTablePlantilla(dataset) {
	$.each(dataset, function(i, item) {
		if (item.activo) {
			var html = '<div class="panel panel-default draggable" id="'
					+ item.id + '"><div class="panel-body">' + item.nombre
					+ '</div> </div>';
			$("#plantillaDiv").append(html);
		}
	});
	initDragNDrop();

}

function draggableStart() {
	return null;
}

function dropComplete(draggable, ui) {
	var personal = getPersonalFromPlantilla($(ui.draggable[0]).attr("id"));

	switch ($(this).attr("id")) {
	case "1m":
		currentGabinete.lunesMorning = personal;
		break;
	case "1t":
		currentGabinete.lunesTarde = personal;
		break;
	case "2m":
		currentGabinete.martesMorning = personal;
		break;
	case "2t":
		currentGabinete.martesTarde = personal;
		break;
	case "3m":
		currentGabinete.miercolesMorning = personal;
		break;
	case "3t":
		currentGabinete.miercolesTarde = personal;
		break;
	case "4m":
		currentGabinete.juevesMorning = personal;
		break;
	case "4t":
		currentGabinete.juevesTarde = personal;
		break;
	case "5m":
		currentGabinete.viernesMorning = personal;
		break;
	case "5t":
		currentGabinete.viernesTarde = personal;
		break;
	case "6m":
		currentGabinete.sabadoMorning = personal;
		break;
	case "6t":
		currentGabinete.sabadoTarde = personal;
		break;
	case "7m":
		currentGabinete.domingoMorning = personal;
		break;
	case "7t":
		currentGabinete.domingoTarde = personal;
		break;
	}

	updateGabinete();

	$(this).text(personal.nombre);
}

function getPersonalFromPlantilla(id) {
	for ( var personal in plantilla) {
		if (plantilla[personal].id == id) {
			return plantilla[personal];
		}
	}
	return null;
}
