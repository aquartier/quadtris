package com.dekdroid.quadtris;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
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

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.sax.StartElementListener;

import com.dekdroid.quadtris.Shape.Movement;

/**
 * 
 * @author LeoNaiDaS
 * @see http://stuartmct.co.uk/2012/07/16/andengine-working-with-rectangles/
 * 
 */

public class SceneManager implements SensorEventListener {

	private static final float SCORE_PER_BLOCK = 25;
	private static final float SCORE_PER_MOVE = 5;
	private static final float DELAY_DEC_RATE = 0.02f;
	private static final float SCORE_DEC_RATE = 4;

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
	private int[][] realBoardPosX, realBoardPosY;
	private int[][] realShapePosX, realShapePosY;
	private Entity rectangleGroup;
	private TextureRegion gameOverTexture;
	private TextureRegion lRotateTexture;
	private TextureRegion rRotateTexture;
	private TextureRegion speedTexture;
	private TextureRegion backgroundTexture1, backgroundTexture2,
			backgroundTexture3;
	private int[][] map;
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private Text text;
	private long score = 0;
	private int tetrominoArray[][];

	private SensorManager sensorManager;
	private int accellerometerSpeedX;
	private int accellerometerSpeedY;

	private Shape tetromino;
	private boolean running;

	private float delay = 1.0f; // second
	private Timer jeepTimer, bgTimer; // Timer
	private SpriteBackground bg1;
	private SpriteBackground bg2;
	private SpriteBackground bg3;
	private Sprite gameOver;
	public int bgNumber = 1;
	public boolean[] gameOverStatus = new boolean[4];

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
				activity.getTextureManager(), 300, 250, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(splashTextureAtlas, activity, "splash.png", 0,
						0);
		splashTextureAtlas.load();
	}

	// Method loads all of the resources for the game scenes such as sprite
	public void loadGameSceneResources() {
		sensorManager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		engine.registerUpdateHandler(new Timer(0.08f,
				new Timer.ITimerCallback() {
					@Override
					public void onTick() {
						updateTetromino();
					}
				}));

		mFontTexture = new BitmapTextureAtlas(null, 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFont = new Font(null, this.mFontTexture, Typeface.create(
				Typeface.DEFAULT, Typeface.BOLD), 20, true, Color.BLACK);
		engine.getTextureManager().loadTexture(this.mFontTexture);
		activity.getFontManager().loadFont(this.mFont);
		try {
			ITexture gameOverTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets()
									.open("gfx/gameOver.png");
						}
					});
			ITexture backgroundTexture1 = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/bg1.png");
						}
					});
			ITexture backgroundTexture2 = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/bg2.png");
						}
					});
			ITexture backgroundTexture3 = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/bg3.png");
						}
					});
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
			ITexture speedTexture = new BitmapTexture(
					activity.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return activity.getAssets().open("gfx/speed.png");
						}
					});
			lRotateTexture.load();
			rRotateTexture.load();
			speedTexture.load();
			gameOverTexture.load();
			backgroundTexture1.load();
			backgroundTexture2.load();
			backgroundTexture3.load();
			this.backgroundTexture1 = TextureRegionFactory
					.extractFromTexture(backgroundTexture1);
			this.backgroundTexture2 = TextureRegionFactory
					.extractFromTexture(backgroundTexture2);
			this.backgroundTexture3 = TextureRegionFactory
					.extractFromTexture(backgroundTexture3);
			this.lRotateTexture = TextureRegionFactory
					.extractFromTexture(lRotateTexture);
			this.rRotateTexture = TextureRegionFactory
					.extractFromTexture(rRotateTexture);
			this.speedTexture = TextureRegionFactory
					.extractFromTexture(speedTexture);
			this.gameOverTexture = TextureRegionFactory
					.extractFromTexture(gameOverTexture);
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
		map = new int[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];

		myRectangle = new Rectangle[Quadtris.BOARD_HEIGHT][Quadtris.BOARD_WIDTH];

		rectangleGroup = drawBoardTable();
		rectangleGroup.setPosition(0, 0);

		bg1 = new SpriteBackground(new Sprite(0, 0, backgroundTexture1,
				activity.getVertexBufferObjectManager()));
		bg2 = new SpriteBackground(new Sprite(0, 0, backgroundTexture2,
				activity.getVertexBufferObjectManager()));
		bg3 = new SpriteBackground(new Sprite(0, 0, backgroundTexture3,
				activity.getVertexBufferObjectManager()));

		gameOver = new Sprite(Quadtris.CAMERA_WIDTH / 2
				- gameOverTexture.getWidth() / 2, Quadtris.CAMERA_HEIGHT / 2
				- gameOverTexture.getHeight() / 2, gameOverTexture,
				activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					// TODO rotate left
					newGame();
				}
				return true;
			}

		};

		Sprite lRotate = new Sprite(20, Quadtris.CAMERA_HEIGHT - 100,
				lRotateTexture, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					// TODO rotate left
					tetromino.rotateLeft();
					if (!placable(tetromino))
						tetromino.rotateRight();
					update();

				}
				return true;
			}
		};
		Sprite rRotate = new Sprite(Quadtris.CAMERA_WIDTH - 100,
				Quadtris.CAMERA_HEIGHT - 100, rRotateTexture,
				activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					// TODO rotate right
					tetromino.rotateRight();
					if (!placable(tetromino))
						tetromino.rotateLeft();
					update();

				}
				return true;
			}
		};
		Sprite speedUp = new Sprite(Quadtris.CAMERA_WIDTH / 2
				- speedTexture.getWidth() / 2, Quadtris.CAMERA_HEIGHT - 100,
				speedTexture, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					jeepTimer.setInterval(0.1f);

				}
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					jeepTimer.setInterval(delay);
				}
				return true;
			}
		};

		text = new Text(Quadtris.CAMERA_WIDTH - 170, 40, mFont, "SCORE : "
				+ score, 25, activity.getVertexBufferObjectManager());

		mainGameScene.registerTouchArea(lRotate);
		mainGameScene.registerTouchArea(rRotate);
		mainGameScene.registerTouchArea(speedUp);
		mainGameScene.registerTouchArea(gameOver);
		mainGameScene.setTouchAreaBindingOnActionDownEnabled(true);
		mainGameScene.setBackground(bg1);
		mainGameScene.attachChild(lRotate);
		mainGameScene.attachChild(rRotate);
		mainGameScene.attachChild(speedUp);
		mainGameScene.attachChild(rectangleGroup); // Add BoardTable to Scene
		mainGameScene.attachChild(text);

		// CALL JEEP PART
		gameMainControl();

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
		realBoardPosX = boardTable.getRealBoardPosX();
		realBoardPosY = boardTable.getRealBoardPosY();
		realShapePosX = boardTable.getRealShapePosX();
		realShapePosY = boardTable.getRealShapePosY();
		rectangleGroup = new Entity(0, 0);
		for (int i = 0; i < Quadtris.BOARD_WIDTH; i++) {
			for (int j = 0; j < Quadtris.BOARD_HEIGHT; j++) {
				if (realBoardPosX[i][j] != -1) {
					myRectangle[i][j] = new Rectangle(realBoardPosX[i][j],
							realBoardPosY[i][j], BoardTable.BLOCK_WIDTH - 2,
							BoardTable.BLOCK_HEIGHT - 2,
							activity.getVertexBufferObjectManager());
					if (i == j && i == Quadtris.BOARD_WIDTH / 2)
						myRectangle[i][j].setColor(1, 0, 0);
					else
						myRectangle[i][j].setColor(0, 0, 0);
					rectangleGroup.attachChild(myRectangle[i][j]);
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (realShapePosX[i][j] != -1) {
					myRectangle[i][j] = new Rectangle(realShapePosX[i][j],
							realShapePosY[i][j], BoardTable.BLOCK_WIDTH - 2,
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
	public void gameMainControl() {
		resetMap();
		tetromino = new Shape();
		update();
		clearGameOverStatus();
		delay = 1f;
		// TODO code Control here
		jeepTimer = new Timer(delay, new Timer.ITimerCallback() {
			@Override
			public void onTick() {
				// Your code to execute each interval.

				if (isGameOver())
					return;
				score += SCORE_PER_MOVE / delay;
				text.setText("SCORE : " + score);
				Point oldPos = tetromino.getRPos();
				moveToNext();
				if (!placable(tetromino)) {
					tetromino.setRPos(oldPos);
					placeToMap();
					delay -= DELAY_DEC_RATE;
					removeFullLine();
					Shape newTetro = new Shape();
					while (!placable(newTetro) && !isGameOver()) {
						updateGameOverStatus(newTetro.getDir());
						newTetro = new Shape();
					}
					tetromino = newTetro;
				}
				if (outOfMap(tetromino) && !isGameOver()) {
					score /= SCORE_DEC_RATE;
					Shape newTetro = new Shape();
					while (!placable(newTetro) && !isGameOver()) {
						updateGameOverStatus(newTetro.getDir());
						newTetro = new Shape();
					}
					tetromino = newTetro;
				}
				update();
			}
		});
		bgTimer = new Timer(0.5f, new Timer.ITimerCallback() {
			@Override
			public void onTick() {
				if (bgNumber == 1) {
					mainGameScene.setBackground(bg1);
					bgNumber = 2;
				} else if (bgNumber == 2) {
					mainGameScene.setBackground(bg2);
					bgNumber = 3;
				} else if (bgNumber == 3) {
					mainGameScene.setBackground(bg3);
					bgNumber = 1;
				}
			}
		});
		engine.registerUpdateHandler(jeepTimer);
		engine.registerUpdateHandler(bgTimer);
		engine.registerUpdateHandler(new Timer(1f, new Timer.ITimerCallback() {
			@Override
			public void onTick() {
				mainGameScene.detachChild(gameOver);
				if (isGameOver() && !gameOver.hasParent()) {
					mainGameScene.attachChild(gameOver);
				}
			}
		}));

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

	public boolean outOfMap(Shape tetromino) {
		if (tetromino.getRPos().x < 0 && tetromino.getDir() == Movement.Left)
			return true;
		if (tetromino.getRPos().y < 0 && tetromino.getDir() == Movement.Up)
			return true;
		if (tetromino.getRPos().x > Quadtris.BOARD_WIDTH - 3
				&& tetromino.getDir() == Movement.Right)
			return true;
		if (tetromino.getRPos().y > Quadtris.BOARD_HEIGHT - 3
				&& tetromino.getDir() == Movement.Down)
			return true;
		return false;
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
		if(!isGameOver()){
			mainGameScene.detachChild(rectangleGroup);
			boardTable.setBoardAndTetromino(map, tetromino); // Change tetromino you
																// want and call
																// update()
			rectangleGroup = drawBoardTable();
			mainGameScene.attachChild(rectangleGroup);
		}
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

	// synchronized private boolean movable() {
	// Shape nextTetro = new Shape(tetromino);
	// nextTetro.setRPos(nextPoint(nextTetro.getRPos(), nextTetro.getDir()));
	// return placable(nextTetro);
	// }
	//
	// synchronized private boolean movable(Shape tetromino, Movement dir) {
	// Shape nextTetro = new Shape(tetromino);
	// nextTetro.setRPos(nextPoint(nextTetro.getRPos(), dir));
	// return placable(nextTetro);
	// }

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

	synchronized private void placeToMap() {
		tetrominoArray = tetromino.getShapeArray();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int y = i + tetromino.getRPos().y;
				int x = j + tetromino.getRPos().x;
				if (inTable(new Point(x, y)) && tetrominoArray[i][j] == 1)
					map[y][x] = 1;
			}
		}
	}

	synchronized private void moveToNext() {
		tetromino.setRPos(nextPoint(tetromino.getRPos(), tetromino.getDir()));
	}

	synchronized private void moveToNext(Shape tetromino, Movement dir) {
		tetromino.setRPos(nextPoint(tetromino.getRPos(), dir));
	}

	synchronized private boolean placable(Shape tetro) {
		int[][] tetroArray = tetro.getShapeArray();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int y = i + tetro.getRPos().y;
				int x = j + tetro.getRPos().x;
				if (inTable(new Point(x, y)) && tetroArray[i][j] == 1
						&& map[y][x] == 1)
					return false;
			}
		}
		return true;
	}

	public boolean isGameOver() {
		for (int i = 0; i < 4; i++) {
			if (gameOverStatus[i] == false)
				return false;
		}
		return true;
	}

	public void updateGameOverStatus(Movement m) {
		if (m == Movement.Up)
			gameOverStatus[0] = true;
		if (m == Movement.Left)
			gameOverStatus[1] = true;
		if (m == Movement.Down)
			gameOverStatus[2] = true;
		if (m == Movement.Right)
			gameOverStatus[3] = true;
	}

	public void clearGameOverStatus() {
		for (int i = 0; i < 4; i++)
			gameOverStatus[i] = false;
	}

	// TODO CheckLine
	public boolean isFullLine(int n) {
		for (int i = 0; i < n * 2 + 1; i++) {
			int h = Quadtris.BOARD_HEIGHT;
			int w = Quadtris.BOARD_WIDTH;
			if (map[i + h / 2 - n][w / 2 - n] == 0)
				return false;
			if (map[h / 2 - n][i + w / 2 - n] == 0)
				return false;
			if (map[i + h / 2 - n][w / 2 + n] == 0)
				return false;
			if (map[h / 2 + n][i + w / 2 - n] == 0)
				return false;
		}

		return true;
	}

	public void removeFullLine() {
		int n;
		boolean chk = false;
		int h = Quadtris.BOARD_HEIGHT;
		int w = Quadtris.BOARD_WIDTH;
		for (n = 1; n < h / 2; n++) {
			if (isFullLine(n)) {
				chk = true;
				break;
			}
		}
		if (!chk)
			return;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (i <= h / 2 - n || i >= h / 2 + n || j <= w / 2 - n
						|| j >= w / 2 + n) {
					if (map[i][j] == 1)
						score += SCORE_PER_BLOCK / delay;
					map[i][j] = 0;
				}

			}
		}
		clearGameOverStatus();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				accellerometerSpeedX = (int) (event.values[0] * 1.5);
				accellerometerSpeedY = (int) (event.values[1] * 1.5);
				break;
			}
		}
	}

	private void updateTetromino() {
		// TODO Auto-generated method stub
		Movement nextMovement;
		if (tetromino.getDir() == Movement.Down
				|| tetromino.getDir() == Movement.Up) {
			if (tetromino.getRPos().x == controlX())
				return;
			nextMovement = (tetromino.getRPos().x < controlX()) ? Movement.Right
					: Movement.Left;

		} else {

			if (tetromino.getRPos().y == controlY())
				return;
			nextMovement = (tetromino.getRPos().y > controlY()) ? Movement.Up
					: Movement.Down;
		}
		Point oldPos = tetromino.getRPos();
		moveToNext(tetromino, nextMovement);
		if (!placable(tetromino))
			tetromino.setRPos(oldPos);

		update();
	}

	private int controlX() {
		return -accellerometerSpeedX + Quadtris.BOARD_WIDTH / 2;
	}

	private int controlY() {
		return accellerometerSpeedY + Quadtris.BOARD_HEIGHT / 2;
	}

	private void newGame() {
		score = 0;
		delay = 1.0f;
		jeepTimer.setInterval(delay);
		clearGameOverStatus();
		resetMap();
		update();
		mainGameScene.detachChild(gameOver);
	}
}
