//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.08 at 11:29:06 AM EST 
//


package edu.cwru.SimpleRTS.environment.state.persistence.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;


/**
 * <p>Java class for ResourceNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{}ResourceNodeType"/>
 *         &lt;element name="xPosition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="yPosition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="initialAmount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="amountRemaining" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceNode", propOrder = {
    "type",
    "xPosition",
    "yPosition",
    "initialAmount",
    "amountRemaining"
})
public class XmlResourceNode {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected Type type;
    protected int xPosition;
    protected int yPosition;
    protected int initialAmount;
    protected int amountRemaining;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(Type value) {
        this.type = value;
    }

    /**
     * Gets the value of the xPosition property.
     * 
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Sets the value of the xPosition property.
     * 
     */
    public void setXPosition(int value) {
        this.xPosition = value;
    }

    /**
     * Gets the value of the yPosition property.
     * 
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Sets the value of the yPosition property.
     * 
     */
    public void setYPosition(int value) {
        this.yPosition = value;
    }

    /**
     * Gets the value of the initialAmount property.
     * 
     */
    public int getInitialAmount() {
        return initialAmount;
    }

    /**
     * Sets the value of the initialAmount property.
     * 
     */
    public void setInitialAmount(int value) {
        this.initialAmount = value;
    }

    /**
     * Gets the value of the amountRemaining property.
     * 
     */
    public int getAmountRemaining() {
        return amountRemaining;
    }

    /**
     * Sets the value of the amountRemaining property.
     * 
     */
    public void setAmountRemaining(int value) {
        this.amountRemaining = value;
    }

}
