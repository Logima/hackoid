package fi.hackoid;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemy {

	private BitmapTextureAtlas textureAtlas;
	private TiledTextureRegion textureRegion;

	private AnimatedSprite animatedSprite;

	Body body;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	private long[] frameTimes = new long[] { 0, 0, 100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 100, 100, 100, 100,
			100, 100, 100, 100 };

	public Enemy() {
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"monster_teekkari_walking.png", 0, 0, 10, 4);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, int cameraWidth, int cameraHeight,
			PhysicsWorld world) {
		float playerX = 500;
		float playerY = 200;

		animatedSprite = new AnimatedSprite(playerX, playerY, textureRegion, vertexBufferObjectManager);
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		animatedSprite.animate(frameTimes, 20, 39, true);

		animatedSprite.registerUpdateHandler(world);

		body = PhysicsFactory.createBoxBody(world, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);

		world.registerPhysicsConnector(new PhysicsConnector(animatedSprite, body, true, false));

		body.setLinearVelocity(-10 * 2, 0);
	}

	public Body getPhysicsBody() {
		return body;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

}
