
/*
 * JDoGroupEdit.java
 *
 * Created on 2007/09/17, 12:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.objectmenuedit;

import java.util.Vector;
import jedit.*;
import jobject.JGroupObject;
import jobject.JLeaf;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JDoGroupEdit extends JAbstractEdit{
    JObject[] parents;
    JLeaf[] objects;
    int[] indexes;
    JGroupObject groupObject;
    /** Creates a new instance of JDoGroupEdit */
    public JDoGroupEdit(JDocumentViewer viewer,Vector<JLeaf> objects) {
        super(viewer);
        groupObject=new JGroupObject();
        parents=new JObject[objects.size()];
        indexes=new int[objects.size()];
        this.objects=new JLeaf[objects.size()];
        double commonTheta=objects.get(0).getTotalRotation();
        for (int i=0;i<objects.size();i++){
            this.objects[i]=objects.get(i);
            if (commonTheta !=objects.get(i).getTotalRotation()){
                commonTheta=0;
            }
        }
        groupObject.setTotalRotation(commonTheta);
        for (int i=0;i<objects.size();i++){
            parents[i]=objects.get(i).getParent();
            
        }
        presentationName="グループ化";
        redo();
    }
    public void redo(){
        super.redo();
        JRequest req=viewer.getCurrentRequest();
        groupObject.clear();
        req.clear();
        for (int i=0;i<objects.length;i++){
            JLeaf o=objects[i];
            indexes[i]=parents[i].indexOf(objects[i]);
            parents[i].remove(o);
            groupObject.add(o);
            req.remove(o);
        }
        parents[objects.length-1].add(groupObject);
        req.add(groupObject);
    }
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        groupObject.getParent().remove(groupObject);
        groupObject.clear();
        req.remove(groupObject);
        for (int i=objects.length-1;i>=0;i--){
            parents[i].add(indexes[i],objects[i]);
            req.add(objects[i]);
        }
    }
}
