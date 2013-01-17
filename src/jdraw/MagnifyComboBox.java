/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdraw;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import javax.swing.JComboBox;
import javax.swing.RepaintManager;
import jtools.JMagnifyTool;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JScroller;

/**
 *
 * @author takashi
 */
public class MagnifyComboBox  extends JComboBox{
    private JDocumentViewer viewer=null;
    private boolean activeAction=true;
    public MagnifyComboBox(){
        float[] mg=JMagnifyTool.MAGNIFY_ARRAY;
        for (int i=mg.length-1;i>=0;i--){
            addItem(new InnerComboItem(mg[i]));
        }
        setMaximumRowCount(25);
        setFocusable(false);
        setEnabled(false);
        this.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        float cm=((Number)getSelectedItem()).floatValue();
        float oldMag=(float)viewer.getEnvironment().getMagnification();
        if (cm!=oldMag){
            JScroller scroller =viewer.getScroller();
            Point2D p=new Point2D.Double();
            JEnvironment env=viewer.getEnvironment();
            Point2D cp=new Point2D.Double(viewer.getVisibleRect().getCenterX(),viewer.getVisibleRect().getCenterY());
            env.getToAbsoluteTransform().transform(cp,p);
            env.setMagnification(cm);
            viewer.adjustSize();
            env.getToScreenTransform().transform(p,p);
            Rectangle bounds=viewer.getBounds();
            Rectangle view=scroller.getViewport().getViewRect();
            int ofsX=(int)Math.min(bounds.width-view.width,p.getX()-view.width/2);
            int ofsY=(int)Math.min(bounds.height-view.height,p.getY()-view.height/2);
            ofsX=Math.max(ofsX,0);
            ofsY=Math.max(ofsY,0);
            scroller.getViewport().setViewPosition(new Point(ofsX,ofsY));
            viewer.isDraftMode=false;
            
            RepaintManager.currentManager(scroller).markCompletelyDirty(scroller);
            RepaintManager.currentManager(viewer).markCompletelyDirty(viewer);
            RepaintManager.currentManager(viewer).paintDirtyRegions();
        }
    }
    public void changeStates(JDocumentViewer viewer){
        this.viewer=viewer;
        if (viewer ==null){
            setEnabled(false);
        }else{
            setEnabled(true);
            float cm=((Number)getSelectedItem()).floatValue();
            if (cm !=viewer.getEnvironment().getMagnification()){
                setMagnify((float)viewer.getEnvironment().getMagnification());
            }
        }
    }
    private void setMagnify(float m){
       float cm=((Number)getSelectedItem()).floatValue();
       if (cm!=m){
           activeAction=false;
           setSelectedItem(new InnerComboItem(m));
       }
    }
    @Override
    public void fireActionEvent(){
        if (!activeAction){
            activeAction=true;
            return;
        }
        super.fireActionEvent();
    }
    private class InnerComboItem extends Number{
        float value;
        public InnerComboItem(float f){
            value=f;
        }

        @Override
        public int intValue() {
            return (int)value;
        }

        @Override
        public long longValue() {
           return (long)value;
        }

        @Override
        public float floatValue() {
            return value;
        }

        @Override
        public double doubleValue() {
            return (double)value;
        }
        @Override
        public boolean equals(Object o){
            if (o instanceof Number){
                return ((Number)o).floatValue()==value;
            }
            return false;
        }
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Float.floatToIntBits(this.value);
            return hash;
        }
        @Override
        public String toString(){
            return String.valueOf((int)(value*100))+"%";
        }
    }

}
