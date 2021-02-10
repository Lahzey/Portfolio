package com.creditsuisse.graphics;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HollowRectangle implements Shape {
	
	private Rectangle bounds;
	private Insets size;
	
	private Rectangle innerRectangle;
	private Path2D path;
	
	public HollowRectangle(Rectangle bounds, Insets size){
		this.bounds = new Rectangle(bounds);
		this.size = (Insets) size.clone();
		
		if(size.left + size.right > bounds.width){
			throw new IllegalArgumentException("Given left and right sizes exceed the bounds.");
		}else if(size.top + size.bottom > bounds.height){
			throw new IllegalArgumentException("Given top and bottom sizes exceed the bounds.");
		}
		
		innerRectangle = new Rectangle(bounds.x + size.left, bounds.y + size.top, bounds.width - size.left - size.right, bounds.height - size.top - size.bottom);
		
		path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
		path.append(bounds, false);
		path.append(innerRectangle, false);
	}
	
	

	public Insets getSize() {
		return (Insets) size.clone();
	}

	@Override
	public Rectangle getBounds() {
		return bounds.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return bounds.getBounds2D();
	}

	@Override
	public boolean contains(double x, double y) {
		if(bounds.contains(x, y)){
			return !innerRectangle.contains(x, y);
		}else{
			return false;
		}
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		if(bounds.intersects(x, y, w, h)){
			return !innerRectangle.intersects(x, y, w, h);
		}else{
			return false;
		}
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		if(bounds.intersects(r)){
			return !innerRectangle.intersects(r);
		}else{
			return false;
		}
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		if(bounds.contains(x, y, w, h)){
			return !innerRectangle.contains(x, y, w, h);
		}else{
			return false;
		}
	}

	@Override
	public boolean contains(Rectangle2D r) {
		if(bounds.contains(r)){
			return !innerRectangle.contains(r);
		}else{
			return false;
		}
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return path.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return path.getPathIterator(at, flatness);
	}

}
