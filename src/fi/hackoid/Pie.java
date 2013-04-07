package fi.hackoid;

import java.util.Random;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Pie {

	private BitmapTextureAtlas textureAtlas;
	private ITextureRegion pie;
	
	private Main main;
	
	private Sprite sprite;
	
	Body body;
	
	private Random random = new Random();
	
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0f);
	
	public Pie(Main main, PhysicsWorld world) {
		this.main = main;
		createResources(main);
		createScene(main.getVertexBufferObjectManager(), world);
	}
	
	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		this.pie = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.textureAtlas, main,
				"object_pie.png", 0, 0);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld world) {
		
		float x = random.nextInt(2000) + 1000;

		Sprite b = new Sprite(x, 0, 160, 80, pie, vertexBufferObjectManager);
		b.setScale(0.5f);
		b.setVisible(true);
		this.sprite = b;
		this.main.scene.attachChild(b);
		
		body = PhysicsFactory.createBoxBody(world, b, BodyType.DynamicBody, FIXTURE_DEF);
		body.setUserData("pie");
		
		world.registerPhysicsConnector(new PhysicsConnector(b, body, true, false));
	}
	
	public void move() {
		float x = this.main.player.animatedSprite.getX();
		x = x + random.nextInt(1000) + 1000;
		
		this.body.setLinearVelocity(0, 0);
		this.sprite.setPosition(x, 0);
		//Log.e("debug", this.body.getTransform().getPosition().toString());
		//this.body.setTransform(new Vector2(x/32, 0), 0f);
	}
}
