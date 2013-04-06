package fi.hackoid;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
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

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;

	private ITextureRegion mParallaxLayerBack;
	private ITextureRegion mParallaxLayerMid;
	private ITextureRegion mParallaxLayerFront;

	private BitmapTextureAtlas controlTextureAtlas;
	private ITextureRegion horizontalControlTexture;

	private Camera camera;

	private Player player = new Player();

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

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH,
				CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		player.createResources(this);

		this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_front.png", 0, 0);
		this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_back.png", 0, 188);
		this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_mid.png", 0, 669);
		this.mAutoParallaxBackgroundTexture.load();

		this.controlTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.horizontalControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.controlTextureAtlas, this, "touchscreen_horizontal_control.png", 0, 0);
		this.controlTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT
				- this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 80, this.mParallaxLayerMid,
				vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(0, CAMERA_HEIGHT
				- this.mParallaxLayerFront.getHeight(), this.mParallaxLayerFront, vertexBufferObjectManager)));
		scene.setBackground(autoParallaxBackground);

		player.createScene(vertexBufferObjectManager, CAMERA_WIDTH, CAMERA_HEIGHT);

		createControllers();

		scene.attachChild(player.getAnimatedSprite());

		return scene;
	}

	private void createControllers() {
		HUD yourHud = new HUD();

		final int xSize = 340;
		final int ySize = 110;

		final Sprite horizontalControl = new Sprite(0, 610, xSize, ySize, horizontalControlTexture,
				this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				float speed = 0;
				if (!touchEvent.isActionUp()) {
					if (X < (xSize / 2)) {
						speed = (xSize / 2) - X;
						speed = -speed;
					} else {
						speed = X - xSize / 2;
					}
				}
				player.getPhysicsHandler().setVelocity(speed * 2, 0);
				Log.w("debug", "horizontal control clicked: X: '" + X + "' Y: '" + Y + "'");
				return true;
			};
		};

		yourHud.registerTouchArea(horizontalControl);
		yourHud.attachChild(horizontalControl);

		this.camera.setHUD(yourHud);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}