package fi.hackoid;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class DeathScreen {

	private BitmapTextureAtlas textureAtlas;
	private TextureRegion textureRegion;

	Sprite sprite;
	
	/*
	 *		deathScreenAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1280, 720);
		deathScreenTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(deathScreenAtlas,
				this, "you_lose.png", 0,0);
		
		deathScreenAtlas.load();
	 */

	public DeathScreen(Main main, HUD hud) {
		createResources(main);
		createScene(main.getVertexBufferObjectManager());
		hud.attachChild(sprite);
	}
	
	/*
	 *		this.controlTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.horizontalControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.controlTextureAtlas, this, "touchscreen_horizontal_control.png", 0, 0);
		this.jumpControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.controlTextureAtlas,
				this, "touchscreen_button_jump.png", 0, 190);
		this.fireControlTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.controlTextureAtlas,
				this, "touchscreen_button_fire.png", 210, 190);
		this.controlTextureAtlas.load();
	*/
	
	/*
	 * 
	 * final Sprite jumpControl = new Sprite(1070, 300, jumpControlTexture, this.getVertexBufferObjectManager()) {
	 */

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, main, "you_lose.png",
				0, 0);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager) {
		//final Sprite jumpControl = new Sprite(1070, 300, jumpControlTexture, this.getVertexBufferObjectManager()) {
		sprite = new Sprite(0, 0, textureRegion, vertexBufferObjectManager);
		//sprite.setScaleCenterY(textureRegion.getHeight());
		sprite.setScale(1);
	}
}
