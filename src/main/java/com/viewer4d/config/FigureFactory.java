package com.viewer4d.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.viewer4d.config.model.EdgeType;
import com.viewer4d.config.model.FaceType;
import com.viewer4d.config.model.FigureType;
import com.viewer4d.config.model.VertexType;
import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Face;
import com.viewer4d.geometry.FigureMovable;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.impl.FigureMovableImpl;
import com.viewer4d.geometry.simple.Pointable;

public final class FigureFactory {

    private static final String MODEL_SUB_PACKAGE = ".model";

    private static final FigureFactory instance;
    
    static {
        try {
            instance = new FigureFactory();
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }
    
    public static FigureFactory getInstance() {
        return instance;
    }
    
    // Instance members
    private JAXBContext jaxbContext;
    
    private FigureFactory() throws JAXBException {
        String modelPackage = this.getClass().getPackage().getName() + MODEL_SUB_PACKAGE;
        jaxbContext = JAXBContext.newInstance(modelPackage);
    }

    public FigureMovable loadFromResources(String figureDefinition) throws Exception {
        InputStream is = getClass().getResourceAsStream(figureDefinition);
        return createFigure(is);
    }
    
    private FigureMovableImpl createFigure(InputStream is) throws Exception {
        FigureType figureType = unmarshall(is);
        
        Map<String, Vertex> vertices = new HashMap<String, Vertex>();
        for (VertexType vertexType : figureType.getVertices()) {
            vertices.put(vertexType.getName(), createVertexFrom(vertexType));
        }
        
        Map<String, Edge> edges = new HashMap<String, Edge>();
        Map<Vertex, Set<Edge>> verticesEdges = new HashMap<Vertex, Set<Edge>>();
        for (EdgeType edgeType : figureType.getEdges()) {
            Edge edge = createEdgeFrom(edgeType, vertices);
            
            putEdgeIntoVerticesEdgesMap(verticesEdges, edge.getA(), edge);
            putEdgeIntoVerticesEdgesMap(verticesEdges, edge.getB(), edge);
            edges.put(edgeType.getName(), edge);
        }
        
        Map<String, Face> faces = new HashMap<String, Face>();
        for (FaceType faceType : figureType.getFaces()) {
            faces.put(faceType.getName(), createFaceFrom(faceType, edges, vertices, verticesEdges));
        }
        
        Double givenPrecision = figureType.getPrecision();
        double precision = (givenPrecision != null) ? givenPrecision : Pointable.PRECISION;
        
        return new FigureMovableImpl(
                new HashSet<Face>(faces.values()), 
                new HashSet<Edge>(edges.values()),
                precision);
    }
    
    private void putEdgeIntoVerticesEdgesMap(Map<Vertex, Set<Edge>> verticesEdges, Vertex vertex, Edge edge) {
        Set<Edge> edgeSet = verticesEdges.get(vertex);
        if (edgeSet == null) {
            edgeSet = new HashSet<Edge>();
            verticesEdges.put(vertex, edgeSet);
        }
        edgeSet.add(edge);
    }

    private Vertex createVertexFrom(VertexType vertexType) {
        double[] coords = {0, 0, 0, 0};
        String[] arrCoords = vertexType.getCoords().split(",");
        for (int i = 0; i < arrCoords.length; i++) {
            coords[i] = Double.parseDouble(arrCoords[i].trim());
        }
        return new Vertex(coords);
    }

    private Edge createEdgeFrom(EdgeType edgeType, Map<String, Vertex> vertices) {
        Vertex a = vertices.get(edgeType.getV1().trim());
        Vertex b = vertices.get(edgeType.getV2().trim());
        if (a == null || b == null) {
            throw new IllegalArgumentException("No such vertex defined.");
        }
        return new Edge(a, b);
    }

    private Face createFaceFrom(FaceType faceType, Map<String, Edge> edgesMap, 
            Map<String, Vertex> vertices, Map<Vertex, Set<Edge>> verticesEdges) {
        Set<Edge> edges = new HashSet<Edge>();
        String edgesAttr = faceType.getEdges();
        String verticesAttr = faceType.getVertices();
        
        if (edgesAttr != null) {
            String[] arrEdges = edgesAttr.split(",");
            
            for (int i = 0; i < arrEdges.length; i++) {
                String adgeName = arrEdges[i].trim();
                Edge edge = edgesMap.get(adgeName);
                if (edge == null) {
                    throw new IllegalArgumentException("No such edge defined - " + adgeName + ".");
                }
                edges.add(edge);
            }
        } else if (verticesAttr != null) {
            String[] arrVertexNames = verticesAttr.split(",");
            Vertex[] arrVertices = new Vertex[arrVertexNames.length];
            
            for (int i = 0; i < arrVertexNames.length; i++) {
                String vertexName = arrVertexNames[i].trim();
                Vertex vertex = vertices.get(vertexName);
                if (vertex == null) {
                    throw new IllegalArgumentException("No such vertex defined - " + vertexName + ".");
                }
                arrVertices[i] = vertex;
            }
            
            for (int i = 0; i < arrVertices.length; i++) {
                Vertex v1 = arrVertices[i];
                
                for (int j = (i + 1); j < arrVertices.length; j++) {
                    Vertex v2 = arrVertices[j];
                    Set<Edge> edgeV1 = new HashSet<Edge>(verticesEdges.get(v1));
                    edgeV1.retainAll(verticesEdges.get(v2));
                    
                    int size = edgeV1.size();
                    if (size == 1) {
                        edges.add(edgeV1.iterator().next());
                    } else if (size > 1) {
                        throw new IllegalArgumentException("Between two vertices cannot be more than one edge.");
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Either 'edges' or 'vertices' attribute should be defined.");
        }
        return new Face(edges);
    }

    protected FigureType unmarshall(InputStream is) throws JAXBException, SAXException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Schema schema = getFigureSchema();
        unmarshaller.setSchema(schema);
        Object result = unmarshaller.unmarshal(is);
        return (FigureType) JAXBElement.class.cast(result).getValue();
    }

    private Schema getFigureSchema() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        InputStream schemaAsStream = getClass().getResourceAsStream("/PlainFigureConfig.xsd");
        Source schemaSource = new StreamSource(schemaAsStream);
        return schemaFactory.newSchema(schemaSource);
    }
}
