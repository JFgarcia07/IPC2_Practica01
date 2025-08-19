/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author jgarcia07
 */
public class crearHTML {

    /**
     * METODO QUE HACE EL CERTIFICADO DEL PARTICIPANTE EN FORMATO HTML
     * @param ruta
     * @param nombreDelArchivo
     * @param nombre
     * @param nombreEvento 
     */
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
    
    public void reporte(String ruta, String nombreDelArchivo, String tipoReporte,ResultSet rs){
        File archivo = new File(ruta, nombreDelArchivo);
        try (BufferedWriter salida = new BufferedWriter(new FileWriter(archivo))){
            salida.write("<html>\n");
            salida.write("<head>\n");
            salida.write("<title>"+tipoReporte+"</title>\n");
            salida.write("<style>\n");
            salida.write("table {border-collapse: collapse; width: 100%;}\n");
            salida.write("th, td {border: 1px solid black; padding: 8px; text-align: left;}\n");
            salida.write("</style>\n");
            salida.write("</head>\n");
            salida.write("<body>\n");

            salida.write("<h1 style='text-align:center;'>TriForce Events</h1>\n");
            salida.write("<h2 style='text-align:center;'>"+tipoReporte+"</h2>\n");
            salida.write("<table>\n");

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnas = rsmd.getColumnCount();

            salida.write("<tr>");
            for (int i = 1; i <= columnas; i++) {
                salida.write("<th>" + rsmd.getColumnName(i) + "</th>");
            }
            salida.write("</tr>\n");

            while (rs.next()) {
                salida.write("<tr>");
                for (int i = 1; i <= columnas; i++) {
                    Object valor = rs.getObject(i);
                    if(valor != null){
                        salida.write("<td>"+valor.toString()+"</td>");
                    } else {
                        salida.write("<td></td>");
                    }
                }
                salida.write("</tr>\n");
            }

            salida.write("</table>\n");
            salida.write("</body>\n");
            salida.write("</html>\n");

        } catch (Exception e) {
            System.out.println("HA PASADO UN ERROR INESPERADO");
        }
    }
    
    

}
