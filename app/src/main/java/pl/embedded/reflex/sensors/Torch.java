package pl.embedded.reflex.sensors;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;

public class Torch
{
    private final CameraManager cameraManager;
    private final Handler handler;

    public Torch(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
        this.handler = new Handler();
    }

    public void flash()
    {
        handler.post(() -> setMode(true));
        handler.postDelayed(() -> setMode(false), 150);
    }

    private void setMode(boolean enable)
    {
        try
        {
            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], enable);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}

