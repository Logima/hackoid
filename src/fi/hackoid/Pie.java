package fi.hackoid;

import java.util.Random;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Pie {

	private BitmapTextureAtlas textureAtlas;
	private ITextureRegion pie;
	
	private Main main;

	private Random random = new Random();
	
	public Pie(Main main) {
		this.main = main;
		createResources(main);
		createScene(main.getVertexBufferObjectManager(), main.world);
	}
	
	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		this.pie = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.textureAtlas, main,
				"object_pie.png", 0, 0);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld world) {
		
		float x = random.nextInt(2000) + 1000;

		Sprite b = new Sprite(x, 260, 160, 80, pie, vertexBufferObjectManager) {
		      @Override
		      protected void onManagedUpdate(final float pSecondsElapsed)
		      {
		            if (main.player.animatedSprite.collidesWith(this))
		            {
		                setPosition(getX() + random.nextInt(2000) + 1000, getY());
		                main.stats.eatPie();
		            }
		           super.onManagedUpdate(pSecondsElapsed);
		       }
		};
		b.setScale(0.5f);
		b.setVisible(true);
		this.main.scene.attachChild(b);
	}
}
