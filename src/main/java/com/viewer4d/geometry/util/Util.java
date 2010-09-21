package com.viewer4d.geometry.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.viewer4d.geometry.Edge;
import com.viewer4d.geometry.Face;
import com.viewer4d.geometry.Vertex;
import com.viewer4d.geometry.simple.Plane;
import com.viewer4d.geometry.simple.Pointable;
import com.viewer4d.geometry.simple.Space;

public class Util {
    
    public static Space create3dSpaceFrom(Face face1, Face face2, double precision) {
        if (face1 == face2) {
            throw new IllegalArgumentException("It is impossible to create a space from the same faces.");
        }
        Set<Edge> edges1 = face1.getEdges();
        Set<Edge> edges2 = face2.getEdges();
        
        Set<Edge> edges1c = new HashSet<Edge>(edges1);
        edges1c.retainAll(edges2);
        
        Iterator<Edge> it1 = edges1.iterator();
        Iterator<Edge> it2 = edges2.iterator();
        Iterator<Edge> it1c = edges1c.iterator();
        if (!it1.hasNext() || !it2.hasNext() || !it1c.hasNext()) {
            throw new IllegalArgumentException(
                    "It is impossible to create a space from empty faces or the faces do not intersect.");
        }
        
        Edge edge1c = it1c.next();
        Edge edge1 = it1.next();
        Edge edge2 = it2.next();
        while (edge1c.equals(edge1)) {
            edge1 = it1.next();
        }
        while (edge1c.equals(edge2)) {
            edge2 = it2.next();
        }
        
        Vertex ac = edge1c.getA();
        Vertex bc = edge1c.getB();
        
        Vertex v1 = edge1.getA();
        if (v1 == ac || v1 == bc) {
            v1 = edge1.getB();
        }
        Vertex v2 = edge2.getA();
        if (v2 == ac || v2 == bc) {
            v2 = edge2.getB();
        }
        return new Space(ac, bc, v1, v2, precision);
    }

    public static Plane create2dPlaneFrom(Face face, double precision) {
        Iterator<Edge> edgeIt = face.getEdges().iterator();
        Edge edge1 = edgeIt.next();
        Edge edge2 = edgeIt.next();
        
        Pointable p1 = edge1.getA();
        Pointable p2 = edge1.getB();
        Pointable p3 = edge2.getA();
        if (p3 == p1 || p3 == p2) {
            p3 = edge2.getB();
        }
        
        return new Plane(p1, p2, p3, precision);
    }

    public static boolean isLyingOn(Face face, Space space) {
        if (face == null) {
            throw new IllegalArgumentException("Null face.");
        }
        if (space == null) {
            return false;
        }
        for (Edge edge : face.getEdges()) {
            if (!space.liesOn(edge.getA()) || !space.liesOn(edge.getB())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLyingOn(Face face, Plane plane) {
        if (face == null) {
            throw new IllegalArgumentException("Null face.");
        }
        if (plane == null) {
            return false;
        }
        for (Edge edge : face.getEdges()) {
            if (!plane.liesOn(edge.getA()) || !plane.liesOn(edge.getB())) {
                return false;
            }
        }
        return true;
    }

}
