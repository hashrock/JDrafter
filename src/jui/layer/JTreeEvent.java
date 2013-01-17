/*
 * JTreeEvent.java
 *
 * Created on 2008/05/11, 11:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.layer;

/**
 *
 * @author takashi
 */
public class JTreeEvent{
    private JPageTreeModel source;
    /** Creates a new instance of JTreeEvent */
    public JTreeEvent(JPageTreeModel source) {
        this.source=source;
    }
    public JPageTreeModel getSource(){
        return source;
    }
}
