package ca.furguardian.it.petwellness.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MealTimes {

    private String breakfast;
    private String lunch;
    private String dinner;

    // Required empty constructor for Firebase
    public MealTimes() {
    }

    public MealTimes(String breakfast, String lunch, String dinner) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
