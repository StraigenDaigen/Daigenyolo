/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.awt.HeadlessException;
import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Lucas Bechara
 */
@Path("ws")
public class monitoreo {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of monitoreo
     */
    public monitoreo() {
    }

    /**
     * Retrieves representation of an instance of service.monitoreo
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() throws SQLException, JSONException {
        //TODO return proper representation object
        //throw new UnsupportedOperationException();

        Connection cn;
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monitoreo", "root", "");

        Statement pst;
        ResultSet rs;
        String sql = "select nombre,ocupacion,hora,fecha from uao order by hora desc";

        pst = cn.createStatement();
        rs = pst.executeQuery(sql);

        JSONArray arreglo = new JSONArray();

        while (rs.next()) {
            //creacion de JSON nuevo
            JSONObject json2 = new JSONObject();

            //(nombre,imagen,ocupacion,fecha,hora)
            String nombre = rs.getString("nombre");
            //String imagen= rs.getString("imagen");
            String ocupacion = rs.getString("ocupacion");
            String fecha = rs.getString("fecha");
            String hora = rs.getString("hora");

            Date now = new Date(System.currentTimeMillis());
            SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");

            String fecha_comparar = date.format(now);

            if (fecha == null ? fecha_comparar == null : fecha.equals(fecha_comparar)) {

                String[] arrayDehoras = hora.split(":");
                int minutoimage = parseInt(arrayDehoras[1]);
                int horaimage = parseInt(arrayDehoras[0]);
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                String tiempo = time.format(now);
                String[] arraydehorasnow = tiempo.split(":");
                int hor = parseInt(arraydehorasnow[0]);
                int minutos = parseInt(arraydehorasnow[1]);
                int difmin = minutos - minutoimage;
                int difhora = hor - horaimage;
                System.out.println(now);

                if (difhora == 0 || difhora == 1) {
                    if (difmin <= 30 && difmin >= 0 || difmin < -50) {

                        System.out.print("Tiempo Imagen:  ");
                        System.out.println(hora);
                        System.out.print("Tiempo Actual:  ");
                        System.out.println(tiempo);
                        System.out.print("Diferencia minuto:  ");
                        System.out.println(difmin);
                        System.out.print("Diferencia Hora:  ");
                        System.out.println(difhora);

                        //Agregar al json
                        json2.put("nombre", nombre);
                        //json2.put("imagen", imagen);
                        json2.put("ocupacion", ocupacion);

                        json2.put("hora", hora);

                        //acumulando json en el arreglo de json
                        arreglo.put(json2);
                    }
                }

            }

        }

        return arreglo.toString();

    }

    /**
     * PUT method for updating or creating an instance of monitoreo
     *
     * @param content representation for the resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void postJson(String content) throws JSONException {
        System.out.println(content);

        JSONObject json1 = new JSONObject(content);

        String name = json1.getString("nombre");
        String imagen = json1.getString("imagen");
        String ocupacion = json1.getString("ocupacion");

        guardar_imagen(imagen, name, ocupacion);

    }

    private void guardar_imagen(String imagen, String nombre, String nivel_ocupacion) {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        String fecha = date.format(now);
        String hora = time.format(now);

        String sql1 = "INSERT INTO uao values (null,?,?,?,?,?)";

        try {
            Connection cn;
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monitoreo", "root", "");

            PreparedStatement pst = cn.prepareStatement(sql1);
            pst.setString(1, nombre);
            pst.setString(2, imagen);
            pst.setString(3, nivel_ocupacion);
            pst.setString(4, fecha);
            pst.setString(5, hora);

            int n = pst.executeUpdate();
            if (n > 0) {
                System.out.println("Registro guardado");
            } else {
                JOptionPane.showMessageDialog(null, "Error al insertar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | HeadlessException e) {
            Logger.getLogger(monitoreo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
