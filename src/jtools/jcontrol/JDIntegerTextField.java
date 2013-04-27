/*
 * JDNumericTextField.java
 *
 * Created on 2007/02/03, 10:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools.jcontrol;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;
/**
 *
 * @author TK
 */
public class JDIntegerTextField extends JFormattedTextField{
    
    /** Creates a new instance of JDNumericTextField */
    private Vector<ChangeListener> changeListeners;
    private String format;
    private Number minValue,maxValue;

    public JDIntegerTextField() { 
        format=null;
        minValue=maxValue=null;
        changeListeners=new Vector<ChangeListener>();
        this.setFormatterFactory(new myFormatterFactory());
    }
    public void addChangeListener(ChangeListener listener){
        if (changeListeners.contains(listener))
            return;
        changeListeners.add(listener);
    }
        
    public void removeChangeListener(ChangeListener listener){
        changeListeners.remove(listener);
    }
    public void setFormat(String f){
        format=f;
    }
    public double getDoubleValue(){
        if (getValue()==null) return 0;
        if (getValue() instanceof Number){
            Number n=(Number)getValue();
            return n.doubleValue();
        }
        return 0;
    }
    public float getFloatValue(){
        return ((float)getDoubleValue());
    }
    public int getIntValue(){
        if (getValue()==null) return 0;
        if (getValue() instanceof Number){
            Number n=(Number)getValue();
            return n.intValue();
        }
        return 0;      
    }
    public void setMinValue(Number n){
        minValue=n;
    }
    public void setMaxValue(Number n){
        maxValue=n;
    }
    public Number getMinValue(){
        return minValue;
    }
    public Number getMaxValue(){
        return maxValue;
    }
    public void setIntValue(int i){
        setValue(new Integer(i));
    }
    private void fireChangeEvent(){
        ChangeEvent e=new ChangeEvent(this);
        for (int i=0;i<changeListeners.size();i++)
            changeListeners.get(i).stateChanged(e);
    }
    public void commitEdit() throws java.text.ParseException{
        try{
            super.commitEdit();
        }catch (java.text.ParseException e){
            throw e;
        }
        fireChangeEvent();
    }
    class myFormatterFactory extends JFormattedTextField.AbstractFormatterFactory{
        myFormatter formatter;
        public myFormatterFactory(){
            formatter=new myFormatter();
        }
        public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
            return formatter;
        }
        
    }
    class myFormatter extends JFormattedTextField.AbstractFormatter{
        public Object stringToValue(String text) throws ParseException {
            Double ret=null;
            String c=text.trim();
            if (c=="") c="0";
            try {
                ret=Double.parseDouble(c);
            }catch(NumberFormatException e){
                throw new ParseException(e.getMessage(),0);
            }
            Integer rt=new Integer((int)Math.round(ret.doubleValue()));
            if (minValue !=null && minValue.intValue()>rt.intValue()) 
                throw new ParseException("",0);
            if (maxValue !=null && maxValue.intValue()<rt.intValue())
                throw new ParseException("",0);
            return rt;
        }
        public String valueToString(Object value) throws ParseException {
            if (!(value instanceof Integer))
                throw new ParseException("NumelicError",0);
            Integer vl=(Integer)value;
            if (format==null)
                return String.format("%d",vl.intValue());
            else
                return String.format(format,vl.intValue());
        }
        
    }
    
}
