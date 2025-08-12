/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ipc2_practica01.GUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author jgarcia07
 */
public class LimitadorCaracteres extends KeyAdapter{
    private final int LIMITE;

    public LimitadorCaracteres(int LIMITE) {
        this.LIMITE = LIMITE;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        JTextField campo = (JTextField) e.getSource();
        if (campo.getText().length() >= LIMITE) {
            e.consume();
        }
    }
}
