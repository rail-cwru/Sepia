//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.13 at 09:38:30 PM EDT 
//


package edu.cwru.SimpleRTS.environment.state.persistence.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;


/**
 * <p>Java class for ResourceNodeExhaustionLog complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceNodeExhaustionLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exhaustedNodeID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="exhaustedNodeType" type="{}ResourceNodeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceNodeExhaustionLog", propOrder = {
    "exhaustedNodeID",
    "exhaustedNodeType"
})
public class XmlResourceNodeExhaustionLog {

    protected int exhaustedNodeID;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected Type exhaustedNodeType;

    /**
     * Gets the value of the exhaustedNodeID property.
     * 
     */
    public int getExhaustedNodeID() {
        return exhaustedNodeID;
    }

    /**
     * Sets the value of the exhaustedNodeID property.
     * 
     */
    public void setExhaustedNodeID(int value) {
        this.exhaustedNodeID = value;
    }

    /**
     * Gets the value of the exhaustedNodeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Type getExhaustedNodeType() {
        return exhaustedNodeType;
    }

    /**
     * Sets the value of the exhaustedNodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExhaustedNodeType(Type value) {
        this.exhaustedNodeType = value;
    }

}
