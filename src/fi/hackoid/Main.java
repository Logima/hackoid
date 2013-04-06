package fi.hackoid;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;

public class Main extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private TiledTextureRegion mEnemyTextureRegion;

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;

	private ITextureRegion mParallaxLayerBack;
	private ITextureRegion mParallaxLayerMid;
	private ITextureRegion mParallaxLayerFront;

	private BitmapTextureAtlas controlTextureAtlas;
	private ITextureRegion horizontalControlTexture;

	private Camera camera;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 2048, 256, TextureOptions.BILINEAR);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"player_walking_right.png", 0, 0, 11, 2);
		this.mEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"enemy.png", 73, 0, 3, 4);
		this.mBitmapTextureAtlas.load();

		this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 1024, 1024);
		this.mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxBackgroundTexture, this,
						"parallax_background_layer_front.png", 0, 0);
		this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxBackgroundTexture, this,
						"parallax_background_layer_back.png", 0, 188);
		this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mAutoParallaxBackgroundTexture, this,
						"parallax_background_layer_mid.png", 0, 669);
		this.mAutoParallaxBackgroundTexture.load();

		this.controlTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 1024, 1024);
		this.horizontalControlTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.controlTextureAtlas, this,
						"touchscreen_horizontal_control.png", 0, 0);
		this.controlTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 5);
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f,
				new Sprite(0, CAMERA_HEIGHT
						- this.mParallaxLayerBack.getHeight(),
						this.mParallaxLayerBack, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f,
				new Sprite(0, 80, this.mParallaxLayerMid,
						vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f,
				new Sprite(0, CAMERA_HEIGHT
						- this.mParallaxLayerFront.getHeight(),
						this.mParallaxLayerFront, vertexBufferObjectManager)));
		scene.setBackground(autoParallaxBackground);

		/*
		 * Calculate the coordinates for the face, so its centered on the
		 * camera.
		 */
		final float playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion
				.getWidth()) / 2;
		final float playerY = CAMERA_HEIGHT
				- this.mPlayerTextureRegion.getHeight() - 5;

		/* Create two sprits and add it to the scene. */
		final AnimatedSprite player = new AnimatedSprite(playerX, playerY,
				this.mPlayerTextureRegion, vertexBufferObjectManager);
		player.setScaleCenterY(this.mPlayerTextureRegion.getHeight());
		player.setScale(1);
		
		long[] frameTimes = new long[]{ 0, 0, 100,
									   100, 100, 100,
									   100, 100, 100,
									   100, 0, 0,
									   0, 100, 100,
									   100, 100, 100,
									   100, 100, 100,
									   0
		};
		player.animate(frameTimes, 0, 21, true);
		
		PhysicsHandler playerPhysicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(playerPhysicsHandler);
		
		createControllers(playerPhysicsHandler);

		final AnimatedSprite enemy = new AnimatedSprite(playerX - 80,
				playerY + 60, this.mEnemyTextureRegion,
				vertexBufferObjectManager);
		enemy.setScaleCenterY(this.mEnemyTextureRegion.getHeight());
		enemy.setScale(2);
		enemy.animate(new long[] { 200, 200, 200 }, 3, 5, true);

		scene.attachChild(player);
		scene.attachChild(enemy);

		

		return scene;
	}

	private void createControllers(final PhysicsHandler playerPhysics) {
		HUD yourHud = new HUD();

		final int xSize = 340;
		final int ySize = 110;

		final Sprite horizontalControl = new Sprite(0, 610, xSize, ySize,
				horizontalControlTexture, this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				// if (touchEvent.isActionDown())
				// {
				float speed = 0;
				if (X < (xSize / 2)) {
					speed = (xSize / 2) - X;
					speed = -speed;
				} else {
					speed = X - xSize / 2;
				}

				playerPhysics.setVelocity(speed * 2, 0);
				Log.w("debug", "left clicked: X: '" + X + "' Y: '" + Y + "'");
				// }
				if (touchEvent.isActionUp()) {
					playerPhysics.setVelocity(0, 0);
				}
				return true;
			};
		};

		yourHud.registerTouchArea(horizontalControl);
		// yourHud.registerTouchArea(right);
		yourHud.attachChild(horizontalControl);
		// yourHud.attachChild(right);

		this.camera.setHUD(yourHud);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}