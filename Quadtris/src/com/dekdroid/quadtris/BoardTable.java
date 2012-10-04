//WTF

package com.dekdroid.quadtris;

/**
 * 
 * @author LeoNaiDaS
 * 
 */

public class BoardTable {
	static final int BLOCK_WIDTH = 20;
	static final int BLOCK_HEIGHT = 20;
	private int[][] board; // Jeep attribute
	private int[][] realPosX, realPosY; // Generate from board

	BoardTable(int[][] table) {
		board = table;
		realPosX = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];
		realPosY = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];
		genRealPos();
	}

	private void genRealPos() {
		// 1 block is 20x20 pixels
		int i, j;
		int posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH * BLOCK_WIDTH/2;
		int posY = Quadtris.CAMERA_HEIGHT / 2 - BLOCK_HEIGHT / 2 - BLOCK_HEIGHT * BLOCK_HEIGHT/2;
		for (i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				if (board[i][j] == 1) {
					realPosX[i][j] = posX;
					realPosY[i][j] = posY;
				} else {
					realPosX[i][j] = -1;
					realPosY[i][j] = -1;
				}
				posX += BLOCK_WIDTH;
			}
			posX = Quadtris.CAMERA_WIDTH / 2 - BLOCK_WIDTH / 2 - BLOCK_WIDTH * BLOCK_WIDTH/2;
			posY += BLOCK_HEIGHT;
		}
	}

	public void setBoard(int[][] table) {
		board = table;
		genRealPos();
	}

	public void setRealPosX(int[][] posX) {
		realPosX = posX;
	}

	public void setRealPosY(int[][] posY) {
		realPosY = posY;
	}

	public int[][] getBoard() {
		return this.board;
	}

	public int[][] getRealPosX() {
		return this.realPosX;
	}

	public int[][] getRealPosY() {
		return this.realPosY;
	}
}
