var rootURL = 'https://dentool-elehin.rhcloud.com/service/tratamiento/';
var diagnosticoURL = 'https://dentool-elehin.rhcloud.com/service/diagnostico/';
var tratamientoURL = 'https://dentool-elehin.rhcloud.com/service/tratamiento/';
var tratamientosTopURL = 'https://dentool-elehin.rhcloud.com/service/tratamientoTop';
var serverURL = 'https://dentool-elehin.rhcloud.com/';
var pacienteURL = 'https://dentool-elehin.rhcloud.com/service/paciente/';
var pagosURL = 'https://dentool-elehin.rhcloud.com/service/pago/';
var authenticationURL = 'https://dentool-elehin.rhcloud.com/service/authentication/';
var presupuestoURL = 'https://dentool-elehin.rhcloud.com/service/presupuesto/';
var facturaURL = 'https://dentool-elehin.rhcloud.com/service/factura/';
var datosComercialesURL = 'https://dentool-elehin.rhcloud.com/service/datosComerciales/';
var reportIngresosURL = 'https://dentool-elehin.rhcloud.com/service/ingresosMes/';
var citaURL = 'https://dentool-elehin.rhcloud.com/service/cita/';

//var rootURL = 'http://localhost:8080/service/tratamiento/';
//var diagnosticoURL = 'http://localhost:8080/service/diagnostico/';
//var pacienteURL = 'http://localhost:8080/service/paciente/';
//var tratamientoURL = 'http://localhost:8080/service/tratamiento/';
//var tratamientosTopURL = 'http://localhost:8080/service/tratamientoTop';
//var serverURL = 'http://localhost:8080/';
//var pagosURL = 'http://localhost:8080/service/pago/';
//var authenticationURL = 'http://localhost:8080/service/authentication/';
//var presupuestoURL = 'http://localhost:8080/service/presupuesto/';
//var facturaURL = 'http://localhost:8080/service/factura/';
//var datosComercialesURL = 'http://localhost:8080/service/datosComerciales/';
//var reportIngresosURL = 'http://localhost:8080/service/ingresosMes/';
//var citaURL = 'http://localhost:8080/service/cita/';

$(document).ready(function() {
	$('.dropdown-toggle').dropdown();
	$('[data-toggle="tooltip"]').tooltip();
});

var getUrlParameter = function getUrlParameter(sParam) {
	var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL
			.split('&'), sParameterName, i;

	for (i = 0; i < sURLVariables.length; i++) {
		sParameterName = sURLVariables[i].split('=');

		if (sParameterName[0] === sParam) {
			return sParameterName[1] === undefined ? true : sParameterName[1];
		}
	}
};

function formatCurrency(value) {
	// European formatting (custom symbol and separators), can also use options
	// object as second parameter:
	return accounting.formatMoney(value, "€", 2, ".", ",", "%v %s");
}

function formatDate(date, formato) {
	if (date === undefined) {
		date = new Date();
	}

	var formattedDate;

	if (formato === undefined || formato == 'long') {
		formattedDate = date.getDate() + ' ' + meses[date.getMonth()] + ' '
				+ date.getFullYear();
	} else if (formato == 'short') {
		formattedDate = date.getDate() + '-' + (date.getMonth() + 1) + '-'
				+ date.getFullYear();
	} else if (formato == 'hora') {
		formattedDate = date.getHours() + ':'
				+ paddingLeft(date.getMinutes(), 2);
	} else if (formato == 'complete') {
		formattedDate = diasSemana[date.getDay()] + ' ' + date.getDate() + ' '
				+ meses[date.getMonth()] + ' ' + date.getFullYear();
	}

	return formattedDate;

}

function formatTelefono(telefono) {
	if (telefono.length < 9) {
		return telefono;
	} else if (telefono.startsWith('6') || telefono.startsWith('7')) {
		return telefono.replace(/(\d\d\d)(\d\d\d)(\d\d\d)/, "$1 $2 $3");
	} else if (telefono.startsWith('9')) {
		return telefono.replace(/(\d\d)(\d\d\d)(\d\d)(\d\d)/, "$1 $2 $3 $4");
	} else if (telefono.length == 12 && telefono.startsWith('+')) {
		return telefono.replace(/(\s\d\d)(\d\d\d)(\d\d\d)(\d\d\d)/,
				"$1 $2 $3 $4");
	} else if (telefono.length == 13 && telefono.startsWith('00')) {
		return telefono.replace(/(\d\d\d\d)(\d\d\d)(\d\d\d)(\d\d\d)/,
				"$1 $2 $3 $4");
	} else {
		return telefono;
	}
}

var meses_es = [ 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
		'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre' ];
var meses = meses_es;

var diasSemana_es = [ 'Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves',
		'Viernes', 'Sábado' ];
var diasSemana = diasSemana_es;

function paddingLeft(number, digits) {
	if (number.toString().length >= digits) {
		return number;

	} else {
		while (number.toString().length < digits) {
			number = '0' + number.toString();
		}
		return number;
	}
}

class TzDate extends Date {
	constructor(){
		super();
		this.setHours(this.getHours() + 6);
	}
	
	setFecha(fecha){
		var dia = fecha.substr(0, fecha.indexOf('-'));
		var mes = fecha.substr(fecha.indexOf('-') + 1,
				fecha.lastIndexOf('-') - fecha.indexOf('-')
						- 1);
		var year = fecha.substr(fecha.lastIndexOf('-') + 1,
				fecha.length);
		
		var difYears = year - this.getFullYear();
		this.setFullYear(this.getFullYear() + difYears);

		var difMeses = mes - (this.getMonth() + 1);
		this.setMonth(this.getMonth() + difMeses);

		var difDias = dia - this.getDate();
		this.setDate(this.getDate() + difDias);
	}
}

