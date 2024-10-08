package ca.furguardian.it.petwellness.ui.peted;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PetEdViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PetEdViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}