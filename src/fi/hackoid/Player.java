package fi.hackoid;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Player {

	private BitmapTextureAtlas textureAtlas;
	private TiledTextureRegion textureRegion;

	private PhysicsHandler physicsHandler;

	private AnimatedSprite animatedSprite;

	private float oldSpeed;

	public Player() {
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 256, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"player_walking_right.png", 0, 0, 11, 2);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, int cameraWidth, int cameraHeight) {
		final float playerX = (cameraWidth - textureRegion.getWidth()) / 2;
		final float playerY = cameraHeight - textureRegion.getHeight() - 5;

		animatedSprite = new AnimatedSprite(playerX, playerY, textureRegion, vertexBufferObjectManager);
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		animatedSprite.setCurrentTileIndex(0);

		physicsHandler = new PhysicsHandler(animatedSprite);
		animatedSprite.registerUpdateHandler(physicsHandler);
	}

	public void run(float speed) {
		physicsHandler.setVelocity(speed * 2, 0);

		if (speed == 0) {
			animatedSprite.stopAnimation();
			animatedSprite.setCurrentTileIndex(0);
		} else if (oldSpeed == 0) {
			long[] frameTimes = new long[] { 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 100, 100, 100, 100,
					100, 100, 100, 100, 0 };
			animatedSprite.animate(frameTimes, 0, 21, true);
		}
		oldSpeed = speed;
	}

	public PhysicsHandler getPhysicsHandler() {
		return physicsHandler;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

}
