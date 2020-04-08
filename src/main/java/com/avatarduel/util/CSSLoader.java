package com.avatarduel.util;

import javafx.scene.Node;

public class CSSLoader {
    private CSSLoader(){
        throw new AssertionError("This is a utility class");
    }

    public static void addClass(Node node, String newClass){
        node.getStyleClass().add(newClass);
    }

    public static void clear(Node node){
        node.getStyleClass().clear();
    }

    public static void setClass(Node node, String newClass){
        CSSLoader.clear(node);
        CSSLoader.addClass(node, newClass);
    }
}
