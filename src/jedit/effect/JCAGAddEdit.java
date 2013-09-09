/*
 * JCAGAddEdit.java
 *
 * Created on 2007/12/19, 11:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.geom.Area;
import java.util.Vector;
import jedit.JAbstractEdit;
import jgeom.JPathIterator;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JCAGAddEdit extends JAbstractEdit{
    private Vector<JPathObject> targets=null;
    private Vector<JObject> parents=null;
    private Vector<Integer> indexes=null;
    private JPathObject createdObject=null;
    
    /** Creates a new instance of JCAGAddEdit */
    public JCAGAddEdit(JDocumentViewer viewer,Vector<JPathObject> targets) {
        super(viewer);
        this.targets=(Vector<JPathObject>)targets;
        sort(this.targets);
        parents=new Vector<JObject>();
        indexes=new Vector<Integer>();
        for (int i=0;i<targets.size();i++){
            JObject parent=targets.get(i).getParent();
            parents.add(parent);
            indexes.add(parent.indexOf(targets.get(i)));
        }
        presentationName="和(結合)";
        createdObject=createObject(targets);
        redo();
        
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=targets.size()-1;i>=0;i--){
            env.addClip(targets.get(i).getBounds());
            targets.get(i).getParent().remove(targets.get(i));
        }
        if (createdObject !=null){
            viewer.getAvailableLayer().add(createdObject);
            env.addClip(createdObject.getBounds());
            req.add(createdObject);
        }
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        req.clear();
        if (createdObject !=null){
            env.addClip(createdObject.getBounds());
            createdObject.getParent().remove(createdObject);
        }
        for (int i=0;i<targets.size();i++){
            parents.get(i).add(indexes.get(i),targets.get(i));
            env.addClip(targets.get(i).getBounds());
            req.add(targets.get(i));
        }
    }
    protected JPathObject createObject(Vector<JPathObject>objs){
        JPathObject ret=objs.get(0).clone();
        Area a=new Area(ret.getPath().getShape());
        for (int i=1;i<objs.size();i++){
            Area b=new Area(objs.get(i).getPath().getShape());
            a.add(b);
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
    public static void sort(Vector<JPathObject>objs){
        for (int i=0;i<objs.size()-1;i++){
            for (int j=i+1;j<objs.size();j++){
                JPathObject ji=objs.get(i),jj=objs.get(j);
                if (ji.getParent()==jj.getParent()){
                    if (ji.getParent().indexOf(ji)>jj.getParent().indexOf(jj)){
                        swap(objs,i,j);
                    }
                }else if (ji.getParent().getParent().indexOf(ji.getParent())>jj.getParent().getParent().indexOf(jj.getParent())){
                    swap(objs,i,j);
                }
                
            }
        }
    }
    private static void swap(Vector objs,int i,int j){
        if (i>j){
            int s=i;i=j;j=s;
        }
        Object oj=objs.remove(j);
        Object oi=objs.remove(i);
        objs.add(i,oj);
        objs.add(j,oi);
    }
}
