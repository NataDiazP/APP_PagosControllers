/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos.PPal;

import co.edu.polijic.APP_Pagos.Controllers.TransaccionJpaController;
import co.edu.polijic.app_pagos.model.RegistroTransaccion;
import co.edu.polijic.app_pagos.model.Tarjeta;
import co.edu.polijic.app_pagos.model.TipoPago;
import co.edu.polijic.app_pagos.model.Transaccion;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author felipe
 */
public class PPal {

    public static void main(String[] args) {
        /**
         * *********************************************
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("APP_PagosPU");

        TransaccionJpaController transaccionJpaController = new TransaccionJpaController(emf);
        /*System.out.println("Transacciones por estado");
         for (Transaccion transaccion : transaccionJpaController.getTransaccionByEstado("ACEPTADA")) {
         System.out.println(transaccion);
         }*/

        System.out.println("Transacciones por numero de tarjeta origen");
        int nmtarjeta = 232323;
        for (Tarjeta tarjeta : transaccionJpaController.getTransaccionesTarjetaOrigen(nmtarjeta)) {
            System.out.println("Numero de cuenta --> " + tarjeta.getNmtarjeta());
            System.out.println("Titular de la cuenta -->" + tarjeta.getDsnombretitular());
            List<Transaccion> transacciones = tarjeta.getTransaccionList();
            
            for (Transaccion trans : transacciones) {
                System.out.println("*****Transacciones realizadas ********");
                System.out.println("Transaccion numero --> " + trans.getCdtransaccion());
                System.out.println("Cuenta origen --> " + trans.getCdtarjetaorigen());
                System.out.println("Cuenta destino --> " + trans.getCdtarjetadestino());
                System.out.println("Valor consignado --> " + trans.getVltransaccion() );
                System.out.println("Cuotas establecidas -->" + trans.getNmcuotaspago());
                System.out.println(trans.getCdtipopago().getDsdescripcion());
                List<RegistroTransaccion> registrotransaccion = trans.getRegistroTransaccionList();
                for(RegistroTransaccion regTransaccion : registrotransaccion){
                    System.out.println("Fecha transaccion --> " + regTransaccion.getFefechatransaccion());
                    System.out.println("Estado transaccion -->" + regTransaccion.getOpestado());
                }
                TipoPago tipopago = new TipoPago();
                tipopago = trans.getCdtipopago();
                System.out.println("Tipo de pago -->" + tipopago.getDsdescripcion());
                
            }
            
        }
        
            TransaccionJpaController transaccioJpaController = new TransaccionJpaController(emf);
        System.out.println("Transacciones por estado");
        for (Transaccion transaccion : transaccioJpaController.getTransaccionByEstado("Exitoso")) {
            System.out.println(transaccion.getCdtransaccion());
        }
        
    }
    


}
