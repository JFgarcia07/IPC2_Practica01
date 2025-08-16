/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01.BDconnection;

import com.mycompany.ipc2_practica01.crearCertificado;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jgarcia07
 */
public class BDconnection {

    private static final String IP = "LocalHost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "TriForceEvents";
    private static final String USER_NAME = "adminDBA";
    private static final String PASSWORD = "admin@123";

    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA;

    private Connection connection;

    private List<String> certificados = new ArrayList<>();
    
    public void connect() {
        try {

            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            System.out.println("CONEXION EXITOSA A LA BASE DE DATOS");

        } catch (Exception e) {
            //Manejamos la excepcion
            System.out.println("ERROR AL CONECTARSE");
            e.printStackTrace();
        }
    }

    private void mensajeQuery(int rows) {
        if (rows > 0) {
            System.out.println("Datos ingresador correctamente");
            JOptionPane.showMessageDialog(null, "Datos ingresador correctamente");
        } else {
            System.out.println("No sean podido ingresar los datos");
            JOptionPane.showMessageDialog(null, "No sean podido ingresar los datos");
        }
    }

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
    
    public void registrarEvento(String codEvento, String fecha, String tipoEvento, String tituloEvento, String ubicacion, int cupoMax, double costo) {
        //HAY QUE VERIFICAR SI EL CODIGO DE EVENTO YA EXISTE
        if (buscarCodigo(codEvento, "evento", "codigo_evento") == true) {
            JOptionPane.showMessageDialog(null, "El codigo el evento ya existe en el registro");
            return;
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
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inseperado");
            e.printStackTrace();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Formato invalido de hora");
            ex.printStackTrace();
        } 
    }
    
    public void registrarParticipante(String nombre, String tipoParticipante, String institucion, String email) {
        //HAY QUE VALIDAR SI YA EXISTE EL USUARIO
        if(buscarEmail(email, "participante") == true){
            JOptionPane.showMessageDialog(null, "El correo electronico ya existe en el registro");
            return;
        }
        
        String sql = "INSERT INTO participante (nombre_participante, rol_participante, correo_electronico, institucion_procedencia) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipoParticipante);
            ps.setString(3, email);
            ps.setString(4, institucion);

            int rowsAffected = ps.executeUpdate();
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }
    
    public void inscripcion(String email, String codEvento, String tipoInscripcion) {
        
        if(buscarCodigo(codEvento, "evento", "codigo_evento") == false){
            JOptionPane.showMessageDialog(null, "El codigo del Evento no existe en el registro");
            return;
        } else if(buscarEmail(email, "participante") == false){
            JOptionPane.showMessageDialog(null, "El correo electronico del participante no existe en el registro");
            return;
        } else if(validarCorreoYcodEvento(email, codEvento, "inscripcion") == true){
            JOptionPane.showMessageDialog(null, "El correo electronico: " + email + " ya ha sido inscrito al evento con codigo: " + codEvento);
            return;
        }
        
        String sql = "INSERT INTO inscripcion (codigo_evento, correo_electronico, tipo_inscripcion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);
            ps.setString(3, tipoInscripcion);

            int rowsAffected = ps.executeUpdate();
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }

    public void pago(String email, String codEvento, String metodoPago, double monto) {
        double montoBD = 0;
        
        if(buscarEmail(email , "participante") == false){
            JOptionPane.showMessageDialog(null, "El correo electronico no se encuentra en el registro");
            return;
        } else if(buscarCodigo(codEvento, "evento", "codigo_evento") == false){
            JOptionPane.showMessageDialog(null, "El codigo del evento no se encuentra en el registro");
            return;
        } else if(validarCorreoYcodEvento(email, codEvento, "pago") == true){
            JOptionPane.showMessageDialog(null, "El correo electronico: " + email + " ya ha pagado su inscripcion al evento con codigo: " + codEvento);
            return;
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
        }
        
        if(montoBD == monto){
            String sql = "INSERT INTO pago (codigo_evento, correo_electronico, metodo_pago, monto) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, codEvento);
                ps.setString(2, email);
                ps.setString(3, metodoPago);
                ps.setDouble(4, monto);

                int rowsAffected = ps.executeUpdate();
                mensajeQuery(rowsAffected);
            } catch (SQLException e) {
                System.out.println("Ha ocurrido un error inseperado");
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "El monto a pagar no es lo que equivale el costo del evento");
        } 
    }

    public void validarInscripcion() {

    }
    
    public void registrarActividad(String codActividad, String codEvento, String tipoActividad, String titulo, String email, String horaInicio, String horaFin, int cupoMax) {
        
        if(buscarCodigo(codActividad, "actividad", "codigo_actividad") == true){
            JOptionPane.showMessageDialog(null, "El codigo del evento ya existe en el registro");
            return;
        } else if (buscarCodigo(codEvento, "evento", "codigo_evento") == false){
            JOptionPane.showMessageDialog(null, "El codigo del evento no existe en el registro");
            return;
        } else if (buscarEmail(email, "inscripcion") == false){
            JOptionPane.showMessageDialog(null, "El correo electronico del participante no existe en el registro");
            return;
        }
        
        String sqlSelect = "SELECT tipo_inscripcion FROM inscripcion WHERE correo_electronico = ?";   
        try (PreparedStatement ps = connection.prepareStatement(sqlSelect)){
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String rol = rs.getString("tipo_inscripcion");
                    if("ASISTENTE".equals(rol)){
                        JOptionPane.showMessageDialog(null, "No se le puede asignar esta actividad al participante, ya que es un ASISTENTE");
                        return;
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
            //Pasar de String a sql.date
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
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        } catch (ParseException ex) {
            System.out.println("Formato invalido de hora");
            ex.printStackTrace();
        } 
    }
    
    
    
    public void regristrarAsistencia(String email, String codActividad) {
        if(buscarEmail(email,"participante") == false){
            JOptionPane.showMessageDialog(null, "El correo electronico no existe en el registro");
            return;
        } else if (buscarCodigo(codActividad, "actividad", "codigo_actividad") == false){
            JOptionPane.showMessageDialog(null, "El codigo de la actividad no existe en el registro");
            return;
        }
        
        String sqlRol = "SELECT correo_electronico FROM actividad WHERE correo_electronico = ?";
        try (PreparedStatement psRol = connection.prepareStatement(sqlRol)){
            psRol.setString(1,email);
            try (ResultSet rs = psRol.executeQuery()){
                if(rs.next()){
                    JOptionPane.showMessageDialog(null, "El correo del usuario no es un asistente");
                    return;
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
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }

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
    
    public void certificado(String email, String codEvento) {
        if(buscarEmail(email,"asistencia") == false){
            JOptionPane.showMessageDialog(null, "El correo no existe en el registro");
            return;
        } else if (buscarCodigo(codEvento, "evento", "codigo_evento") == false) {
            JOptionPane.showMessageDialog(null, "El codigo del evento no existe en el registro");
            return;
        } else if (buscarEmail(email, "certificado") || buscarCodigo(codEvento, "certificado", "codigo_evento")) {
            JOptionPane.showMessageDialog(null, "Ya se ha emitido un certificado para el participante: " + email + " al evento: " + codEvento);
            return;
        }
        
        String sql = "INSERT INTO certificado (codigo_evento, correo_electronico) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);

            String participante = nombreParticipante(email);
            String nombreEvento = nombreEvento(codEvento);

            String ruta = "/home/jgarcia07/NetBeansProjects/IPC2_Practica01/Reportes";
            String nombreArchivo =  "Certificado: " + participante + ".html";
            
            System.out.println(nombreArchivo);
            
            crearCertificado certificado = new crearCertificado();
            certificado.emitirCertificado(ruta, nombreArchivo, participante, nombreEvento);
            
            int rowsAffected = ps.executeUpdate();
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }
}
