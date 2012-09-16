package com.dekdroid.quadtris;

/**
 * 
 * @author LeoNaiDaS
 * 
 */

public class BoardTable {
	private final int CAMERA_WIDTH = 480;
	private final int CAMERA_HEIGHT = 800;
	private final int BLOCK_WIDTH = 20;
	private final int BLOCK_HEIGHT = 20;
	private int[][] board; // Jeep attribute
	private int[][] realPosX, realPosY; // Generate from relativePos

	BoardTable(int[][] table) {
		board = table;
		realPosX = new int[17][17];
		realPosY = new int[17][17];
		genRealPos();
	}

	private void genRealPos() {
		// 1 block is 20x20 pixels
		int i, j;
		int posX = CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH * 8;
		int posY = CAMERA_HEIGHT / 2 - BLOCK_HEIGHT / 2 - BLOCK_HEIGHT *8;
		for (i = 0; i < 17; i++) {
			for (j = 0; j < 17; j++) {
				if (board[i][j] == 1) {
					realPosX[i][j] = posX;
					realPosY[i][j] = posY;
				} else {
					realPosX[i][j] = -1;
					realPosY[i][j] = -1;
				}
				posX += BLOCK_WIDTH;
			}
			posX = CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH * 8;
			posY += BLOCK_WIDTH;
		}
	}

	public void setBoard(int[][] table) {
		board = table;
	}

	public void setRealPosX(int[][] posX) {
		realPosX = posX;
	}

	public void setRealPosY(int[][] posY) {
		realPosY = posY;
	}

	public int[][] getRelativePos() {
		return this.board;
	}

	public int[][] getRealPosX() {
		return this.realPosX;
	}

	public int[][] getRealPosY() {
		return this.realPosY;
	}
}
