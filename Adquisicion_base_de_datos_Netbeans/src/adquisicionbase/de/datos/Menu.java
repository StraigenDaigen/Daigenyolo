/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adquisicionbase.de.datos;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import static jdk.nashorn.internal.codegen.CompilerConstants.__FILE__;
import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.awt.TrayIcon;
import static java.lang.Integer.parseInt;
import javax.swing.ImageIcon;

/**
 *
 * @author Lucas Bechara
 */
public class Menu extends javax.swing.JFrame implements Runnable {

    boolean actualizacion = true;
    private ImageIcon imageicon;
    private TrayIcon trayicon;
    public int id;
    boolean prueba = true;
    int minutosmuestra;

    private JPanel contentPane;

    conectar cc = new conectar();
    Connection conexion = cc.conexion();
    int cont = 0;
    private Thread t;

    /**
     * Creates new form Menu
     */
    public Menu() {
        imageicon = new ImageIcon(this.getClass().getResource("/images/logo.jpg"));
        initComponents();
        this.setIconImage(imageicon.getImage());
        contar();

        //tiempo();
    }

    public static String encodeToString(BufferedImage image) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    int cargar(int limite) {
        BufferedImage img = null;
        String sql = "SELECT * FROM uao limit " + limite + ",1";
        String imagen_string = null;

        try {

            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                id = rs.getInt("id");
                imagen_string = rs.getString("imagen");
                lb_nombre.setText(rs.getString("nombre"));
                ocupacion.setText(rs.getString("ocupacion"));
                fecha.setText(rs.getString("fecha"));
                hora.setText(rs.getString("hora"));

            }
            if (imagen_string == null) {
                cont = cont - 1;
                contar();
            } else {
                img = decodeToImage(imagen_string);
                ImageIcon icon = new ImageIcon(img);
                Icon icono = new ImageIcon(icon.getImage().getScaledInstance(lbver.getWidth(), lbver.getHeight(), Image.SCALE_DEFAULT));
                lbver.setText(null);
                lbver.setIcon(icono);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    void eliminar_imagen(String nom, int ident) {
        try {
            String sql = "DELETE FROM uao WHERE nombre='" + nom + "' AND id=" + ident;
            Statement pst = conexion.createStatement();
            int n = pst.executeUpdate(sql);
            if (n > 0) {
                JOptionPane.showMessageDialog(null, "Registro Eliminado.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
                lbver.setIcon(null);
                lb_nombre.setText("");
                contar();
            } else {
                System.out.println(sql);
                JOptionPane.showMessageDialog(null, "No existe el registro a eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void contar() {
        String sql2 = "SELECT count(*) as cont from uao";
        try {
            int con = 0;
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery(sql2);
            while (rs.next()) {
                con = rs.getInt("cont");
            }
            if (con == 0) {
                lb_nombre.setText("No se encontraron imagenes");
            } else {
                cont = con - 1;
                cargar(cont);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void guardar_imagen(String imagen, String nombre, String nivel_ocupacion) {

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        String cale = date.format(now);
        String times = time.format(now);

        String sql = "INSERT INTO uao values (null,?,?,?,?,?)";
        try {

            PreparedStatement pst = conexion.prepareStatement(sql);
            pst.setString(1, nombre);
            pst.setString(2, imagen);
            pst.setString(3, nivel_ocupacion);
            pst.setString(4, cale);
            pst.setString(5, times);

            int n = pst.executeUpdate();
            if (n > 0) {
                System.out.println("Registro guardado");
                contar();
            } else {
                JOptionPane.showMessageDialog(null, "Error al insertar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | HeadlessException e) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     */
    public void tiempo() {

        int minutos;
        Date now = new Date(System.currentTimeMillis());
        System.out.println(now);
        SimpleDateFormat time = new SimpleDateFormat("mm");
        String min = time.format(now);
        minutos = parseInt(min);
        int diferencia = minutos - minutosmuestra;
        System.out.print("Minuto actual: ");
        System.out.println(minutos);
        System.out.print("Minuto muestra: ");
        System.out.println(minutosmuestra);
        System.out.print("Tiempo de difrencia para actualizacion es : ");
        System.out.println(diferencia);
        if (diferencia > 2 || diferencia < 0) {
            actualizar();
            minutosmuestra = minutos;
        }
        System.out.println(now);
        System.out.println("Termino de ver diferencia");
    }

    void actualizar() {
        System.out.println("actualizando");

        String path = "G:\\Mi unidad\\Todo_Tesis_Testeo\\registro\\base_de_datos";
        String file;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                file = listOfFile.getName();
                //System.out.println(file);
                if (file.endsWith(".jpg")) {
                    System.out.println("Hay imagenes");
                    if (file.startsWith("lab automatica llena")) {
                        String name = "Laboratorio Automatica";
                        String nivel_ocupacion = "lleno";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);
                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("lab automatica media")) {

                        String name = "Laboratorio Automatica";
                        String nivel_ocupacion = "medio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("lab automatica vacia")) {

                        String name = "Laboratorio Automatica";
                        String nivel_ocupacion = "vacio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("cafeteria piso1 llena")) {

                        String name = "Cafeteria del primer piso";
                        String nivel_ocupacion = "lleno";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("cafeteria piso1 media")) {

                        String name = "Cafeteria del primer piso";
                        String nivel_ocupacion = "medio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("cafeteria piso1 vacia")) {

                        String name = "Cafeteria del primer piso";
                        String nivel_ocupacion = "vacio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("lab electronica llena")) {

                        String name = "Laboratorio de electronica";
                        String nivel_ocupacion = "lleno";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("lab electronica media")) {

                        String name = "Laboratorio de electronica";
                        String nivel_ocupacion = "medio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    } else if (file.startsWith("lab electronica vacia")) {

                        String name = "Laboratorio de electronica";
                        String nivel_ocupacion = "vacio";
                        System.out.print("Se encontro la imagen : ");
                        System.out.println(file);

                        String ubicacion = folder + "\\" + file;
                        //System.out.println(ubicacion);
                        File di = new File(ubicacion);
                        try {
                            // TODO add your handling code here:
                            BufferedImage img = ImageIO.read(new File(ubicacion));
                            String image_string = encodeToString(img);
                            guardar_imagen(image_string, name, nivel_ocupacion);

                        } catch (IOException ex) {
                            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!di.exists()) {
                            System.out.println("El archivo data no existe.");
                        } else {
                            di.delete();
                            System.out.println("El archivo data fue eliminado.");
                        }

                    }
                }
            }
        }
        System.out.println("Termino la actualizando");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbver = new javax.swing.JLabel();
        izquierda = new javax.swing.JButton();
        derecha = new javax.swing.JButton();
        Eliminar = new javax.swing.JButton();
        lb_nombre = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        fecha = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlabel2 = new javax.swing.JLabel();
        ocupacion = new javax.swing.JLabel();
        jlabel3 = new javax.swing.JLabel();
        hora = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        izquierda.setText("<<");
        izquierda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izquierdaActionPerformed(evt);
            }
        });

        derecha.setText(">>");
        derecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derechaActionPerformed(evt);
            }
        });

        Eliminar.setText("Eliminar");
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });

        lb_nombre.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lb_nombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        fecha.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        fecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fecha.setLabelFor(fecha);
        fecha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("FECHA");

        jlabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jlabel2.setText("HORA");

        ocupacion.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ocupacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ocupacion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jlabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jlabel3.setText("OCUPACION");

        hora.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        hora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hora.setLabelFor(hora);
        hora.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 51, 51));
        jButton1.setText("SALIR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1bCerrar(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lb_nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(lbver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ocupacion, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlabel3)
                    .addComponent(hora, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlabel2)
                    .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(60, 60, 60))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(izquierda, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(94, 94, 94)
                        .addComponent(Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                        .addComponent(derecha, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)))
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lb_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbver, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hora, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jlabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ocupacion, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Eliminar)
                                .addComponent(izquierda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(derecha)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jLabel3.setFont(new java.awt.Font("Tempus Sans ITC", 3, 48)); // NOI18N
        jLabel3.setForeground(java.awt.Color.red);
        jLabel3.setText("MONITOREO UAO");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Base de datos");

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton2.setActionCommand("sincronizacion");
        jButton2.setLabel("Iniciar sincronizacion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logomini.jpg"))); // NOI18N
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.N_RESIZE_CURSOR));
        jLabel4.setDebugGraphicsOptions(javax.swing.DebugGraphics.FLASH_OPTION);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(67, 67, 67)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(57, 57, 57)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void izquierdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_izquierdaActionPerformed
        // TODO add your handling code here:
        prueba = false;

        if (cont == 0) {
            cargar(cont);
        } else if (cont < 0) {
            cargar(cont = 0);
        } else {
            cont = cont - 1;
            cargar(cont);
        }

    }//GEN-LAST:event_izquierdaActionPerformed

    private void derechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derechaActionPerformed
        // TODO add your handling code here:
        prueba = false;

        cont = cont + 1;
        cargar(cont);

    }//GEN-LAST:event_derechaActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        // TODO add your handling code here:
        prueba = false;
        id=cargar(cont);
        eliminar_imagen(lb_nombre.getText(),id);
        fecha.setText(null);
        hora.setText(null);
        ocupacion.setText(null);
        lbver.setText(null);
    }//GEN-LAST:event_EliminarActionPerformed

    private void jButton1bCerrar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1bCerrar
        // TODO add your handling code here:
        prueba = false;
        actualizacion = false;
        int dato = JOptionPane.showConfirmDialog(null, "¿Está seguro de salir?");
        if (dato == 0) {
            this.dispose();
            JOptionPane.showMessageDialog(null, "Muhas gracias por su tiempo, vuelva pronto.");
        }
    }//GEN-LAST:event_jButton1bCerrar

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizacion = false;
        System.out.println("solicito acatualizar");
        t = new Thread(this);
        actualizacion = true;
        t.start();


    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Eliminar;
    private javax.swing.JButton derecha;
    private javax.swing.JLabel fecha;
    private javax.swing.JLabel hora;
    private javax.swing.JButton izquierda;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel jlabel2;
    private javax.swing.JLabel jlabel3;
    private javax.swing.JLabel lb_nombre;
    private javax.swing.JLabel lbver;
    private javax.swing.JLabel ocupacion;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        while (actualizacion) {
            tiempo();
            try {
                Thread.sleep(120000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
