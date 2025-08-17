
package com.mycompany.ipc2_practica01;

import com.mycompany.ipc2_practica01.BDconnection.BDconnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author jgarcia07
 */
public class archivoEntrada {
    
    private BDconnection con = new BDconnection();

    public archivoEntrada() {
        con.connect();
    }
    
    /**
     * METODO QUE LEE EL ARCHIVO DE TEXTO CON LAS INSTRUCCIONES PARA HACER LOS INSERTS
     * Y CADA LINEA LAS COLOCA EN UN ARRAYLIST PARA PODER ANALIZARLOS DE MEJOR MANERA
     * @param archivo 
     */
    public void leerArchivo(File archivo){
        ArrayList<String> lineas = new ArrayList<>();
        try (BufferedReader entrada = new BufferedReader(new FileReader(archivo))){
            String linea;
            while((linea = entrada.readLine()) != null){
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
        }
        
        interprete(lineas);
    }
    
    /**
     * METODO QUE LEE LINEA POR LINEA PARA SABER QUE TIPO DE INSTRUCCION HAY QUE REALIZAR 
     * ASI MISMO SEPARA EL TIPO DE INSTRUCCION DE LOS PARAMETROS DE LA INSTRUCCION
     * @param contenido 
     */
    private void interprete(ArrayList<String> contenido){
        String instruccion;
        String parametros;
        for(String linea: contenido){
            
            if(linea == null || linea.trim().isEmpty()){
                continue;
            }
            
            int inicio = linea.indexOf("(");
            int fin = linea.indexOf(")");
            
            
            if (inicio != -1 && fin != -1 && fin > inicio) {
                instruccion = linea.substring(0, inicio).trim();

                parametros = linea.substring(inicio + 1, fin).trim();

                tipoInsert(instruccion, parametros);
                
            } else {
                System.out.println("No se puede hacer esta instruccion");
            }
        }
    }
    
    /**
     * RECONOCE QUE TIPO DE INSERT HAY QUE HACER PARA SUBIR LA INFORMACION A LA BASE DE DATOS
     * Y MANDA A LLAMAR EL METODO QUE SE ENCARGA DE HACER EL INSERT DEPENDIENDO DE LA INSTRUCCION
     * @param tipo
     * @param parametros 
     */
    private void tipoInsert(String tipo, String parametros){
        switch(tipo){
            case "REGISTRO_EVENTO":
                insertEvento(separarParametros(parametros));
                break;
            case "REGISTRO_PARTICIPANTE":
                insertParticipante(separarParametros(parametros));
                break;
            case "INSCRIPCION":
                insertInscripcion(separarParametros(parametros));
                break;
            case "PAGO":
                insertPago(separarParametros(parametros));
                break;
            case "VALIDAR_INSCRIPCION":
                insertValidarInscrip(separarParametros(parametros));
                break;
            case "REGISTRO_ACTIVIDAD":
                insertActividad(separarParametros(parametros));
                break;
            case "ASISTENCIA":
                insertAsistencia(separarParametros(parametros));
                break;
            case "CERTIFICADO":
                insertCertificado(separarParametros(parametros));
                break;
            default:
                break;
        }
    }
    
    /**
     * METODO QUE SEPARA LOS PARAMETROS PARA PODER ANALIZARLOS UNO POR UNO
     * SI SE ENCUENTRA ALGUN PARAMETRO VACIO, HARA SE DEVUELVA UN VALOR NULL
     * @param parametros
     * @return parametrosList
     */
    private String[] separarParametros(String parametros) {
        String[] parametrosList = parametros.split(",");

        for (int i = 0; i < parametrosList.length; i++) {
            String parametro = parametrosList[i].trim();

            if (parametro.equals("\"\"")) {
                return null;
            } else if (parametro.startsWith("\"") && parametro.endsWith("\"")) {
                parametrosList[i] = parametro.substring(1, parametro.length() - 1);
                if (parametrosList[i].isEmpty()) {
                    return null;
                }
            } else if (parametro.isEmpty()) {
                return null;
            }
        }

        return parametrosList;
    }

    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA EL EVENTO
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertEvento(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los parametros");
            return;
        }
        
        String codEvento = parametros[0];
        String fecha = parametros[1];
        String tipoEvento = parametros[2];
        String tituloEvento = parametros[3];
        String ubicacion = parametros[4];
        int cupoMax = Integer.parseInt(parametros[5]);
        double costo = Double.parseDouble(parametros[6]);
        
        
        
        con.registrarEvento(codEvento, fecha, tipoEvento, tituloEvento, ubicacion, cupoMax, costo);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA EL PARTICIPANTE
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertParticipante(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String nombre = parametros[0];
        String tipoParticipante = parametros[1];
        String institucion = parametros[2];
        String email = parametros[3];
        
        con.registrarParticipante(nombre, tipoParticipante, institucion, email);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA LA INSCRIPCION
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertInscripcion(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String email = parametros[0];
        String codEvento = parametros[1];
        String tipoInscripcion = parametros[2];
        
        con.inscripcion(email, codEvento, tipoInscripcion);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA EL PAGO
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertPago(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String email = parametros[0];
        String codEvento = parametros[1];
        String metodoPago = parametros[2];
        double monto = Double.parseDouble(parametros[3]);
        
        con.pago(email, codEvento, metodoPago, monto);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA LA VALIDACION
     * DE LA INSCRIPCION SI LA LISTA DE PARAMETROS ES NULL, ESTO 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertValidarInscrip(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String email = parametros[0];
        String codEvento = parametros[1];
        
        con.validarInscripcion(email, codEvento);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA LA ACTIVIDAD
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertActividad(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String codActividad = parametros[0];
        String codEvento = parametros[1];
        String tipoActividad = parametros[2];
        String titulo = parametros[3];
        String email = parametros[4];
        String horaInicio = parametros[5];
        String horaFin = parametros[6];
        int cupoMax = Integer.parseInt(parametros[7]);
        
        con.registrarActividad(codActividad, codEvento, tipoActividad, titulo, email, horaInicio, horaFin, cupoMax);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA LA ASISTENCIA
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertAsistencia(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String email = parametros[0];
        String codActividad = parametros[1];
        
        con.regristrarAsistencia(email, codActividad);
    }
    
    /**
     * METODO QUE SE ENCARGA DE HACER EL INSERT PARA EL EVENTO
     * SI LA LISTA DE PARAMETROS ES NULL, ESTO QUIERE DECIR QUE 
     * LOS PARAMETROS ESTAN INCOMPLETOS ENTONCES SE OMITARA
     * @param parametros 
     */
    private void insertCertificado(String[] parametros){
        if(parametros == null){
            System.out.println("No tiene todos los campos");
            return;
        }
        
        String email = parametros[0];
        String codEvento = parametros[1];
        
        con.certificado(email, codEvento);
    }
            
}

