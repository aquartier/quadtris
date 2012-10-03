//A 03-10-2555 00:44

package com.dekdroid.quadtris;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.graphics.Point;

import com.dekdroid.quadtris.SceneManager.SceneType;
import com.dekdroid.quadtris.Shape.Movement;

/**
 * 
 * @modify LeoNaiDaS
 * @see http://stuartmct.co.uk/2012/07/16/andengine-scenes-and-scene-management/
 * 
 */

public class Quadtris extends BaseGameActivity { // Main Activity
	static final int CAMERA_WIDTH = 480;
	static final int CAMERA_HEIGHT = 800;
	static final int BOARD_WIDTH = 17;
	static final int BOARD_HEIGHT = 17;
	private Camera mCamera;
	private SceneManager sceneManager;
	private BoardTable boardTable;
	private int[][] map;

	private final int DELAY_START = 1000;
	private final int DELAY_STEP = 100;
	private final int DELAY_FINAL = 300;
	private final int DELAY_DEBUG = 100;

	private int delay;
	private boolean running;
	Shape tetromino;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
				mCamera);
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {

		map = new int[17][17];

		// Test generate relative array here. not for game, just for test
		// Please set relative position
		int i, j;
		for (i = 0; i < 17; i++) {
			for (j = 0; j < 17; j++) {
				if (i <= j)
					map[i][j] = 1;
				else
					map[i][j] = 0;
			}
		}

		boardTable = new BoardTable(map);

		sceneManager = new SceneManager(this, mEngine, mCamera, boardTable);
		sceneManager.loadSplashSceneResources();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager
				.createSplashScene());
	}

	// Method to choose screen to display
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(2f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						sceneManager.loadGameSceneResources();
						sceneManager.createGameScenes();
						sceneManager.setCurrentScene(SceneType.MAINGAME);
					}
				}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();

		// Jeep code here. Do everything you want such as create thread, game
		// logic or update blockObj.

		resetMap();
		updateBoard();

		delay = DELAY_DEBUG;
		running = true;

		// TODO Game Control here
//		while (running) {
//			tetromino = new Shape();
//			while (movable()) {
//				delay_ms(delay);
//				moveToNext();
//			}
//			delay_ms(delay);
//			placeToMap();
//			boardTable.setBoard(map);
//		}
		int i=0;
		while(i<17*17){
			map[i / 17][(i++) % 17] = 1;
			updateBoard();
			delay_ms(delay);
		}

	}

	// Jeep's methods
	public void resetMap() {
		for (int i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				map[i][j] = 0;
			}
		}
		map[Quadtris.BOARD_HEIGHT / 2][Quadtris.BOARD_WIDTH / 2] = 1;
	}

	public void updateBoard() {
		boardTable = new BoardTable(map);
		sceneManager.setBoardTable(boardTable);
	}

	public void setMap(int[][] map) {
		this.map = map;
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

	private void placeToMap() {
		for (int i = 0; i < 4; i++) {
			map[tetromino.getRPos().y + tetromino.y(i)][tetromino.getRPos().x
					+ tetromino.x(i)] = 1;
		}
	}

	private void delay_ms(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void moveToNext() {
		tetromino.setRPos(nextPoint(tetromino.getRPos(), tetromino.getDir()));
	}
}
