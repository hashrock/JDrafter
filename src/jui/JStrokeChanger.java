/*
 * JStrokeChanger.java
 *
 * Created on 2007/12/04, 16:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;


import jui.JDStrokePanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.UndoableEdit;
import jedit.paintedit.JChangeStrokesEdit;
import jobject.JColorable;
import jobject.JLeaf;
import jobject.JObject;
import jpaint.JStroke;
import jui.color.JColorChanger;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JStrokeChanger extends JComponent implements ChangeListener,ItemListener{
    private boolean disableEvent=false;
    private JDocumentViewer viewer=null;
    private JDStrokePanel panel=null;
    private JStroke stroke=JEnvironment.currentStroke;
    private static GeneralPath shade=new GeneralPath();
    private static GeneralPath shine=new GeneralPath();
    private static Color shadeColor=new Color(0f,0f,0f,0.5f);
    private static Color shineColor=new Color(1f,1f,1f);
    private static Rectangle bounds=new Rectangle(1,1,47,15);
    /** Creates a new instance of JStrokeChanger */
    public JStrokeChanger() {
        setToolTipText("線の種類(クリックして編集)");
        panel=new JDStrokePanel();
        panel.addChangeListener(this);
        MouseAdapter madp=new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                mClicked(e);
            }
        };
        this.addMouseListener(madp);
        shade.moveTo(1,16);
        shade.lineTo(1,1);
        shade.lineTo(47,1);
        shine.moveTo(47,1);
        shine.lineTo(47,16);
        shine.lineTo(1,16);
        Dimension dm=new Dimension(48,20);
        this.setMaximumSize(dm);
        this.setMinimumSize(dm);
        this.setPreferredSize(dm);
    }
    private void mClicked(MouseEvent e){
        panel.setStroke(stroke.getStroke());
        panel.showAsPopup(this);
        
    }
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        g2.setStroke(new BasicStroke(0f));
        g2.setColor(Color.WHITE);
        g2.fill(bounds);
        g2.setColor(shadeColor);
        g2.draw(shade);
        g2.setColor(shineColor);
        g2.draw(shine);
        g2.setStroke(stroke);
        if (stroke.getEndCap()!=BasicStroke.CAP_BUTT || stroke.getWidth()>12.5f){
            Stroke st=new BasicStroke(Math.min(stroke.getWidth(),12.5f),BasicStroke.CAP_BUTT,
                    stroke.getLineJoin(),stroke.getMiterLimit(),stroke.getDashArray(),stroke.getDashPhase());
            g2.setStroke(st);
        }
        g2.setColor(Color.BLACK);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawLine(3,9,46,9);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        
    }
    public void setViewer(JDocumentViewer viewer){
        
        if (this.viewer !=null)
            this.viewer.getDocument().removeItemListener(this);
        this.viewer=viewer;
        viewer.getDocument().addItemListener(this);
    }
    public void setStroke(JStroke s){
        this.stroke=s;
        repaint();
    }
    public void stateChanged(ChangeEvent e) {
        if (!panel.isDesided()) return;
        JStroke jst=new JStroke(panel.getStroke());
        UndoableEdit edt=updateStroke(jst);
        if (edt !=null){
            viewer.getDocument().fireUndoEvent(edt);
        }
    }
    public UndoableEdit updateStroke(JStroke newStroke){
        disableEvent=true; 
        UndoableEdit ret=null;
        if (viewer !=null){
            Vector<JLeaf> leafs=getSelectedLeafs(viewer.getCurrentRequest());
            ret=update(viewer,leafs,newStroke);
            viewer.repaint();
        }
        JEnvironment.currentStroke=stroke=newStroke;
        disableEvent=false;
        repaint();
        return ret;
    }
    private UndoableEdit update(JDocumentViewer viewer,Vector<JLeaf> targets,JStroke newStroke){
        if (targets.isEmpty() || viewer==null) return null;
        return new JChangeStrokesEdit(viewer,targets,newStroke);
    }
    public static Vector<JLeaf> getSelectedLeafs(JRequest req){
        Vector<JLeaf> ret=new Vector<JLeaf>();
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JObject){
                JColorChanger.getLeafObjects(ret,(JObject)o);
            }else if ((o instanceof JLeaf)&&(o instanceof JColorable)){
                ret.add((JLeaf)o);
            }
        }
        return ret;
    }
    public void itemStateChanged(ItemEvent e) {
        if (disableEvent) return;
        stroke=getCommonStroke();
        repaint();
    }
    private JStroke getCommonStroke(){
        JStroke ret=JEnvironment.currentStroke;
        if (viewer==null) return ret;
        
        JRequest req=viewer.getCurrentRequest();
        Vector<JLeaf> leafs=getSelectedLeafs(req);
        if (leafs.isEmpty()) return ret;
        ret=leafs.get(0).getStroke();
        for (int i=1;i<leafs.size();i++){
            if (!ret.equals(leafs.get(i).getStroke())){
                return JEnvironment.currentStroke;
            }
        }
        return ret;
    }
}
