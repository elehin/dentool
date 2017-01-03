package com.dentool.model;

import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.Pago;

public class PagoEager {
	private Pago pago;
	private Diagnostico diagnostico;

	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}

	public Diagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(Diagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}
}
