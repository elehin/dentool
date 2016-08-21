package com.dentool.model;

import java.util.Calendar;
import java.util.List;

public class MiniCalendario {
	private Calendar fecha;
	private List<Short> diasCita;
	private short primerDiaMes;
	private short ultimoDiaMes;

	public short getPrimerDiaMes() {
		return primerDiaMes;
	}

	public void setPrimerDiaMes(short primerDiaMes) {
		this.primerDiaMes = primerDiaMes;
	}

	public short getUltimoDiaMes() {
		return ultimoDiaMes;
	}

	public void setUltimoDiaMes(short ultimoDiaMes) {
		this.ultimoDiaMes = ultimoDiaMes;
	}

	public Calendar getFecha() {
		return fecha;
	}

	public void setFecha(Calendar fecha) {
		this.fecha = fecha;
	}

	public List<Short> getDiasCita() {
		return diasCita;
	}

	public void setDiasCita(List<Short> diasCita) {
		this.diasCita = diasCita;
	}
}
