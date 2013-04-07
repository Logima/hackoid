package fi.hackoid;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Stats {
	private int drunkness;
	private int pie;
	
	private ITextureRegion bottle;
	
	private Sprite[] drunkLevel = new Sprite[5];
	private Sprite[] pieLevel = new Sprite[4];
	
	private BitmapTextureAtlas textureAtlas;
	
	private HUD myHud;
	
	public Stats(HUD h) {
		myHud = h;
	}
	
	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		this.bottle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.textureAtlas, main, "object_beerbottle.png", 0, 0);
		textureAtlas.load();
	}
	
	public void createScene(VertexBufferObjectManager vertexBufferObjectManager) {
		for (int i = 0; i < 5; i++) {
			Sprite b = new Sprite(2+32*i, 2, 30, 80, bottle, vertexBufferObjectManager);
			b.setVisible(true);
			drunkLevel[i] = b;
			myHud.attachChild(b);
		}
		
	}
}
