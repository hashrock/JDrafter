/*
 * JDMenuItem.java
 *
 * Created on 2007/09/29, 11:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author TK
 */
public class JDMenuItem extends JCheckBoxMenuItem{
    
    /** Creates a new instance of JDMenuItem */
    public JDMenuItem() {
    }
    public JDMenuItem(Icon icon){
        this();
        setIcon(icon);
    }
    public void fireActionEvent(){
       fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,super.getActionCommand()));
    }
}