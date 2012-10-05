//WTF

package com.dekdroid.quadtris;

import android.graphics.Point;

/**
 * 
 * @author LeoNaiDaS
 * 
 */

public class BoardTable {
	static final int BLOCK_WIDTH = 20;
	static final int BLOCK_HEIGHT = 20;
	private int[][] board; // Jeep attribute
	private int[][] shape; // shapeArray
	private int[][] realBoardPosX, realBoardPosY; // Generate from board
	private int[][] realShapePosX, realShapePosY;
	private Shape tetromino;

	BoardTable(int[][] table, Shape tet) {
		board = table;
		tetromino = tet;
		shape = tet.getShapeArray();
		realBoardPosX = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];
		realBoardPosY = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];
		realShapePosX = new int[4][4];
		realShapePosY = new int[4][4];
		genRealPos();
	}

	private void genRealPos() {
		// 1 block is 20x20 pixels
		int posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH
				* (Quadtris.BOARD_WIDTH / 2);
		int posY = Quadtris.CAMERA_HEIGHT / 2 - BLOCK_HEIGHT / 2 - BLOCK_HEIGHT
				* (Quadtris.BOARD_HEIGHT / 2);
		for (int i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				if (board[i][j] == 1) {
					realBoardPosX[i][j] = posX;
					realBoardPosY[i][j] = posY;
				} else {
					realBoardPosX[i][j] = -1;
					realBoardPosY[i][j] = -1;
				}
				posX += BLOCK_WIDTH;
			}
			posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH
					* (Quadtris.BOARD_WIDTH / 2);
			posY += BLOCK_HEIGHT;
		}
		int x = tetromino.getRPos().x;
		int y = tetromino.getRPos().y;
		posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH
				* (Quadtris.BOARD_WIDTH / 2) + (x * BLOCK_WIDTH);
		posY = Quadtris.CAMERA_HEIGHT / 2 - BLOCK_HEIGHT / 2 - BLOCK_HEIGHT
				* (Quadtris.BOARD_HEIGHT / 2) + (y * BLOCK_WIDTH);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (shape[i][j] == 1) {
					realShapePosX[i][j] = posX;
					realShapePosY[i][j] = posY;
				} else {
					realShapePosX[i][j] = -1;
					realShapePosY[i][j] = -1;
				}
				posX += BLOCK_WIDTH;
			}
			posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH
					* (Quadtris.BOARD_WIDTH / 2) + (x * BLOCK_WIDTH);
			posY += BLOCK_HEIGHT;
		}

	}

	public void setBoardAndTetromino(int[][] table, Shape tetromino) {
		board = table;
		this.tetromino = tetromino;
		shape = tetromino.getShapeArray();
		genRealPos();
	}

	public Shape getTetromino() {
		return this.tetromino;
	}

	public int[][] getRealBoardPosX() {
		return this.realBoardPosX;
	}

	public int[][] getRealBoardPosY() {
		return this.realBoardPosY;
	}

	public int[][] getRealShapePosX() {
		return this.realShapePosX;
	}

	public int[][] getRealShapePosY() {
		return this.realShapePosY;
	}
}
