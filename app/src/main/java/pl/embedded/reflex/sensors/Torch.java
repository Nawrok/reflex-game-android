package pl.embedded.reflex.sensors;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;

public class Torch
{
    private final CameraManager cameraManager;

    public Torch(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public void flash()
    {
        setMode(true);
        new Handler().postDelayed(() -> setMode(false), 150);
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

