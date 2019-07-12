/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;

import adquisicionbase.de.datos.Menu;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas Bechara
 */
public class actualizacion extends Thread {

    boolean b = false;

    @Override
    public void run() {

        System.out.println("Hello from a thread!");

        while (b == false) {
            
            

            try {
                Thread.sleep(50);

                // b becomes true
            } catch (InterruptedException ex) {
                Logger.getLogger(actualizacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            

        }
    }
}
