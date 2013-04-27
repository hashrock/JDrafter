/*
 * JUndoRedoListener.java
 *
 * Created on 2007/12/07, 16:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

/**
 *
 * @author i002060
 */
public interface JUndoRedoListener {
    public void undoRedoEventHappened(JUndoRedoEvent e);
}
