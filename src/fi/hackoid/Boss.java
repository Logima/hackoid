package fi.hackoid;

import java.util.Random;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

public class Boss {

	private static BitmapTextureAtlas textureAtlas;
	private static BitmapTextureAtlas deathTextureAtlas;

	private static TiledTextureRegion textureRegion;

	AnimatedSprite animatedSprite;

	private Main main;

	private Random random = new Random();

	int HP = 10;

	boolean dead = false;

	private int frameTime = 80;
	private long[] frameTimes = new long[] { frameTime, frameTime, frameTime, frameTime, frameTime };

	private int deathFrameTime = 100;
	private long[] deathFrameTimes = new long[] { deathFrameTime, deathFrameTime, deathFrameTime, deathFrameTime,
			deathFrameTime, deathFrameTime, deathFrameTime, deathFrameTime, deathFrameTime, deathFrameTime };

	public Boss(Main main) {
		this.main = main;
		createResources(main);
		createScene(main.getVertexBufferObjectManager(), Main.CAMERA_WIDTH, Main.CAMERA_HEIGHT, main.world);
		main.scene.attachChild(animatedSprite);
	}

	public void createResources(Main main) {
		if (textureAtlas != null && deathTextureAtlas != null) {
			return;
		}

		textureAtlas = new BitmapTextureAtlas(main.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, main,
				"monster_matemaagikko.png", 0, 0, 10, 4);
		textureAtlas.load();
	}

	public void createScene(VertexBufferObjectManager vertexBufferObjectManager, int cameraWidth, int cameraHeight,
			PhysicsWorld world) {
		float x = main.player.animatedSprite.getX() + 7000 + random.nextInt(3000);
		float y = 125;

		animatedSprite = new AnimatedSprite(x, y, textureRegion, vertexBufferObjectManager) {
			private boolean collidingWithPlayer = false;
			boolean facingRight = true;

			@Override
			protected void onManagedUpdate(final float pSecondsElapsed) {
				if (!dead) {
					if (main.player.animatedSprite.collidesWith(this) && !collidingWithPlayer) {
						main.stats.drinkBeer();
						collidingWithPlayer = true;
					} else if (!main.player.animatedSprite.collidesWith(this)) {
						collidingWithPlayer = false;
					}

					synchronized (main.spears) {
						for (SpearProjectile spear : main.spears) {
							if (spear.sprite.collidesWith(this) && !spear.hasDamaged) {
								spear.hasDamaged = true;
								HP--;
								if (HP <= 0) {
									dead = true;
									Log.e("debug", "Boss HP 0");
									if (facingRight) {
										animatedSprite.animate(deathFrameTimes, 30, 39, false);
									} else {
										animatedSprite.animate(deathFrameTimes, 20, 29, false);
									}
									// voitit pelin
								}
							}
						}
					}

					if (animatedSprite.getX() > main.player.animatedSprite.getX()) {
						animatedSprite.setPosition(animatedSprite.getX() - 10, animatedSprite.getY());
						if (facingRight) {
							animatedSprite.animate(frameTimes, 0, 4, true);
						}
						facingRight = false;
					} else {
						animatedSprite.setPosition(animatedSprite.getX() + 10, animatedSprite.getY());
						if (!facingRight) {
							animatedSprite.animate(frameTimes, 10, 14, true);
						}
						facingRight = true;
					}
				}

				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		animatedSprite.setScaleCenterY(textureRegion.getHeight());
		animatedSprite.setScale(1);

		animatedSprite.animate(frameTimes, 0, 4, true);

	}
}
