package com.creditsuisse.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Path2D;

public class GraphicsUtil {

	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_LEFT = 4;
	public static final int BOTTOM_RIGHT = 8;
	public static final int ALL_CORNERS = TOP_LEFT + TOP_RIGHT + BOTTOM_LEFT + BOTTOM_RIGHT;

	public static void drawRoundRect(Graphics g, Color fillColor, Color borderColor, int x, int y, int width, int height, int radius, int cornerMask) {
		radius += radius % 2; // so we don't have to deal with rounding issues
								// for odd numbers
		int radiusHalf = radius / 2;
		width--;
		height--;
		if (fillColor != null) {
			g.setColor(fillColor);
			// og.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
			if ((cornerMask & TOP_LEFT) > 0) {
				g.fillArc(x, y, radius, radius, 90, 90);
			} else {
				g.fillRect(x, y, radiusHalf, radiusHalf);
			}
			if ((cornerMask & TOP_RIGHT) > 0) {
				g.fillArc(x + width - radius, y, radius, radius, 0, 90);
			} else {
				g.fillRect(x + width - radiusHalf, y, radiusHalf, radiusHalf);
			}
			if ((cornerMask & BOTTOM_RIGHT) > 0) {
				g.fillArc(x + width - radius, y + height - radius, radius, radius, 270, 90);
			} else {
				g.fillRect(x + width - radiusHalf, y + height - radiusHalf, radiusHalf, radiusHalf);
			}
			if ((cornerMask & BOTTOM_LEFT) > 0) {
				g.fillArc(x, y + height - radius, radius, radius, 180, 90);
			} else {
				g.fillRect(x, y + height - radiusHalf, radiusHalf, radiusHalf);
			}

			g.fillRect(x + radiusHalf, y, width - radius, radiusHalf);
			g.fillRect(x + radiusHalf, y + height - radiusHalf, width - radius, radiusHalf);
			g.fillRect(x, y + radiusHalf, radiusHalf, height - radius);
			g.fillRect(x + width - radiusHalf, y + radiusHalf, radiusHalf, height - radius);
			g.fillRect(x + radiusHalf, y + radiusHalf, width - radius, height - radius);
		}
		if (borderColor != null && !borderColor.equals(fillColor)) {
			g.setColor(borderColor);

			// XXX: there are problems with this when using semi-transparent
			// colors + borderSize > 1
			// XXX: this could be changed to to use ONE draw action using
			// drawShape with GeneralPath.curveTo()
			// XXX: this then could also be used to FILL the shape (see above)
			if ((cornerMask & TOP_LEFT) > 0) {
				g.drawArc(x, y, radius, radius, 90, 90);
			} else {
				g.drawLine(x, y, x + radiusHalf, y);
				g.drawLine(x, y, x, y + radiusHalf);
			}
			if ((cornerMask & TOP_RIGHT) > 0) {
				g.drawArc(x + width - radius, y, radius, radius, 0, 90);
			} else {
				g.drawLine(x + width - radiusHalf, y, x + width, y);
				g.drawLine(x + width, y, x + width, y + radiusHalf);
			}
			if ((cornerMask & BOTTOM_RIGHT) > 0) {
				g.drawArc(x + width - radius, y + height - radius, radius, radius, 270, 90);
			} else {
				g.drawLine(x + width - radiusHalf, y + height, x + width, y + height);
				g.drawLine(x + width, y + height - radiusHalf, x + width, y + height);
			}
			if ((cornerMask & BOTTOM_LEFT) > 0) {
				g.drawArc(x, y + height - radius, radius, radius, 180, 90);
			} else {
				g.drawLine(x, y + height, x + radiusHalf, y + height);
				g.drawLine(x, y + height - radiusHalf, x, y + height);
			}

			g.drawLine(x + radiusHalf, y, x + width - radiusHalf, y); // top
			g.drawLine(x + width, y + radiusHalf, x + width, y + height - radiusHalf); // right
			g.drawLine(x + radiusHalf, y + height, x + width - radiusHalf, y + height); // bottom
			g.drawLine(x, y + radiusHalf, x, y + height - radiusHalf); // left
		}
	}
	
	
	public static Path2D.Float createRoundedCorner(float startX, float startY, float endX, float endY, float cornerX, float cornerY){
		Path2D.Float path = new Path2D.Float();
		path.moveTo(startX, startY);
		path.curveTo((startX + cornerX) / 2, (startY + cornerY) / 2, (endX + cornerX) / 2, (endY + cornerY) / 2, endX, endY);
		return path;
	}
}
