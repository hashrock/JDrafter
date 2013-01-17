/*
 * SizeSubMenu.java
 *
 * Created on 2008/06/03, 19:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author takashi
 */
public class SizeSubMenu extends JCheckBoxMenuItem{
    private int fontSize;
    /** Creates a new instance of SizeSubMenu */
    public SizeSubMenu(int size) {
        setText(String.valueOf(size));
        fontSize=size;
    }
    public int getFontSize(){
        return fontSize;
    }
}
