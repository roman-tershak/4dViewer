//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.17 at 11:22:55 AM EEST 
//


package com.viewer4d.config.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FigureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FigureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="precision" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="vertex" type="{tests:4DViewer:PlainFigureConfig}VertexType"/>
 *         &lt;/sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="edge" type="{tests:4DViewer:PlainFigureConfig}EdgeType"/>
 *         &lt;/sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="face" type="{tests:4DViewer:PlainFigureConfig}FaceType"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FigureType", propOrder = {
    "precision",
    "vertices",
    "edges",
    "faces"
})
public class FigureType {

    protected Double precision;
    @XmlElement(name = "vertex")
    protected List<VertexType> vertices;
    @XmlElement(name = "edge")
    protected List<EdgeType> edges;
    @XmlElement(name = "face")
    protected List<FaceType> faces;

    /**
     * Gets the value of the precision property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPrecision(Double value) {
        this.precision = value;
    }

    /**
     * Gets the value of the vertices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vertices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVertices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VertexType }
     * 
     * 
     */
    public List<VertexType> getVertices() {
        if (vertices == null) {
            vertices = new ArrayList<VertexType>();
        }
        return this.vertices;
    }

    /**
     * Gets the value of the edges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the edges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEdges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EdgeType }
     * 
     * 
     */
    public List<EdgeType> getEdges() {
        if (edges == null) {
            edges = new ArrayList<EdgeType>();
        }
        return this.edges;
    }

    /**
     * Gets the value of the faces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the faces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFaces().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FaceType }
     * 
     * 
     */
    public List<FaceType> getFaces() {
        if (faces == null) {
            faces = new ArrayList<FaceType>();
        }
        return this.faces;
    }

}
