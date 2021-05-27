package ui.smartpro.quizappj.models;

import android.os.Parcel;
import android.os.Parcelable;
//Имплементируем интерфейс Parcelable и реализуем его методы.
// Parcelable используется для передачи объектов между активити
public class CategoryModel implements Parcelable {

    String categoryId;
    String categoryName;

    public CategoryModel(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
//описывает различного рода специальные объекты, описывающие интерфейс
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(categoryId);
        dest.writeString(categoryName);

    }

    public CategoryModel(Parcel in) {
        categoryId = in.readString();
        categoryName = in.readString();
    }

    public static Creator<CategoryModel> getCreator() {
        return CREATOR;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel source) {
            return new CategoryModel(source);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };
}
