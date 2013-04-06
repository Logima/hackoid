package fi.hackoid;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Enemy {

	private BitmapTextureAtlas textureAtlas;
	private TiledTextureRegion textureRegion;

	private PhysicsHandler physicsHandler;

	private AnimatedSprite animatedSprite;

	public Enemy() {
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 256, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"monster_teekkari_walking_left.png", 0, 0, 11, 2);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, int cameraWidth, int cameraHeight) {
		/*
		 * Calculate the coordinates for the face, so its centered on the
		 * camera.
		 */
		final float playerX = cameraWidth - textureRegion.getWidth();
		final float playerY = cameraHeight - textureRegion.getHeight();
		
		animatedSprite = new AnimatedSprite(playerX, playerY, textureRegion, vertexBufferObjectManager);
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		long[] frameTimes = new long[] { 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 100, 100, 100, 100,
				100, 100, 100, 100, 0 };
		animatedSprite.animate(frameTimes, 0, 21, true);

		physicsHandler = new PhysicsHandler(animatedSprite);
		physicsHandler.setVelocity(-10 * 2, 0);
		animatedSprite.registerUpdateHandler(physicsHandler);
	}

	public PhysicsHandler getPhysicsHandler() {
		return physicsHandler;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

}
