package com.dekdroid.quadtris;

import java.util.Random;

import android.graphics.Point;
import android.util.Log;

public class Shape {

	enum Tetrominoes {
		NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
	};

	enum Movement {
		Up, Left, Right, Down
	};

	private Tetrominoes pieceShape;
	private int coords[][];
	private int[][][] coordsTable;
	private Point rPos;
	private Movement dir;

	public Shape() {

		coords = new int[4][2];
		// setShape(Tetrominoes.NoShape);
		setRandomShape();
	}
	public Shape(Shape tetromino) {
		coords = new int[4][2];
		this.setShape(tetromino.getShape());
		this.setRPos(tetromino.getRPos());
	}

	public void setShape(Tetrominoes shape) {

		coordsTable = new int[][][] {
				{ { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } },
				{ { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } },
				{ { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } },
				{ { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } },
				{ { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } },
				{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } },
				{ { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
				{ { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; ++j) {
				coords[i][j] = coordsTable[shape.ordinal()][i][j];
			}
		}
		pieceShape = shape;

	}

	private void setX(int index, int x) {
		coords[index][0] = x;
	}

	private int getX(int index) {
		return coords[index][0];
	}

	private void setY(int index, int y) {
		coords[index][1] = y;
	}

	private int getY(int index) {
		return coords[index][1];
	}

	public int x(int index) {
		return coords[index][0];
	}

	public int y(int index) {
		return coords[index][1];
	}

	public Tetrominoes getShape() {
		return pieceShape;
	}

	public void setRandomShape() {
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;
		Tetrominoes[] values = Tetrominoes.values();
		setShape(values[x]);
		int y = Math.abs(r.nextInt()) % 4;

		switch (y) {
		case 0:
			dir = Movement.Up;
			setRPos(new Point(Quadtris.BOARD_WIDTH / 2, Quadtris.BOARD_HEIGHT
					- 4 + 3 - (maxY() - minY())));
			break;
		case 1:
			dir = Movement.Down;
			setRPos(new Point(Quadtris.BOARD_WIDTH / 2, 0));
			break;
		case 2:
			dir = Movement.Right;
			setRPos(new Point(0, Quadtris.BOARD_HEIGHT / 2));
			break;
		case 3:
			dir = Movement.Left;
			setRPos(new Point(Quadtris.BOARD_WIDTH - 4 + 3 - (maxX() - minX()),
					Quadtris.BOARD_HEIGHT / 2));
			break;
		}
	}

	public int minX() {
		int m = coords[0][0];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][0]);
		}
		return m;
	}

	public int minY() {
		int m = coords[0][1];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][1]);
		}
		return m;
	}

	public int maxX() {
		int m = coords[0][0];
		for (int i = 0; i < 4; i++) {
			m = Math.max(m, coords[i][0]);
		}
		return m;
	}

	public int maxY() {
		int m = coords[0][1];
		for (int i = 0; i < 4; i++) {
			m = Math.max(m, coords[i][1]);
		}
		return m;
	}

	public void rotateLeft() {
		if (pieceShape == Tetrominoes.SquareShape)
			return;

		for (int i = 0; i < 4; ++i) {
			int tmp = -x(i);
			setX(i, y(i));
			setY(i, tmp);
		}
	}

	public void rotateRight() {
		if (pieceShape == Tetrominoes.SquareShape)
			return;

		for (int i = 0; i < 4; ++i) {
			int tmp = x(i);
			setX(i, -y(i));
			setY(i, tmp);
		}
	}

	public int[][] getShapeArray() {
		int[][] shapeArray = new int[4][4];
		for (int i = 0; i < 4; i++) {
			shapeArray[getY(i) - minY()][getX(i) - minX()] = 1;
		}
		return shapeArray;
	}

	/**
	 * set relative position
	 * 
	 * @param relativePosition
	 *            point of relative position
	 */
	public void setRPos(Point relativePosition) {
		this.rPos = relativePosition;
	}

	/**
	 * get relative position
	 * 
	 * @return point of current relative position
	 */
	public Point getRPos() {
		return rPos;
	}

	/**
	 * get direction of tetromino
	 * 
	 * @return enum of movement
	 */
	public Movement getDir() {
		return dir;
	}
}