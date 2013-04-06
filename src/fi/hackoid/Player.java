package fi.hackoid;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player {

	private BitmapTextureAtlas textureAtlas;
	private TiledTextureRegion textureRegion;

	private PhysicsHandler playerPhysicsHandler;

	private AnimatedSprite animatedSprite;

	private float oldSpeed;
	
	private boolean facingRight = true;
	
	private Main context;
	
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	public Player() {
	}

	public void createResources(Main main) {
		this.context = main;
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 256, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"player_walking_right.png", 0, 0, 11, 2);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, int cameraWidth, int cameraHeight, PhysicsWorld world) {
		final float playerX = (cameraWidth - textureRegion.getWidth()) / 2;
		final float playerY = cameraHeight - textureRegion.getHeight() - 5;

		animatedSprite = new AnimatedSprite(playerX, playerY, textureRegion, vertexBufferObjectManager);
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		animatedSprite.setCurrentTileIndex(0);

		playerPhysicsHandler = new PhysicsHandler(animatedSprite);
		animatedSprite.registerUpdateHandler(playerPhysicsHandler);
		
		
		final Body body;
		body = PhysicsFactory.createBoxBody(world, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
		
		world.registerPhysicsConnector(new PhysicsConnector(animatedSprite, body, true, true));
	}

	public void run(float speed) {
		playerPhysicsHandler.setVelocity(speed * 2, 0);

		if (speed == 0) {
			animatedSprite.stopAnimation();
			animatedSprite.setCurrentTileIndex(0);
		} else if (oldSpeed == 0) {
			long[] frameTimes = new long[] { 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 100, 100, 100, 100,
					100, 100, 100, 100, 0 };
			animatedSprite.animate(frameTimes, 0, 21, true);			
		}
		
		if (this.facingRight && speed < 0 || !this.facingRight && speed > 0) {
			this.facingRight = !this.facingRight;
			toggleDirection();
		}
		
		oldSpeed = speed;
	}
	
	private void toggleDirection() {
		this.textureAtlas.clearTextureAtlasSources();
		BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, this.context,
				this.facingRight ? "player_walking_right.png" : "player_walking_left.png", 0, 0, 11, 2);
}

	public PhysicsHandler getPhysicsHandler() {
		return playerPhysicsHandler;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

	public void jump() {
		Log.w("debug", "jump pressed");
	}

}
