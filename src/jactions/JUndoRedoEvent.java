/*
 * JUndoRedoEvent.java
 *
 * Created on 2007/09/20, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.awt.Event;

/**
 *
 * @author TI
 */
public class JUndoRedoEvent{
    public static final int REDO=1;
    public static final int UNDO=2;
    private Object source;
    private int eventType;
    /**
     * Creates a new instance of JUndoRedoEvent
     */
    public JUndoRedoEvent(Object source,int eventType) {
        this.source=source;
        this.eventType=eventType;
    }
    public int getEventType(){
        return eventType;
    }
    public Object getSource(){
        return source;
    }
}
