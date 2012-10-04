package com.dekdroid.quadtris;

import android.R.id;
import android.graphics.Point;

import com.dekdroid.quadtris.Shape.Movement;
import com.dekdroid.quadtris.Shape.Tetrominoes;

/**
 * The class that control all logic in game including input control
 * 
 * @author jeep
 * 
 */
public class GameControl implements Runnable {
	private final int DELAY_START = 1000;
	private final int DELAY_STEP = 100;
	private final int DELAY_FINAL = 300;

	private int delay;
	private boolean running;

	int[][] map;

	Shape tetromino;

	public GameControl(int[][] map) {
		setMap(map);
		resetMap();
		running = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (running) {
			while (movable()) {
				delay_ms(delay);
				move();
			}
			delay_ms(delay);
			place();
		}
	}

	private void place() {
		for (int i = 0; i < 4; i++) {
			map[tetromino.getRPos().y + tetromino.y(i)][tetromino.getRPos().x
					+ tetromino.x(i)] = 1;
		}
	}

	/**
	 * set internal map to compute all game
	 * 
	 * @param map
	 *            a 0/1 map that want to be internal map
	 * @author jeep
	 */
	public void setMap(int[][] map) {
		this.map = map;
	}

	/**
	 * reset internal map to zero metric
	 * 
	 * @author jeep
	 * 
	 */
	public void resetMap() {
		for (int i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				map[i][j] = 0;
			}
		}
		map[Quadtris.BOARD_HEIGHT / 2][Quadtris.BOARD_WIDTH / 2] = 1;
	}

	private Point nextPoint(Point curr, Movement direction) {
		switch (direction) {
		case Up:
			return new Point(curr.x, curr.y - 1);
		case Down:
			return new Point(curr.x, curr.y + 1);
		case Left:
			return new Point(curr.x - 1, curr.y);
		case Right:
			return new Point(curr.x + 1, curr.y);
		}
		return null;
	}

	private boolean movable() {
		for (int i = 0; i < 4; i++) {
			Point next = nextPoint(tetromino.getRPos(), tetromino.getDir());

			if (next.x < 0 || next.x > Quadtris.BOARD_WIDTH)
				return false;
			if (next.y < 0 || next.y > Quadtris.BOARD_HEIGHT)
				return false;
			if (map[next.y][next.x] == 1)
				return false;
		}
		return true;
	}

	private void move() {
		tetromino.setRPos(nextPoint(tetromino.getRPos(), tetromino.getDir()));
	}

	private void delay_ms(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
