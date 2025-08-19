/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01.BDconnection;

import com.mycompany.ipc2_practica01.crearHTML;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author jgarcia07
 */
public class BDconnection {

    private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "TriForceEvents";
    private static final String USER_NAME = "adminDBA";
    private static final String PASSWORD = "admin@123";

    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA;

    private Connection connection;

    private crearHTML html = new crearHTML();
    private String ruta = "/home/jgarcia07/NetBeansProjects/IPC2_Practica01/Reportes";
   
    /**
     * METODO PARA CONECTARSE A LA BASE DE DATOS
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            System.out.println("CONEXION EXITOSA A LA BASE DE DATOS");
        } catch (Exception e) {
            System.out.println("ERROR AL CONECTARSE");
            e.printStackTrace();
        }
    }

    /**
     * FUNCION PARA MENSAJE DEL INSERT
     * @param rows 
     */
    private int mensajeQuery(int rows) {
        if (rows > 0) {
            System.out.println("Datos ingresador correctamente");
            return 0;
        } else {
            System.out.println("No sean podido ingresar los datos");
            return -1;
        }
    }

    /**
     * FUNCION QUE BUSCARA UN CODIGO EN UNA TABLA DE LA BASE DE DATOS PARA VERIFICAR SI 
     * EXISTE, YA SEA PARA VALIDAR QUE SE PUEDA HACER EL INSERT O BIEN QUE EL INSERT NO 
     * SEA REPETIDO
     * @param codigo
     * @param tabla
     * @param tipoCodigo
     * @return 
     */
    private boolean buscarCodigo(String codigo, String tabla, String tipoCodigo) {
        boolean existeElCodigo = false;
        String sqlCodigo = "SELECT COUNT(*) FROM " + tabla + " WHERE " + tipoCodigo + " = ?";
        try (PreparedStatement psCodigo = connection.prepareStatement(sqlCodigo)) {
            psCodigo.setString(1, codigo);
            ResultSet rs = psCodigo.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                existeElCodigo = true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inesperado");
        }

        return existeElCodigo;
    }
    
    /**
     * FUNCION  QUE BUSCARA UN EMAIL EN UNA TABLA DE LA BASE DE DATOS PARA VERIFICAR SI 
     * EXISTE, YA SEA PARA VALIDAR QUE SE PUEDA HACER EL INSERT O BIEN QUE EL INSERT NO 
     * SEA REPETIDO
     * @param email
     * @param tabla
     * @return 
     */
    private boolean buscarEmail(String email, String tabla){
        boolean existeElEmail = false;
        String sqlEmail = "SELECT COUNT(*) FROM "+ tabla + " WHERE correo_electronico = ?";
        try (PreparedStatement psEmail = connection.prepareStatement(sqlEmail)){
            psEmail.setString(1,email);
            ResultSet rs = psEmail.executeQuery();
            rs.next();
            if(rs.getInt(1) > 0){
               
                existeElEmail = true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inseperado");
        }
        return existeElEmail;
    }
    
    /**
     * FUNCION QUE BUSCARA UN CODIGO Y UN EMAIL EN UNA TABLA DE LA BASE DE DATOS PARA VERIFICAR SI 
     * EXISTE, YA SEA PARA VALIDAR QUE SE PUEDA HACER EL INSERT O BIEN QUE EL INSERT NO 
     * SEA REPETIDO
     * @param email
     * @param codEvento
     * @param tabla
     * @return 
     */
    private boolean validarCorreoYcodEvento(String email, String codEvento, String tabla){
        boolean existeRegistro = false;
        String sqlValidacion = "SELECT codigo_evento, correo_electronico FROM "+tabla+" WHERE codigo_evento = ? AND correo_electronico = ?";
        try (PreparedStatement psValidar = connection.prepareStatement(sqlValidacion)){
            psValidar.setString(1, codEvento);
            psValidar.setString(2, email);
            ResultSet rs = psValidar.executeQuery();
            if(rs.next()){
                existeRegistro = true;
            } 
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
        
        return existeRegistro;
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA EL EVENTO, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param codEvento
     * @param fecha
     * @param tipoEvento
     * @param tituloEvento
     * @param ubicacion
     * @param cupoMax
     * @param costo 
     */
    public int registrarEvento(String codEvento, String fecha, String tipoEvento, String tituloEvento, String ubicacion, int cupoMax, double costo) {
        //HAY QUE VERIFICAR SI EL CODIGO DE EVENTO YA EXISTE
        if (buscarCodigo(codEvento, "evento", "codigo_evento") == true) {
            return 1;
        }

        String sql = "INSERT INTO evento (codigo_evento, fecha_evento, tipo_evento, titulo_evento, ubicacion, cupo_maximo, costo_inscripcionn) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            //PASAR DE D/M/A -> A/M/D
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dateUtil = date.parse(fecha);
            java.sql.Date dateSQL = new java.sql.Date(dateUtil.getTime());

            ps.setString(1, codEvento);
            ps.setDate(2, dateSQL);
            ps.setString(3, tipoEvento);
            ps.setString(4, tituloEvento);
            ps.setString(5, ubicacion);
            ps.setInt(6, cupoMax);
            ps.setDouble(7, costo);

            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Formato invalido de hora");
            ex.printStackTrace();
            return -1;
        } 
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA EL PARTICIPANTE, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param nombre
     * @param tipoParticipante
     * @param institucion
     * @param email 
     */
    public int registrarParticipante(String nombre, String tipoParticipante, String institucion, String email) {
        //HAY QUE VALIDAR SI YA EXISTE EL USUARIO
        if(buscarEmail(email, "participante") == true){
            return 1; 
        }
        
        String sql = "INSERT INTO participante (nombre_participante, rol_participante, correo_electronico, institucion_procedencia) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipoParticipante);
            ps.setString(3, email);
            ps.setString(4, institucion);

            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA LA INSCRIPCION, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param email
     * @param codEvento
     * @param tipoInscripcion 
     */
    public int inscripcion(String email, String codEvento, String tipoInscripcion) {
        
        if(buscarCodigo(codEvento, "evento", "codigo_evento") == false){
            return 1;
        } else if(buscarEmail(email, "participante") == false){
            return 2;
        } else if(validarCorreoYcodEvento(email, codEvento, "inscripcion") == true){
            return 3;
        }
        
        String sql = "INSERT INTO inscripcion (codigo_evento, correo_electronico, tipo_inscripcion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);
            ps.setString(3, tipoInscripcion);

            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * METODO QUE REALIZA EL INSERT PARA EL PAGO, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param email
     * @param codEvento
     * @param metodoPago
     * @param monto 
     */
    public int pago(String email, String codEvento, String metodoPago, double monto) {
        double montoBD = 0;
        
        if(buscarEmail(email , "participante") == false){
            return 1;
        } else if(buscarCodigo(codEvento, "evento", "codigo_evento") == false){
            return 2;
        } else if(validarCorreoYcodEvento(email, codEvento, "pago") == true){
            return 3;
        }
        
        String sqlValidacionMonto = "SELECT costo_inscripcionn FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement psMonto = connection.prepareStatement(sqlValidacionMonto)){
            psMonto.setString(1, codEvento);
            ResultSet rs = psMonto.executeQuery();
            if(rs.next()){
                montoBD = rs.getDouble("costo_inscripcionn");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inesperado al buscar el codigo de evento");
            return -1;
        }
        
        if(montoBD == monto){
            String sql = "INSERT INTO pago (codigo_evento, correo_electronico, metodo_pago, monto) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, codEvento);
                ps.setString(2, email);
                ps.setString(3, metodoPago);
                ps.setDouble(4, monto);

                int rowsAffected = ps.executeUpdate();
                return mensajeQuery(rowsAffected);
            } catch (SQLException e) {
                System.out.println("Ha ocurrido un error inseperado");
                e.printStackTrace();
                return -1;
            }
        } else {
            JOptionPane.showMessageDialog(null, "El monto a pagar no es lo que equivale el costo del evento");
            return -1;
        } 
    }

    private boolean validarCupo(String codigo){
        int cupoMax = 0, registros = 0;
        String sql = "SELECT cupo_maximo FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    cupoMax = rs.getInt("cupo_maximo");
                }
            } 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inesperado");
        }
        
        String sql2 = "SELECT COUNT(*) FROM validarInscripcion";
        try (PreparedStatement ps2 = connection.prepareStatement(sql2)){
            try (ResultSet rs2 = ps2.executeQuery()){
                if(rs2.next()){
                    registros = rs2.getInt(1);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inesperado");
        }
        
        if(cupoMax < registros){
            return true;
        } else {
            return false;
        }
    }
    /**
     * METODO QUE REALIZA EL INSERT PARA LA VALIDACION DE LA INSCRIPCION, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param email
     * @param codEvento 
     * @return RESPUETA DEL INSERT
     */
    public int validarInscripcion(String email, String codEvento) {
        if(buscarEmail(email, "inscripcion") == false){
            return 1;
        } else if (buscarCodigo(codEvento, "evento", "codigo_evento") == false) {
            return 2;
        } else if (validarCupo(codEvento) == false){
            return 3;
        } else if (validarCorreoYcodEvento(email, codEvento, "validarInscripcion")){
            return 4;
        }
        
        String sql = "INSERT INTO validarInscripcion (correo_electronico, codigo_evento) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, email);
            ps.setString(2, codEvento);
            
            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inesperado");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA LA ACTIVIDAD, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param codActividad
     * @param codEvento
     * @param tipoActividad
     * @param titulo
     * @param email
     * @param horaInicio
     * @param horaFin
     * @param cupoMax 
     */
    public int registrarActividad(String codActividad, String codEvento, String tipoActividad, String titulo, String email, String horaInicio, String horaFin, int cupoMax) {
        
        if(buscarCodigo(codActividad, "actividad", "codigo_actividad") == true){
            return 1;
        } else if (buscarCodigo(codEvento, "evento", "codigo_evento") == false){ 
            return 2;
        } else if (buscarEmail(email, "inscripcion") == false){
            return 3;
        }
        
        String sqlSelect = "SELECT tipo_inscripcion FROM inscripcion WHERE correo_electronico = ?";   
        try (PreparedStatement ps = connection.prepareStatement(sqlSelect)){
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String rol = rs.getString("tipo_inscripcion");
                    if("ASISTENTE".equals(rol)){
                        return 4;
                    }
                } else {
                    System.out.println("NO SE ENCONTRO EL USUARIO");
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String sql = "INSERT INTO actividad (codigo_actividad, codigo_evento, correo_electronico, tipo_actividad, titulo_actividad, hora_inicio, hora_fin, cupo_maximo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            //PASAR DE STRING A DATE
            SimpleDateFormat hInicio = new SimpleDateFormat("HH:mm");
            SimpleDateFormat hFin = new SimpleDateFormat("HH:mm");

            java.util.Date hInicioUtil = hInicio.parse(horaInicio);
            java.util.Date hFinUtil = hFin.parse(horaFin);

            java.sql.Time horaInicioSQL = new java.sql.Time(hInicioUtil.getTime());
            java.sql.Time horaFinSQL = new java.sql.Time(hFinUtil.getTime());

            ps.setString(1, codActividad);
            ps.setString(2, codEvento);
            ps.setString(3, email);
            ps.setString(4, tipoActividad);
            ps.setString(5, titulo);
            ps.setTime(6, horaInicioSQL);
            ps.setTime(7, horaFinSQL);
            ps.setInt(8, cupoMax);

            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        } catch (ParseException ex) {
            System.out.println("Formato invalido de hora");
            ex.printStackTrace();
            return -1;
        } 
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA LA ASISTENCIA, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * @param email
     * @param codActividad 
     */
    public int regristrarAsistencia(String email, String codActividad) {
        if(buscarEmail(email,"participante") == false){
           
            return 1;
        } else if (buscarCodigo(codActividad, "actividad", "codigo_actividad") == false){
            
            return 2;
        }
        
        String sqlRol = "SELECT correo_electronico FROM actividad WHERE correo_electronico = ?";
        try (PreparedStatement psRol = connection.prepareStatement(sqlRol)){
            psRol.setString(1,email);
            try (ResultSet rs = psRol.executeQuery()){
                if(rs.next()){
                    return 3;
                }
            } catch (Exception e) {
                System.out.println("HA OCURRIDO UN ERROR INESPERADO");
            }
        } catch (SQLException ex) {
            System.out.println("HA OCURRIDO UN ERROR INESPERADO");
        }
        
        
        String sql = "INSERT INTO asistencia (codigo_actividad, correo_electronico) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codActividad);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * FUNCION QUE BUSCARA EL NOMBRE GRACIAS AL EMAIL, PARA COLOCAR EL NOMBRE DEL PARTICIPANTE
     * EN EL CERTIFICADO
     * @param email
     * @return 
     */
    private String nombreParticipante(String email){
        String nombre = "";
        String sql = "SELECT nombre_participante FROM participante WHERE correo_electronico = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,email);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    nombre = rs.getString("nombre_participante");
                }
            } 
        } catch (SQLException e) {
            System.out.println("HA OCURRIDO UN ERROR INESPERADO");
        }
        return nombre;
    }
    
    /**
     * FUNCION QUE BUSCARA EL NOMBRE DEL EVENTO GRACIAS AL CODIGO DEL MISMO, PARA COLOCAR EL NOMBRE DEL
     * EVENTO EN EL CERTIFICADO
     * @param codEvento
     * @return 
     */
    private String nombreEvento(String codEvento){
        String nombreEvento = null;
        String sql = "SELECT titulo_evento FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,codEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    nombreEvento = rs.getString("titulo_evento");
                }
            } 
        } catch (SQLException e) {
            System.out.println("HA OCURRIDO UN ERROR INESPERADO");
        }
        return nombreEvento;
    }
    
    /**
     * METODO QUE REALIZA EL INSERT PARA EL CERTIFICADO, CON SUS RESPECTIVAS VALIDACIONES 
     * PARA EVITAR EXCEPCIONES Y SE UTILIZA EL PreparedStatement PARA EVITAR EL SQL
     * INYECTION
     * 
     * ASI MISMO CREA UN ARCHIVO HTML DEL CERTIFICADO EN LA CARPETA DE REPORTES
     * @param email
     * @param codEvento 
     */
    public int certificado(String email, String codEvento) {
        if(buscarEmail(email,"asistencia") == false){
            return 1;
        } else if (buscarCodigo(codEvento, "evento", "codigo_evento") == false) {
            return 2;
        } else if (buscarEmail(email, "certificado") || buscarCodigo(codEvento, "certificado", "codigo_evento")) {
            return 3;
        }
        
        String sql = "INSERT INTO certificado (codigo_evento, correo_electronico) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);

            String participante = nombreParticipante(email);
            String nombreEvento = nombreEvento(codEvento);

           
            String nombreCertificado =  "Certificado: " + participante + ".html";
            
            html.emitirCertificado(ruta, nombreCertificado, participante, nombreEvento);
            
            int rowsAffected = ps.executeUpdate();
            return mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
            return -1;
        }
    }
    
    public int reporteParticipantes(){
        String sql = "SELECT * FROM participante";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            
            String nombreArchivo =  "Reporte De Participantes.html";
            
            html.reporte(ruta, nombreArchivo, "Reporte de Participante",rs);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int reporteActividades(){
        String sql = "SELECT * FROM actividad";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            
            String nombreArchivo =  "Reporte De Actividades.html";
            
            html.reporte(ruta, nombreArchivo, "Reporte de Actividades",rs);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int reporteEventos(){
        String sql = "SELECT * FROM evento";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            
            String nombreArchivo =  "Reporte De Eventos.html";
            
            html.reporte(ruta, nombreArchivo, "Reporte de Eventos",rs);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
