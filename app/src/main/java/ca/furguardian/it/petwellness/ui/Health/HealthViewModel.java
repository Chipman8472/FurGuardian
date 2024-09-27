package ca.furguardian.it.petwellness.ui.Health;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HealthViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Health fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}