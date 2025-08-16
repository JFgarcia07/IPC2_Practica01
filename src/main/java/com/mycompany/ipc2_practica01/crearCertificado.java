/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jgarcia07
 */
public class crearCertificado {

    public void emitirCertificado(String ruta, String nombreDelArchivo, String nombre, String nombreEvento) {
        File archivo = new File(ruta, nombreDelArchivo);

        try (BufferedWriter salida = new BufferedWriter(new FileWriter(archivo))) {
            salida.write("<html>\n");
            salida.write("<head>\n");
            salida.write("<title>CERTIFICADO</title>\n");
            salida.write("</head>\n");
            salida.write("<body>\n");

            salida.write("<h1 style='text-align:center; font-size:64px;'>TriForce Events</h1>\n");
            salida.write("<h1 style='text-align:center; font-size:48px;'>CERTIFICADO</h1>\n");
            salida.write("<p style='text-align:center; font-size:24px;'>Se certifica que el Sr:</p>\n");
            salida.write("<h2 style='text-align:center; font-size:36px;'>" + nombre + "</h2>\n");
            salida.write("<p style='text-align:center; font-size:24px;'>Ha completado satisfactoriamente la actividad/evento:</p>\n");
            salida.write("<h3 style='text-align:center; font-size:36px;'>" + nombreEvento + "</h3>\n");

            salida.write("<p style='text-align:center;'>___________________________</p>");
            salida.write("<p style='text-align:center; font-size:36px;'>Firma</p>");

            salida.write("</body>\n");
            salida.write("</html>\n");
        } catch (IOException e) {
            System.out.println("HA OCURRIDO UN ERROR AL CREAR CERTIFICADO");
        }
    }

}
