package com.backmask.timelapse.infuse.sensor;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.backmask.timelapse.infuse.ISocketWriter;

import java.io.IOException;

public class CameraSensor implements ISensor, Camera.PreviewCallback {
    private Camera m_camera;
    private ISocketWriter m_writer;
    private CameraHelper m_cameraHelper;

    public CameraSensor(ViewGroup view, Context context) {
        m_cameraHelper = new CameraHelper(context);
        view.addView(m_cameraHelper.getView());
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (m_writer != null) {
            m_writer.write("sensor.camera", data);
        }
    }

    @Override
    public void attach(ISocketWriter writer) {
        if (m_writer != null || m_camera != null)
            detach();

        m_writer = writer;
        startCamera();
    }

    @Override
    public void detach() {
        if (m_camera != null) {
            m_camera.release();
            m_camera = null;
            m_cameraHelper.setCamera(null);
        }
        m_writer = null;
    }

    private void startCamera() {
        if (m_camera == null) {
            m_camera = Camera.open();
            m_camera.setPreviewCallback(this);
        }
        m_cameraHelper.setCamera(m_camera);
    }
}

class CameraHelper implements SurfaceHolder.Callback {
    private SurfaceView m_surfaceView;
    private SurfaceHolder m_surfaceHolder;
    private Camera m_camera;

    CameraHelper(Context context) {
        m_surfaceView = new SurfaceView(context);
        m_surfaceHolder = m_surfaceView.getHolder();
        m_surfaceHolder.addCallback(this);
    }

    public SurfaceView getView() {
        return m_surfaceView;
    }

    public void setCamera(Camera camera) {
        m_camera = camera;
        if (m_camera != null && m_surfaceHolder != null) {
            try {
                m_camera.setPreviewDisplay(m_surfaceHolder);
                m_camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        m_surfaceHolder = holder;
        if (m_camera != null) {
            try {
                m_camera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        m_surfaceHolder = null;
        if (m_camera != null) {
            m_camera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        m_surfaceHolder = holder;
        if (m_camera != null) {
            m_camera.startPreview();
        }
    }

}
