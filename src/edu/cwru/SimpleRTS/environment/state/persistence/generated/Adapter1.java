//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.09 at 08:01:11 PM EST 
//


package edu.cwru.SimpleRTS.environment.state.persistence.generated;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import edu.cwru.SimpleRTS.model.unit.UnitTask;

public class Adapter1
    extends XmlAdapter<String, UnitTask>
{


    public UnitTask unmarshal(String value) {
        return (UnitTask.valueOf(value));
    }

    public String marshal(UnitTask value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
