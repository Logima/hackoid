package fi.hackoid;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class WinScreen {

	private BitmapTextureAtlas textureAtlas;
	private TextureRegion textureRegion;

	Sprite sprite;

	public WinScreen(Main main, HUD hud) {
		createResources(main);
		createScene(main.getVertexBufferObjectManager());
		hud.attachChild(sprite);
	}
	

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, main, "victory_screen.png",
				0, 0);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager) {
		sprite = new Sprite(0, 0, textureRegion, vertexBufferObjectManager);
		
		sprite.setScale(1);
	}
}
