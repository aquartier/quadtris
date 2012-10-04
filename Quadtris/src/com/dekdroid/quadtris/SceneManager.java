package com.dekdroid.quadtris;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;

import com.dekdroid.quadtris.Shape.Movement;

/**
 * 
 * @author LeoNaiDaS
 * @see http://stuartmct.co.uk/2012/07/16/andengine-working-with-rectangles/
 * 
 */

public class SceneManager {

	private SceneType currentScene;
	BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private BitmapTextureAtlas splashTextureAtlas;
	private TextureRegion splashTextureRegion;
	private Scene splashScene;
	private Scene mainGameScene;
	private BoardTable boardTable;
	private Rectangle[][] myRectangle;
	private int[][] mainBlockPosX, mainBlockPosY;
	private Entity rectangleGroup;
	private TextureRegion lRotateTexture;
	private TextureRegion rRotateTexture;
	private TextureRegion backgroundTexture;
	private int[][] map;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private Text text;
	private int score = 0;
	private int tetrominoArray[][];

	private final int DELAY_START = 1000;
	private final int DELAY_STEP = 100;
	private final int DELAY_FINAL = 300;
	private final int DELAY_DEBUG = 100;
	private int delay = 1; // second

	private Shape tetromino;
	private boolean running;

	public enum SceneType {
		SPLASH, MAINGAME
	}

	public SceneManager(BaseGameActivity activity, Engine engine,
			Camera camera, BoardTable boardTable) {
		this.boardTable = boardTable;
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
	}

	// Method loads all of the splash scene resources
	public void loadSplashSceneResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(
				activity.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(splashTextureAtlas, activity, "splash.png", 0,
						0);
		splashTextureAtlas.load();
	}

	// Method loads all of the resources for the game scenes such as sprite
	public void loadGameSceneResources() {
		mFontTexture = new BitmapTextureAtlas(null, 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFont = new Font(null, this.mFontTexture, Typeface.create(
				Typeface.DEFAULT, Typeface.BOLD), 20, true, Color.BLACK);
		engine.getTextureManager().loadTexture(this.mFontTexture);
		activity.getFontManager().loadFont(this.mFont);
		try {
			/*
			 * ITexture backgroundTexture = new
			 * BitmapTexture(activity.getTextureManager(), new
			 * IInputStreamOpener() {
			 * 
			 * @Override public InputStream open() throws IOException { return
			 * activity.getAssets().open("gfx/board.gif"); } });
			 */
			ITexture lRotateTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open(
									"gfx/left_rotate.png");
						}
					});
			ITexture rRotateTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open(
									"gfx/right_rotate.png");
						}
					});
			lRotateTexture.load();
			rRotateTexture.load();
			// backgroundTexture.load();
			// this.backgroundTexture =
			// TextureRegionFactory.extractFromTexture(backgroundTexture);
			this.lRotateTexture = TextureRegionFactory
					.extractFromTexture(lRotateTexture);
			this.rRotateTexture = TextureRegionFactory
					.extractFromTexture(rRotateTexture);
		} catch (IOException e) {
			Debug.e(e);
		}

	}

	// Method creates the Splash Scene
	public Scene createSplashScene() {
		// Create the Splash Scene and set background color to red and add the
		// splash logo.
		splashScene = new Scene();
		splashScene.setBackground(new Background(0, 0, 0));
		Sprite splash = new Sprite(0, 0, splashTextureRegion,
				activity.getVertexBufferObjectManager()) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.0f);
		splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f,
				(camera.getHeight() - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);

		return splashScene;
	}

	// Method creates all of the Game Scenes
	public void createGameScenes() {
		// Create the Main Game Scene and set background color to white
		mainGameScene = new Scene();
		mainGameScene.setBackground(new Background(1, 1, 1));

		map = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];

		myRectangle = new Rectangle[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];
		mainBlockPosX = boardTable.getRealPosX();
		mainBlockPosY = boardTable.getRealPosY();

		rectangleGroup = drawBoardTable();
		rectangleGroup.setPosition(0, 0);

		Sprite lRotate = new Sprite(20, 700, lRotateTexture,
				activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					// TODO rotate left
					tetromino.rotateLeft();
//					if (!placable())
//						tetromino.rotateRight();
					//update();

				}
				return true;
			}
		};
		Sprite rRotate = new Sprite(380, 700, rRotateTexture,
				activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					// TODO rotate right
					tetromino.rotateRight();
//					if (!placable())
//						tetromino.rotateLeft();
					//update();

				}
				return true;
			}
		};

		text = new Text(Quadtris.CAMERA_WIDTH - 150, 40, mFont, "SCORE : "
				+ score, 25, activity.getVertexBufferObjectManager());

		mainGameScene.registerTouchArea(lRotate);
		mainGameScene.registerTouchArea(rRotate);
		mainGameScene.setTouchAreaBindingOnActionDownEnabled(true);
		mainGameScene.attachChild(lRotate);
		mainGameScene.attachChild(rRotate);
		mainGameScene.attachChild(rectangleGroup); // Add BoardTable to Scene
		mainGameScene.attachChild(text);

		// CALL JEEP PART
		jeep();

	}

	// Method allows you to get the currently active scene
	public SceneType getCurrentScene() {
		return currentScene;
	}

	// Method allows you to set the currently active scene
	public void setCurrentScene(SceneType scene) {
		currentScene = scene;
		switch (scene) {
		case SPLASH:
			break;
		case MAINGAME:
			engine.setScene(mainGameScene);
			break;
		}
	}

	public Entity drawBoardTable() {
		mainBlockPosX = boardTable.getRealPosX();
		mainBlockPosY = boardTable.getRealPosY();
		rectangleGroup = new Entity(0, 0);
		for (int i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				if (mainBlockPosX[i][j] != -1) {
					myRectangle[i][j] = new Rectangle(mainBlockPosX[i][j],
							mainBlockPosY[i][j], BoardTable.BLOCK_WIDTH - 2,
							BoardTable.BLOCK_HEIGHT - 2,
							activity.getVertexBufferObjectManager());
					myRectangle[i][j].setColor(0, 0, 0);
					rectangleGroup.attachChild(myRectangle[i][j]);
				}
			}
		}
		return rectangleGroup;
	}

	public void setBoardTable(BoardTable board) {
		this.boardTable = board;

	}

	/*
	 * 
	 * --------------------------------------------------------------------------
	 * ------------------------
	 * --------------------------------------------------
	 * ------------------------------------------------
	 * --------------------------
	 * ------------------------------------------------------------------------
	 */
	// Main Jeep Method
	public void jeep() {
		resetMap();
		tetromino = new Shape();
		update();
		// TODO code Control here
		Timer timer = new Timer(delay, new Timer.ITimerCallback() {
			public void onTick() {
				// Your code to execute each interval.
				resetMap();
				score++;
				text.setText("SCORE : " + score);

				if (movable()) {
					moveToNext();
				} else {
					placeToMap();
					tetromino = new Shape();
				}
				tetrominoArray = tetromino.getShapeArray();
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 4; j++) {
						int y = i + tetromino.getRPos().y;
						int x = j + tetromino.getRPos().x;
						if (inTable(new Point(y, x)))
							map[y][x] = tetrominoArray[i][j];
					}
				}
				update();
			}
		});
		engine.registerUpdateHandler(timer);

	}

	public void makeSimpleMap() {
		for (int i = 0; i < Quadtris.BOARD_HEIGHT; i++) {
			for (int j = 0; j < Quadtris.BOARD_WIDTH; j++) {
				if (i <= j)
					map[i][j] = 1;
				else
					map[i][j] = 0;
			}
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

	public void update() {
		mainGameScene.detachChild(rectangleGroup);
		boardTable.setBoard(map);
		boardTable.setTetromino(tetromino);
		rectangleGroup = drawBoardTable();
		mainGameScene.attachChild(rectangleGroup);
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	public boolean inTable(Point next) {

		if (next.x < 0 || next.x >= Quadtris.BOARD_WIDTH)
			return false;
		if (next.y < 0 || next.y >= Quadtris.BOARD_HEIGHT)
			return false;
		return true;
	}

	private boolean movable() {
		for (int i = 0; i < 4; i++) {
			Point next = nextPoint(tetromino.getRPos(), tetromino.getDir());
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

	private void moveToNext() {
		tetromino.setRPos(nextPoint(tetromino.getRPos(), tetromino.getDir()));
	}

	private boolean placable() {
		for (int i = 0; i < 4; i++) {
			int y = tetromino.y(i);
			int x = tetromino.x(i);
			if (map[y][x] == 1)
				return false;
		}
		return true;
	}

}
