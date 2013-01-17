/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jui.color.arrow;

import java.awt.Shape;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobject.JDocument;
import jobject.JGroupObject;
import jobject.JLeaf;
import jobject.JObject;
import jobject.effector.JArrowEffect;

/**
 *
 * @author takashi
 */
public final class ArrowFactory {
    private Vector<Shape> arrowVector;
    private static ArrowFactory instance=new ArrowFactory();
    private ArrowFactory(){
        JDocument doc=null;
        try {
            URL url= getClass().getResource("/jui/uipicture/arrowhead.jdoc");
            //File f=new File("../arrowhead.jdoc");
            ObjectInputStream ostream=new ObjectInputStream(url.openStream());
            doc=(JDocument)ostream.readObject();
            ostream.close();
        }
        catch(Exception ex){
            Logger.getLogger(ArrowFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (doc==null){
            return;
        }
        arrowVector=new Vector<Shape>();
        createArrowVector(doc,arrowVector);
    }
    private void createArrowVector(JObject o,Vector<Shape> arrowVector){
        for(int i=0;i<o.size();i++){
            JLeaf jl=o.get(i);
            if (jl instanceof JGroupObject){
                Shape s=JArrowEffect.createNormalShape((JGroupObject)jl, 1);
                if (s!=null)
                    arrowVector.add(s);
            }else if(jl instanceof JObject){
                createArrowVector((JObject)jl,arrowVector);
            }
        }
    }
    public Vector<Shape> getArrowVector(){
        return arrowVector;
    }
    public static ArrowFactory getInstance(){
        return instance;
    }
}
