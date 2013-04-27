/*
 * JNameComparator.java
 *
 * Created on 2008/05/05, 11:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.naming;

import java.util.Comparator;

/**
 *
 * @author takashi
 */
public class JNameComparator implements Comparator<String>{
    private static JNameComparator instance=null;
    /** Creates a new instance of JNameComparator */
    public JNameComparator() {
        
    }
    public static JNameComparator getInstance(){
        if (instance ==null)
            instance=new JNameComparator();
        return instance;
    }
    public int compare(String o1, String o2) {
        return new JNaming(o1).compareTo(new JNaming(o2));
    }
    public boolean equals(Object o){
        return (o instanceof JNameComparator);
    }
}
