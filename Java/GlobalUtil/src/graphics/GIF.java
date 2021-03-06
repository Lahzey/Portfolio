package graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.SwingConstants;

public class GIF {

	private BufferedImage[] frames;
	private long frameTime; // is a long to signal that it is counted in milliseconds
	private int width;
	private int height;
	
	private long elapsedTime = 0;
	
	public GIF(BufferedImage frameSheet, int columns, int rows, long frameTime) {
		if (columns <= 0) {
			throw new IllegalArgumentException("Columns must be 1 or more.");
		} else if (rows <= 0) {
			throw new IllegalArgumentException("Rows must be 1 or more.");
		} else if (rows <= 0) {
			throw new IllegalArgumentException("Frame Time must be 1 or more");
		}
		
		this.frameTime = frameTime;
		
		int sheetWidth = frameSheet.getWidth(null);
		int sheetHeight = frameSheet.getHeight(null);

		width = sheetWidth / columns;
		if (width * columns != sheetWidth) {
			throw new IllegalArgumentException("The frame sheet (" + sheetWidth + "x" + sheetHeight + ") cannot be split into " + columns + " equally sized columns.");
		}
		
		height = sheetHeight / rows;
		if (height * rows != sheetHeight) {
			throw new IllegalArgumentException("The frame sheet (" + sheetWidth + "x" + sheetHeight + ") cannot be split into " + rows + " equally sized rows.");
		}
		
		frames = new BufferedImage[columns * rows];
		int i = 0;
		for (int y = 0; y < sheetHeight; y += height) {
			for (int x = 0; x < sheetWidth; x += width) {
				frames[i] = frameSheet.getSubimage(x, y, width, height);
				i++;
			}
		}
	}
	
	public GIF(GIF gif) {
		this.frames = Arrays.copyOf(gif.frames, gif.frames.length);
		this.frameTime = gif.frameTime;
		this.width = gif.width;
		this.height = gif.height;
	}
	
	public BufferedImage getFrame(int index) {
		return frames[index];
	}
	
	public void setFrame(BufferedImage frame, int index) {
		if (frame == null) {
			throw new IllegalArgumentException("Cannot set a frame to null");
		} else if (frame.getWidth() != width || frame.getHeight() != height) {
			throw new IllegalArgumentException("Cannot set a frame with different dimensions.");
		}
		
		frames[index] = frame;
	}
	
	public int getFrameCount() {
		return frames.length;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void resize(int width, int height, boolean stretch) {
		float widthRatio = this.width / (float) width;
		float heightRatio = this.height / (float) height;
		for (int i = 0; i < frames.length; i++) {
			Image frame = frames[i];
			if (stretch) {
				frame = frame.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			} else {
				if (widthRatio > heightRatio) {
					frame = ImageUtil.getWidthScaledImage(frame, width);
				} else {
					frame = ImageUtil.getHeightScaledImage(frame, width);
				}
			}
			frame = ImageUtil.changeCanvasSize(frame, width, height, SwingConstants.CENTER, SwingConstants.CENTER);
			frames[i] = ImageUtil.toBufferedImage(frame);
		}
		this.height = height;
		this.width = width;
	}
	
	public void edit(Editor editor) {
		for (int i = 0; i < frames.length; i++) {
			frames[i] = ImageUtil.toBufferedImage(editor.edit(frames[i]));
		}
	}

	public void update(long deltaTime) {
		elapsedTime += deltaTime;
	}
	
	public BufferedImage currentFrame(long deltaTime) {
		update(deltaTime);
		return currentFrame();
	}
	
	public BufferedImage currentFrame() {
		int frameIndex = (int) ((elapsedTime / frameTime) % frames.length);
		return frames[frameIndex];
	}
	
	public static interface Editor {
		public Image edit(BufferedImage frame);
	}
	
}
