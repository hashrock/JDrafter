/*
 * JCAGMultEdit.java
 *
 * Created on 2007/12/19, 19:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.geom.Area;
import java.util.Vector;
import jgeom.JPathIterator;
import jobject.JPathObject;
import jscreen.JDocumentViewer;

/**
 *
 * @author takashi
 */
public class JCAGMultEdit extends JCAGAddEdit{
    
    /** Creates a new instance of JCAGMultEdit */
    public JCAGMultEdit(JDocumentViewer viewer,Vector<JPathObject> targets) {
        super(viewer,targets);
        presentationName="êœ";
    }
    protected JPathObject createObject(Vector<JPathObject>objs){
        JPathObject ret=objs.get(0).clone();
        Area a=new Area(ret.getPath().getShape());
        for (int i=1;i<objs.size();i++){
            Area b=new Area(objs.get(i).getPath().getShape());
            a.intersect(b);
        }
        if (a.isEmpty()){
            return null;
        }else{
            JPathIterator jp=new JPathIterator(a.getPathIterator(null));
            jp.normalize();
            ret.setPath(jp.getJPath());
            return ret;
        }
    }
    
}
