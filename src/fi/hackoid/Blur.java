package fi.hackoid;

import java.util.ArrayList;

import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class Blur {
	
	static public boolean mRadialBlurring = true;
	static public float mRadialBlurCenterX = 0.5f;
	static public float mRadialBlurCenterY = 0.5f;
	
	static public float increment = 0.4f;
	
	static public int maxBeerCount = 5;
	
	public static class RadialBlurShaderProgram extends ShaderProgram {
		// ===========================================================
		// Constants
		// ===========================================================

		private static ArrayList<RadialBlurShaderProgram> INSTANCES;

		public static final String VERTEXSHADER =
			"uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" +
			"attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"attribute vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"varying vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
			"void main() {\n" +
			"	" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"	gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"}";

		private static final String UNIFORM_RADIALBLUR_CENTER = "u_radialblur_center";

		public static String getFragmentShader(float sampleStrength)
		{
			String FRAGMENTSHADER =
					"precision lowp float;\n" +

					"uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
					"varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

					"uniform vec2 " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + ";\n" +

					"const float sampleShare = (1.0 / 11.0);\n" +
					"const float sampleDist = 1.0;\n" +
					"const float sampleStrength = " + sampleStrength + ";\n" +

					"void main() {\n" +
					/* The actual (unburred) sample. */
					"	vec4 color = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ");\n" +

					/* Calculate direction towards center of the blur. */
					"	vec2 direction = " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + " - " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

					/* Calculate the distance to the center of the blur. */
					"	float distance = sqrt(direction.x * direction.x + direction.y * direction.y);\n" +

					/* Normalize the direction (reuse the distance). */
					"	direction = direction / distance;\n" +

					"	vec4 sum = color * sampleShare;\n" +
					/* Take 10 additional samples along the direction towards the center of the blur. */
					"	vec2 directionSampleDist = direction * sampleDist;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.08 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.05 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.03 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.02 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.01 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.01 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.02 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.03 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.05 * directionSampleDist) * sampleShare;\n" +
					"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.08 * directionSampleDist) * sampleShare;\n" +

					/* Weighten the blur effect with the distance to the center of the blur (further out is blurred more). */
					"	float t = sqrt(distance) * sampleStrength;\n" +
					"	t = clamp(t, 0.0, 1.0);\n" + // 0 <= t >= 1

					/* Blend the original color with the averaged pixels. */
					"	gl_FragColor = mix(color, sum, t);\n" +
					"}";
			
			return FRAGMENTSHADER;
		}

		// ===========================================================
		// Fields
		// ===========================================================

		public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformRadialBlurCenterLocation = ShaderProgramConstants.LOCATION_INVALID;

		// ===========================================================
		// Constructors
		// ===========================================================

		private RadialBlurShaderProgram(int drunkness) {
			super(RadialBlurShaderProgram.VERTEXSHADER, getFragmentShader(increment  * drunkness));
		}

		public static RadialBlurShaderProgram getInstance(int drunkness) {
			if(RadialBlurShaderProgram.INSTANCES == null) {
				
				RadialBlurShaderProgram.INSTANCES = new ArrayList<Blur.RadialBlurShaderProgram>();
				
				for(int i = 0; i < maxBeerCount; i++)
				{
					RadialBlurShaderProgram shaderProgram = new RadialBlurShaderProgram(i);
					
					RadialBlurShaderProgram.INSTANCES.add(shaderProgram);
				}
			}
			if(drunkness >= maxBeerCount - 1)
			{
				return RadialBlurShaderProgram.INSTANCES.get(maxBeerCount - 1);
			}
			return RadialBlurShaderProgram.INSTANCES.get(drunkness);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		protected void link(final GLState pGLState) throws ShaderProgramLinkException {
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

			super.link(pGLState);

			RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
			RadialBlurShaderProgram.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);

			RadialBlurShaderProgram.sUniformRadialBlurCenterLocation = this.getUniformLocation(RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER);
		}

		@Override
		public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
			GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.bind(pGLState, pVertexBufferObjectAttributes);

			GLES20.glUniformMatrix4fv(RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
			GLES20.glUniform1i(RadialBlurShaderProgram.sUniformTexture0Location, 0);
		}

		@Override
		public void unbind(final GLState pGLState) throws ShaderProgramException {
			GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.unbind(pGLState);
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
