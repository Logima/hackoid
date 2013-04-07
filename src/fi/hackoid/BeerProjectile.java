package fi.hackoid;

import java.util.Random;

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

public class BeerProjectile {

	private BitmapTextureAtlas textureAtlas;
	private TiledTextureRegion textureRegion;

	AnimatedSprite animatedSprite;

	Body body;
	Main main;

	private Random random = new Random();

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0f);

	public BeerProjectile(Main main, PhysicsWorld world, Enemy enemy) {
		createResources(main);
		createScene(main.getVertexBufferObjectManager(), world, enemy);
		main.getScene().attachChild(animatedSprite);
		synchronized (main.beers) {
			main.beers.add(this);
		}
		this.main = main;
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"object_beerbottle.png", 0, 0, 1, 1);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld world, Enemy enemy) {
		AnimatedSprite enemySprite = enemy.getAnimatedSprite();
		float projectileX = enemySprite.getX();
		float projectileY = enemySprite.getY();

		animatedSprite = new AnimatedSprite(projectileX, projectileY, textureRegion, vertexBufferObjectManager);
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		animatedSprite.setCurrentTileIndex(0);

		animatedSprite.registerUpdateHandler(world);

		body = PhysicsFactory.createBoxBody(world, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData("beer");

		world.registerPhysicsConnector(new PhysicsConnector(animatedSprite, body, true, true));

		body.setLinearDamping(2f);
		body.setLinearVelocity(-20, -20);
		body.applyAngularImpulse((random.nextFloat() - 0.5f) * 30);

	}

	public void destroy() {
		synchronized (main.beers) {
			main.beers.remove(this);
		}
		synchronized (main.beersToBeRemoved) {
			main.beersToBeRemoved.add(this);
		}
	}
}
