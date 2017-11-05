// ------ URLs para conexión en local  --------------------- 
//var rootURL = 'https://localhost/service/tratamiento/';
//var diagnosticoURL = 'https://localhost/service/diagnostico/';
//var pacienteURL = 'https://localhost/service/paciente/';
//var tratamientoURL = 'https://localhost/service/tratamiento/';
//var tratamientosTopURL = 'https://localhost/service/tratamientoTop';
//var serverURL = 'https://localhost/';
//var pagosURL = 'https://localhost/service/pago/';
//var authenticationURL = 'https://localhost/service/authentication/';
//var presupuestoURL = 'https://localhost/service/presupuesto/';
//var facturaURL = 'https://localhost/service/factura/';
//var datosComercialesURL = 'https://localhost/service/datosComerciales/';
//var reportIngresosURL = 'https://localhost/service/ingresosMes/';
//var citaURL = 'https://localhost/service/cita/';
//var personalURL = 'https://localhost/service/personal/';
//var gabineteURL = 'https://localhost/service/gabinete/';
//var diaURL = 'https://localhost/service/dia/';

// ------ URLs para conexión en GCP ---------------------
 var rootURL = 'https://dentool.elehin.com/service/tratamiento/';
 var diagnosticoURL = 'https://dentool.elehin.com/service/diagnostico/';
 var pacienteURL = 'https://dentool.elehin.com/service/paciente/';
 var tratamientoURL = 'https://dentool.elehin.com/service/tratamiento/';
 var tratamientosTopURL = 'https://dentool.elehin.com/service/tratamientoTop';
 var serverURL = 'https://dentool.elehin.com/';
 var pagosURL = 'https://dentool.elehin.com/service/pago/';
 var authenticationURL = 'https://dentool.elehin.com/service/authentication/';
 var presupuestoURL = 'https://dentool.elehin.com/service/presupuesto/';
 var facturaURL = 'https://dentool.elehin.com/service/factura/';
 var datosComercialesURL =
 'https://dentool.elehin.com/service/datosComerciales/';
 var reportIngresosURL = 'https://dentool.elehin.com/service/ingresosMes/';
 var citaURL = 'https://dentool.elehin.com/service/cita/';
 var personalURL = 'https://dentool.elehin.com/service/personal/';
 var gabineteURL = 'https://dentool.elehin.com/service/gabinete/';
 var diaURL = 'https://dentool.elehin.com/service/dia/';
// 
 var uiBlocked, initConnCheck;

$(document).ready(function() {
	$('.dropdown-toggle').dropdown();
	$('[data-toggle="tooltip"]').tooltip();
	
	uiBlocked = false;

	initConnCheck = window.setInterval(checkConnectivity, 1000);
	
	window.setInterval(checkConnectivity, 60000);
	
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

function formatPorcentaje(value) {
	return accounting.formatMoney(value, "%", 2, ".", ",", "%v %s");
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
// this.horasDif = 1;
		this.horasDif = 0;
		this.setHours(this.getHours() + this.horasDif);
	}
	
	setFecha(fecha){
		var fechaActual = new Date();
		
		this.setHours(fechaActual.getHours());
		this.setMinutes(fechaActual.getMinutes());
		this.setSeconds(fechaActual.getSeconds());
		this.setMilliseconds(fechaActual.getMilliseconds());
		
		var year = fecha.substr(fecha.lastIndexOf('-') + 1,
				fecha.length);
		
		var mes = fecha.substr(fecha.indexOf('-') + 1,
				fecha.lastIndexOf('-') - fecha.indexOf('-')
						- 1);
		mes--;
		var dia = fecha.substr(0, fecha.indexOf('-'));
		
		// para evitar que los días 31 cambien el mes
		this.setDate(1);
		
		this.setFullYear(year);
		this.setMonth(mes);
		this.setDate(dia);
		
		this.setHours(this.getHours() + this.horasDif);
	}
}


function checkConnectivity(){
	
	    $.ajax({
	      cache: false,
	      type: 'GET',
	      url: rootURL + 'ping',
	      timeout: 5000,
	      success: function(data, textStatus, XMLHttpRequest) {
	        if (data == 'Up & running') {
	          
	          if (uiBlocked == true) {
	            uiBlocked = false;
	            $.unblockUI();
	          }
	          console.log("Comprobada conexión a servidor OK.");
	          clearInterval(initConnCheck);
	        }
	      },
	      error: function(jqXHR, textStatus, errorThrown){
	    	  if (uiBlocked == false) {
		            uiBlocked = true;
		            $.blockUI({
		              message: "No puedo conectarme con el servidor, espera unos instantes...",
		              css: {
		                border: 'none',
		                padding: '15px',
		                backgroundColor: '#000',
		                '-webkit-border-radius': '10px',
		                '-moz-border-radius': '10px',
		                opacity: .5,
		                color: '#fff'
		              } });
		          }
		        }
	    })

	  
}

