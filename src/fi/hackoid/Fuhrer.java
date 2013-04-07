package fi.hackoid;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Fuhrer {
	private BitmapTextureAtlas textureAtlas;
	private TextureRegion textureRegion;

	Sprite sprite;

	float x;

	public Fuhrer(Main main, float x) {
		this.x = x;
		createResources(main);
		createScene(main.getVertexBufferObjectManager());
		main.scene.attachChild(sprite);
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, main,
				"object_herr_fuhrer.png", 0, 0);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager) {

		sprite = new Sprite(x, -25, textureRegion, vertexBufferObjectManager);
		sprite.setScaleCenterY(textureRegion.getHeight());
		sprite.setScale(1);
	}

	public void setX(float x) {
		this.x = x;
		sprite.setPosition(x, sprite.getY());
	}
}
