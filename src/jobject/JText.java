/*
 * JText.java
 *
 * Created on 2007/10/29, 9:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.text.DefaultStyledDocument;
import jscreen.JEnvironment;
import jobject.text.TextLocater;

/**
 *
 * @author i002060
 */
public interface JText {
    public DefaultStyledDocument getStyledDocument();
    public DefaultStyledDocument getCloneStyledDocument();
    public void setStyledDocument(DefaultStyledDocument doc);
    public void textUpdate(JEnvironment env);
    public  AffineTransform getTotalTransform();
    public void updatePath();
    public TextLocater createLocater(FontRenderContext frc);
    public Shape getLayoutShape();
}
