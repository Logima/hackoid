package fi.hackoid;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

public class Stats {
	int drunkness = 0;
	int pies = 0;

	private ITextureRegion bottle;
	private ITextureRegion pie;

	private Sprite[] drunkLevel = new Sprite[5];
	private Sprite[] pieLevel = new Sprite[4];

	private BitmapTextureAtlas textureAtlas;

	private HUD myHud;
	private Camera camera;
	
	Main main;

	public Stats(HUD h, Camera camera) {
		myHud = h;
		this.camera = camera;
	}

	public void createResources(Main main) {
		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		this.bottle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.textureAtlas, main,
				"object_beerbottle.png", 0, 0);
		this.pie = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.textureAtlas, main, "object_pie.png",
				300, 0);
		textureAtlas.load();
		
		this.main = main;
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager) {
		// beer-o-meter
		for (int i = 0; i < 5; i++) {
			Sprite b = new Sprite(2 + 32 * i, 2, 30, 80, bottle, vertexBufferObjectManager);
			b.setVisible(false);
			drunkLevel[i] = b;
			myHud.attachChild(b);
		}

		// pies
		float xLimit = this.camera.getWidth() - 130;
		for (int i = 0; i < 4; i++) {
			Sprite b = new Sprite(xLimit - 90 * i, 2, 160, 80, pie, vertexBufferObjectManager);
			b.setScale(0.5f);
			b.setVisible(false);
			pieLevel[i] = b;
			myHud.attachChild(b);
		}
	}

	public void drinkBeer() {
		if (drunkness > 4) {
			Log.e("debug", "Kuolema");
			main.playerDeath();
			return;
		}
		drunkLevel[drunkness].setVisible(true);
		drunkness++;
	}
	
	public void eatPie() {
		if (pies > 3) {
			Log.e("debug", "Found an pie'ses");
			main.boss = new Boss(main);
		} else {
			pieLevel[pies].setVisible(true);
			pies++;			
		}
		
		if (drunkness == 0) {
			Log.e("debug", "No beers");
			return;
		}
		drunkness--;
		drunkLevel[drunkness].setVisible(false);
	}
}
