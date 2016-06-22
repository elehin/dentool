
// ################### document.ready() ##################################
$(document).ready(function() {

	getUsuarios();

	$("#btnSearch").click(function() {
		buscarTratamiento();
		return false;
	});

	$('#searchKey').keypress(function(e) {
		if (e.which == '13') {
			buscarPaciente();
		}
	});

});

// ###################### Funciones #######################################
function populateTable(dataset) {
	var trHTML = '';
	var lupa = '<button class="btn btn-info padding-0-4" role="button"><span class="glyphicon glyphicon-search"></span></button>';
	$('#tableUsuariosBody').empty();

	$.each(dataset, function(i, item) {
		trHTML += '<tr><td>' + item.id + '</td><td>' + lupa + '</td><td>'
				+ item.username + '</td><td>' + item.nombre + '</td><td>'
				+ item.apellidos + '</td></tr>';
	});
	$("#tableUsuariosBody").append(trHTML);

	searchTable = $('#tableUsuarios').DataTable({
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
			"search" : "Buscar:"
		}
	});

	$('#tableUsuariosBody tbody').on('click', 'button', function() {
		var data = searchTable.row($(this).parents('tr')).data();
		url = serverURL + 'usuario.html?usuario=' + data[0];
		window.location.replace(url);
	});
}

function buscarTratamiento() {
	key = $("#searchKey").val();

	var url = serverURL + 'usuarioMultiple.html?key=' + key;
	window.location.replace(url);
}

function getUsuarios() {
	$.ajax({
		type : 'GET',
		url : authenticationURL + 'lastUsers',
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