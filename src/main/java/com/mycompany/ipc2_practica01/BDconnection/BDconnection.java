/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01.BDconnection;

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

    private static final String IP = "LocalHost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "TriForceEvents";
    private static final String USER_NAME = "adminDBA";
    private static final String PASSWORD = "admin@123";

    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA;

    private Connection connection;

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
        } else {
            System.out.println("No sean podido ingresar los datos");
        }
    }

    public void registrarEvento(String codEvento, String fecha, String tipoEvento, String tituloEvento, String ubicacion, int cupoMax, double costo) {
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
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        } catch (ParseException ex) {
            System.out.println("Formato invalido de hora");
            ex.printStackTrace();
        } 
    }

    public void registrarParticipante(String nombre, String tipoParticipante, String institucion, String email) {
        //HAY QUE VALIDAR SI YA EXISTE EL USUARIO
        String sqlEmail = "SELECT COUNT(*) FROM participante WHERE correo_electronico = ?";
        try (PreparedStatement psEmail = connection.prepareStatement(sqlEmail)){
            psEmail.setString(1,email);
            ResultSet rs = psEmail.executeQuery();
            rs.next();
            if(rs.getInt(1) > 0){
                JOptionPane.showMessageDialog(null, "El usuario ya existe en la base de datos");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error inseperado");
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
    }

    public void validarInscripcion() {

    }

    public void registrarActividad(String codActividad, String codEvento, String tipoActividad, String titulo, String email, String horaInicio, String horaFin, int cupoMax) {
        String sqlSelect = "SELECT  rol_participante FROM inscripcion WHERE correo_electronico = ?";
        String sql = "INSERT INTO actividad (codigo_actividad, codigo_evento, correo_electronico, tipo_actividad, titulo_actividad, hora_inicio, hora_fin, cupo_maximo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        boolean esParticipante = false;
        
        try (PreparedStatement ps = connection.prepareStatement(sqlSelect)){
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String rol = rs.getString("rol_participante");
                    if("ASISTENTE".equals(rol)){
                        esParticipante = true;
                    }
                } else {
                    System.out.println("NO SE ENCONTRO EL USUARIO");
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if(esParticipante){
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
        } else {
            System.out.println("Es participante");
        }
        
    }

    public void regristrarAsistencia(String email, String codEvento) {
        String sql = "INSERT INTO asistencia (codigo_actividad, correo_electronico) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }

    public void certificado(String email, String codEvento) {
        String sql = "INSERT INTO certificado (codigo_evento, correo_electronico) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codEvento);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();
            mensajeQuery(rowsAffected);
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inseperado");
            e.printStackTrace();
        }
    }
}
