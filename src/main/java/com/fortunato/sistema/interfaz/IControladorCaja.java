package com.fortunato.sistema.interfaz;

import com.fortunato.sistema.entidad.Caja;
import java.math.BigDecimal;

public interface IControladorCaja {
    void abrirCaja(Caja caja);
    void cerrarCaja(Caja caja);
    void añadirDineroCaja(Caja caja, BigDecimal dinero);
    void retirarDineroCaja(Caja caja, BigDecimal dinero);
    BigDecimal calcularTotalVentas(Caja caja);
    BigDecimal getDiferencia(Caja caja);
    Caja crearCaja(Integer numeroCaja, BigDecimal saldoInicial);
    Caja buscarCaja(Long id);
}
