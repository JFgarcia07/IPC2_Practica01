/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.ipc2_practica01;

import com.mycompany.ipc2_practica01.BDconnection.BDconnection;

/**
 *
 * @author jgarcia07
 */
public class IPC2_Practica01 {

    public static void main(String[] args) {
        System.out.println("Practica 1 IPC2");
        BDconnection cn = new BDconnection();
        cn.connect();
        cn.registrarEvento("EVT-001","25/08/2025","CHARLA","Tecnología Sheikah","Auditorio Central",150);
        cn.registrarParticipante("Zelda Hyrule","ESTUDIANTE","Universidad de Hyrule","zelda@hyrule.edu");
        cn.inscripcion("zelda@hyrule.edu","EVT-001","ASISTENTE");
        cn.pago("zelda@hyrule.edu","EVT-001","EFECTIVO",50.00);
        cn.registrarActividad("ACT-001","EVT-001","CHARLA","Taller de pociones","zelda@hyrule.edu","10:00","12:00",30);
        cn.regristrarAsistencia ("zelda@hyrule.edu","ACT-001");
        cn.certificado("zelda@hyrule.edu","EVT-001");
    }
}
