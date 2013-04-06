package fi.hackoid;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;

public class Main extends SimpleBaseGameActivity {

	private static final int CAMERA_WIDTH = 1280;
	private static final int CAMERA_HEIGHT = 720;

	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;

	private ITextureRegion mParallaxLayerBack;
	private ITextureRegion mParallaxLayerMid;
	private ITextureRegion mParallaxLayerFront;

	private BitmapTextureAtlas controlTextureAtlas;
	private ITextureRegion horizontalControlTexture;
	private ITextureRegion jumpControlTexture;
	private ITextureRegion fireControlTexture;

	private Camera camera;
	private AutoParallaxBackground autoParallaxBackground;

	private Player player = new Player();
	private Enemy enemy = new Enemy();
	
	private PhysicsWorld world;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new CustomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH,
				CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		player.createResources(this);
		enemy.createResources(this);

		this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), 2048, 2048);
		this.mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "background_texture_ground.png", 0, 0);
		this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "background_texture_sky.png", 0, 100);
		this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mAutoParallaxBackgroundTexture, this, "background_texture_city.png", 0, 820);
		this.mAutoParallaxBackgroundTexture.load();

		this.controlTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.horizontalControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.controlTextureAtlas, this, "touchscreen_horizontal_control.png", 0, 0);
		this.jumpControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.controlTextureAtlas,
				this, "touchscreen_button_jump.png", 0, 95);
		this.fireControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.controlTextureAtlas,
				this, "touchscreen_button_fire.png", 105, 95);
		this.controlTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 0);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT
				- this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 410, this.mParallaxLayerMid,
				vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(0, CAMERA_HEIGHT
				- this.mParallaxLayerFront.getHeight(), this.mParallaxLayerFront, vertexBufferObjectManager)));
		scene.setBackground(autoParallaxBackground);

		createControllers();

		camera.setChaseEntity(player.getAnimatedSprite());
		camera.setCenter(camera.getCenterX(), camera.getCenterY() - 200);
		
		this.world = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		player.createScene(vertexBufferObjectManager, CAMERA_WIDTH, CAMERA_HEIGHT, world);
		enemy.createScene(vertexBufferObjectManager, CAMERA_WIDTH, CAMERA_HEIGHT, world);
		
		final Rectangle ground = new Rectangle(0, 500, CAMERA_WIDTH, 10, vertexBufferObjectManager);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		
		PhysicsFactory.createBoxBody(this.world, ground, BodyType.StaticBody, wallFixtureDef);
		
		scene.attachChild(ground);
		
		scene.registerUpdateHandler(this.world);
		

		
		scene.attachChild(player.getAnimatedSprite());
		scene.attachChild(enemy.getAnimatedSprite());
		
		return scene;
	}

	private void createControllers() {
		HUD yourHud = new HUD();

		final int xSize = 380;
		final int ySize = 150;

		final Sprite horizontalControl = new Sprite(0, 570, xSize, ySize, horizontalControlTexture,
				this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				float playerSpeed = 0;
				if (!touchEvent.isActionUp()) {
					if (X < (xSize / 2)) {
						playerSpeed = (xSize / 2) - X;
						playerSpeed = -playerSpeed;
					} else {
						playerSpeed = (X - xSize / 2) + 100;
					}
				}
				if (xSize - X < 100 || Y < 80) {
					playerSpeed = 0;
				}
				playerSpeed *= 0.75;
				autoParallaxBackground.setParallaxChangePerSecond(playerSpeed / 5);
				player.run(playerSpeed);
				Log.w("debug", "horizontal control clicked: X: '" + X + "' Y: '" + Y + "'");
				return true;
			};
		};
		yourHud.registerTouchArea(horizontalControl);
		yourHud.attachChild(horizontalControl);

		final Sprite jumpControl = new Sprite(1175, 510, jumpControlTexture,
				this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				player.jump();
				return true;
			};
		};
		yourHud.registerTouchArea(jumpControl);
		yourHud.attachChild(jumpControl);

		final Sprite fireControl = new Sprite(1175, 615, fireControlTexture,
				this.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				Log.w("debug", "fire pressed");
				// fire
				return true;
			};
		};
		yourHud.registerTouchArea(fireControl);
		yourHud.attachChild(fireControl);
		this.camera.setHUD(yourHud);
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (mEngine.isRunning()) {
				mEngine.stop();
			} else {
				mEngine.start();
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
}