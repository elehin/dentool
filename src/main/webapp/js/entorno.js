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

// var rootURL = 'http://localhost:8080/service/tratamiento/';
// var diagnosticoURL = 'http://localhost:8080/service/diagnostico/';
// var pacienteURL = 'http://localhost:8080/service/paciente/';
// var tratamientoURL = 'http://localhost:8080/service/tratamiento/';
// var tratamientosTopURL = 'http://localhost:8080/service/tratamientoTop';
// var serverURL = 'http://localhost:8080/';
// var pagosURL = 'http://localhost:8080/service/pago/';
// var authenticationURL = 'http://localhost:8080/service/authentication/';
// var presupuestoURL = 'http://localhost:8080/service/presupuesto/';
// var facturaURL = 'http://localhost:8080/service/factura/';
// var datosComercialesURL = 'http://localhost:8080/service/datosComerciales/';
// var reportIngresosURL = 'http://localhost:8080/service/ingresosMes/';

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

var meses_es = [ 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
		'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre' ];
var meses = meses_es;
