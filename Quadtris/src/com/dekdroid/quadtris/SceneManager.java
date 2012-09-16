package com.dekdroid.quadtris;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * 
 * @author LeoNaiDaS
 * @see	http://stuartmct.co.uk/2012/07/16/andengine-working-with-rectangles/
 *
 */

public class SceneManager {

	private SceneType currentScene;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private BitmapTextureAtlas splashTextureAtlas;
	private TextureRegion splashTextureRegion;
	private Scene splashScene;
	private Scene mainGameScene;
	private BoardTable boardTable;
	private Rectangle[][] myRectangle;
	private int[][] mainBlockPosX,mainBlockPosY;

	public enum SceneType{
		SPLASH,
		MAINGAME
	}

	public SceneManager(BaseGameActivity activity, Engine engine, Camera camera,BoardTable boardTable) {
		this.boardTable = boardTable;
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
	}

	//Method loads all of the splash scene resources
	public void loadSplashSceneResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	//Method loads all of the resources for the game scenes such as sprite
	public void loadGameSceneResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	}

	//Method creates the Splash Scene
	public Scene createSplashScene() {
		//Create the Splash Scene and set background color to red and add the splash logo.
		splashScene = new Scene();
		splashScene.setBackground(new Background(0, 0, 0));
		Sprite splash = new Sprite(0, 0, splashTextureRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.0f);
		splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f, (camera.getHeight() - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);

		return splashScene;
	}

	//Method creates all of the Game Scenes
	public void createGameScenes() {
		//Create the Main Game Scene and set background color to blue
		mainGameScene = new Scene();
		mainGameScene.setBackground(new Background(1, 1, 1));
		
		myRectangle = new Rectangle[17][17];
		mainBlockPosX = boardTable.getRealPosX();
		mainBlockPosY = boardTable.getRealPosY();
		
		Entity rectangleGroup = new Entity(0, 0);
		
		int i,j;
		for(i=0;i<17;i++){
			for(j=0;j<17;j++){
				if(mainBlockPosX[i][j] != -1){
					myRectangle[i][j] = new Rectangle(mainBlockPosX[i][j], mainBlockPosY[i][j], 20, 20, activity.getVertexBufferObjectManager());
					myRectangle[i][j].setColor((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
					rectangleGroup.attachChild(myRectangle[i][j]);
				}
			}
		}
		
		rectangleGroup.setPosition(0,0);		
		mainGameScene.attachChild(rectangleGroup);

	}


	//Method allows you to get the currently active scene
	public SceneType getCurrentScene() {
		return currentScene;
	}

	//Method allows you to set the currently active scene
	public void setCurrentScene(SceneType scene) {
		currentScene = scene;
		switch (scene)
		{
		case SPLASH:
			break;
		case MAINGAME:
			engine.setScene(mainGameScene);
			break;
		}
	}

}
