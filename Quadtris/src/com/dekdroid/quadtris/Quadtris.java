//A 05-10-2555 00:32

package com.dekdroid.quadtris;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import com.dekdroid.quadtris.SceneManager.SceneType;

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
	int[][] map;

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
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 17; j++) {
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
	public void onPopulateScene(Scene pScene,OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(2f,new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						sceneManager.loadGameSceneResources();
						sceneManager.createGameScenes();						
						sceneManager.setCurrentScene(SceneType.MAINGAME);						
					}
				}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		// Jeep code here. Do everything you want such as create thread, game	

	}

}
