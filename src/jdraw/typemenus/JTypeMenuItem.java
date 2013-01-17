/*
 * JTypeMenuItem.java
 *
 * Created on 2008/06/08, 10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionListener;
import javax.swing.text.AttributeSet;

/**
 *
 * @author takashi
 */
public interface JTypeMenuItem extends ActionListener{
    public  void setAttributes(AttributeSet attr);
    public  AttributeSet getAttributes();
}
