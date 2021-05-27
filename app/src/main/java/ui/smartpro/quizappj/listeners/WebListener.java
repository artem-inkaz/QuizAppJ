package ui.smartpro.quizappj.listeners;

public interface WebListener {
    //будут определять поведение класса при загрузке данных, отображение прогресса, заголовка или ошибок загрузки
    public void onStart();
    public void onLoaded();
    public void onProgress(int progress);
    public void onNetworkError();
    public void onPageTitle(String title);
}
