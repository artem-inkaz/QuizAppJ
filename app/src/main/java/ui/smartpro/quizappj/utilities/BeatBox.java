package ui.smartpro.quizappj.utilities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String SOUNDS_FOLDER = "all_sounds";
    private static final int MAX_SOUNDS = 5;
    private AssetManager mAssets;
    private List<SoundUtilities> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        //идеально подходит для многократного воспроизведения небольших звуковых файлов,
        // поскольку загружает их в память все сразу и воспроизводит оттуда, а это положительно влияет на производительность
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }
// с помощью класса SoundUtilities формирует список звуков для воспроизведения и
// передает их в SoundPool методом load
    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
        } catch (IOException ioe) {
            return;
        }

        for (String filename : soundNames) {
            try {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                SoundUtilities sound = new SoundUtilities(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load(SoundUtilities sound) throws IOException {
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }
//для воспроизведения звуков, мы будем вызывать его в классе тестирования
    public void play(SoundUtilities sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }
//для освобождения ресурсов проигрывателя
    public void release() {
        mSoundPool.release();
    }
//для получения списка звуковых файлов для воспроизведения
    public List<SoundUtilities> getSounds() {
        return mSounds;
    }
}
