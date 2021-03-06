/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.example.user.suivezbouddhaandroid;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import vuforia.utils.LeftArrow;
import vuforia.utils.SampleAppRenderer;
import vuforia.utils.SampleAppRendererControl;
import vuforia.utils.SampleApplicationSession;
import vuforia.utils.CubeShaders;
import vuforia.utils.LoadingDialogHandler;
import vuforia.utils.SampleApplication3DModel;
import vuforia.utils.SampleUtils;
import vuforia.utils.Arrow;
import vuforia.utils.Texture;
import vuforia.utils.models.BackArrow;
import vuforia.utils.models.BackLeftArrow;
import vuforia.utils.models.FrontLeftArrow;
import vuforia.utils.models.FrontRightArrow;

import java.io.IOException;
import java.util.Vector;

// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl
{
    private static final String LOGTAG = "ImageTargetRenderer";
    
    private SampleApplicationSession vuforiaAppSession;
    private ScanActivity mActivity;
    private SampleAppRenderer mSampleAppRenderer;

    private Vector<Texture> mTextures;
    
    private int shaderProgramID;
    private int vertexHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texSampler2DHandle;
    
    private Arrow mArrow;
    private LeftArrow mLArrow;
    private BackArrow mBArrow;
    private BackLeftArrow mBLArrow;
    private FrontLeftArrow mFLArrow;
    private FrontRightArrow mFRArrow;
    
    private float kBuildingScale = 2.0f;
    private SampleApplication3DModel mBuildingsModel;

    private boolean mIsActive = false;
    private boolean mModelIsLoaded = false;
    
    private static final float OBJECT_SCALE_FLOAT = 300.0f;

    protected boolean dataBool = false;
    protected int i = 0;
    private Intent returnIntent;



    public ImageTargetRenderer(ScanActivity activity, SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
        // SampleAppRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 10f , 5000f);
    }
    
    
    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content from SampleAppRenderer class
        mSampleAppRenderer.render();
    }
    

    public void setActive(boolean active)
    {
        mIsActive = active;

        if(mIsActive)
            mSampleAppRenderer.configureVideoBackground();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mSampleAppRenderer.onSurfaceCreated();
    }
    
    
    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        
        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mSampleAppRenderer.onConfigurationChanged(mIsActive);

        initRendering();
    }
    
    
    // Function for initializing the renderer.
    private void initRendering()
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);
        
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
            CubeShaders.CUBE_MESH_VERTEX_SHADER,
            CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "texSampler2D");

        if(!mModelIsLoaded) {
            mArrow = new Arrow();
            mLArrow = new LeftArrow();
            mBArrow = new BackArrow();
            mBLArrow = new BackLeftArrow();
            mFLArrow = new FrontLeftArrow();
            mFRArrow = new FrontRightArrow();

            try {
                mBuildingsModel = new SampleApplication3DModel();
                mBuildingsModel.loadModel(mActivity.getResources().getAssets(),
                        "ImageTargets/Buildings.txt");
                mModelIsLoaded = true;
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable to load buildings");
            }

            // Hide the Loading Dialog
            mActivity.loadingDialogHandler
                    .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        }
    }

    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by SampleAppRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix)
    {
        // Renders video background replacing Renderer.DrawVideoBackground()
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();
            printUserData(trackable);
            Matrix44F modelViewMatrix_Vuforia = Tool
                    .convertPose2GLMatrix(result.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            int textureIndex = 0;

            if(trackable.getName().equalsIgnoreCase("QRCode_1")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 1);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);

                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_2")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 2);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_3")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 3);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_4")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 4);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_5")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 5);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_6")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 6);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_7")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 7);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_8")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 8);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_9")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 9);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            if(trackable.getName().equalsIgnoreCase("QRCode_10")) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        // display the arrow for 3s
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnIntent = new Intent();
                                returnIntent.putExtra("data", dataBool = true);
                                returnIntent.putExtra("index", i = 10);
                                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                                mActivity.finish();

                            }
                        }, 3000);
                    }
                });
            }

            // deal with the modelview and projection matrices
            float[] modelViewProjection = new float[16];

            if (!mActivity.isExtendedTrackingActive()) {
                Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f,
                        OBJECT_SCALE_FLOAT);
                Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT,
                        OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
            } else {
                Matrix.rotateM(modelViewMatrix, 0, 90.0f, 1.0f, 0, 0);
                Matrix.scaleM(modelViewMatrix, 0, kBuildingScale,
                        kBuildingScale, kBuildingScale);
            }
            Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

            // activate the shader program and bind the vertex/normal/tex coords
            GLES20.glUseProgram(shaderProgramID);

            if (!mActivity.isExtendedTrackingActive()) {

                switch(mActivity.getArrowDir()) {
                    case "left":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mLArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mLArrow.getTexCoords());
                        break;

                    case "right":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                            false, 0, mArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                            GLES20.GL_FLOAT, false, 0, mArrow.getTexCoords());
                        break;

                    case "back":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mBArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mBArrow.getTexCoords());
                        break;

                    case "back-left":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mBLArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mBLArrow.getTexCoords());
                        break;

                    case "front-right":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mFRArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mFRArrow.getTexCoords());
                        break;

                    case "front-left":
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mFLArrow.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mFLArrow.getTexCoords());
                        break;

                    case "none":
                        break;
                    
                    default:
                        break;

                }

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                // activate texture 0, bind it, and pass to shader
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        mTextures.get(textureIndex).mTextureID[0]);
                GLES20.glUniform1i(texSampler2DHandle, 0);

                // pass the model view matrix to the shader
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                        modelViewProjection, 0);

                // finally draw the arrow
                switch(mActivity.getArrowDir()) {
                    case "left":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mLArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "right":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "back":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mBArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "back-left":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mBLArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "front-right":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mFRArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "front-left":
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mFLArrow.getNumObjectVertex());
                        mModelIsLoaded = false;
                        break;
                    case "none":
                        break;
                    default:
                        break;

                }

                // disable the enabled arrays
                GLES20.glDisableVertexAttribArray(vertexHandle);
                GLES20.glDisableVertexAttribArray(textureCoordHandle);
            } else {
                GLES20.glDisable(GLES20.GL_CULL_FACE);
                GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                        false, 0, mBuildingsModel.getVertices());
                GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                        GLES20.GL_FLOAT, false, 0, mBuildingsModel.getTexCoords());

                GLES20.glEnableVertexAttribArray(vertexHandle);
                GLES20.glEnableVertexAttribArray(textureCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        mTextures.get(3).mTextureID[0]);
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                        modelViewProjection, 0);
                GLES20.glUniform1i(texSampler2DHandle, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,
                        mBuildingsModel.getNumObjectVertex());

                SampleUtils.checkGLError("Renderer DrawBuildings");
            }

            SampleUtils.checkGLError("Render Frame");

        }


        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    }

    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }
    
    
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
        
    }

}
