package ui.smartpro.quizappj.utilities;

//вспомогательный класс для подготовки путей к звуковым ресурсам и работы с их свойствами
public class SoundUtilities {
    //Переменные будут хранить пути к звукам, имена и идентификаторы файлов
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;
//В конструктор передаем путь к файлу, выполняем над ним необходимые преобразования и получаем имя ресурса
    public SoundUtilities(String assetPath) {
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        mName = filename.replace(".wav", "");
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
