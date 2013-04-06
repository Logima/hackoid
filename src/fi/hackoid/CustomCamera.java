package fi.hackoid;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.util.Constants;

public class CustomCamera extends Camera {

	private IEntity mChaseEntity;

	public CustomCamera(float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight);
	}

	@Override
	public void setChaseEntity(final IEntity pChaseEntity) {
		this.mChaseEntity = pChaseEntity;
	}

	@Override
	public void updateChaseEntity() {
		if (mChaseEntity != null) {
			final float[] centerCoordinates = this.mChaseEntity.getSceneCenterCoordinates();
			this.setCenter(centerCoordinates[Constants.VERTEX_INDEX_X],
					this.getCenterY());
		}
	}

}
