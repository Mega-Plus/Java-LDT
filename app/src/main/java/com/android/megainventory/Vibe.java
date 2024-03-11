package com.android.megainventory;


import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Vibe {

    public static void vibrate(Context context, long milliseconds) {
        // Get instance of Vibrator from current Context
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Check if device supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for the specified milliseconds
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // New API for vibrate with vibration effect
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated method for vibrate, available for backward compatibility
                vibrator.vibrate(milliseconds);
            }
        }
    }
    public static void beep(Context context, int duration) {
        // Create a ToneGenerator instance
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        // Play a beep sound for the specified duration
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, duration);
    }
}
