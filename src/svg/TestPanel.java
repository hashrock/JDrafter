/*
 * TestPanel.java
 *
 * Created on 2008/09/12, 13:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import svg.oject.SVGDocument;

/**
 *
 * @author i002060
 */
public class TestPanel extends JPanel{
    private SVGDocument doc=null;
    /** Creates a new instance of TestPanel */
    public TestPanel() {
    }
    
    public void setDocument(SVGDocument doc){
       this.doc=doc;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g.create();
        if (doc !=null){
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            doc.paint(g2);  
        }
        g.dispose();
    }
    
}
