/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jui.color.arrow;

import java.awt.Shape;
import javax.swing.JComboBox;

/**
 *
 * @author takashi
 */
public class ArrowCombo extends JComboBox{
    private ArrowRenderer arrowRenderer;
    public ArrowCombo(){
        removeAllItems();
        ArrowFactory  factory=ArrowFactory.getInstance();
        this.addItem(null);
        for(Shape s:factory.getArrowVector()){
            this.addItem(s);
        }

        arrowRenderer=new ArrowRenderer();
        this.setRenderer(arrowRenderer);
    }
    public  int getDirection(){
        return arrowRenderer.getDirection();
    }
    public void setDirection(int dr){
        arrowRenderer.setDirection(dr);
    }

}
