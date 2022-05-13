package com.aiyouwei.drk.shelf.model;

import android.graphics.Rect;

public class DrawInfo {
    private Rect rect;
    private int color;
    private String name;

    public DrawInfo(Rect rect, int color, String name) {
        this.rect = rect;
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rect getRect() {
        return rect;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
