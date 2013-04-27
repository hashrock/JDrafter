/*
 * AbstractTypeMenuItem.java
 *
 * Created on 2008/06/04, 22:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.text.AttributeSet;

/**
 *
 * @author takashi
 */
public abstract class AbstractTypeMenuItem extends JMenu implements JTypeMenuItem{
    
    /** Creates a new instance of AbstractTypeMenuItem */
    public AbstractTypeMenuItem() {
    }
    protected void fireActionEvents(String cmd){
        ActionEvent e=new ActionEvent(this,ActionEvent.ACTION_PERFORMED,cmd);
        for (int i=0;i<getActionListeners().length;i++){
            getActionListeners()[i].actionPerformed(e);
        }
    }
}
