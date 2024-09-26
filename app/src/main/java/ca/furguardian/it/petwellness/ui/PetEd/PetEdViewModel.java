package ca.furguardian.it.petwellness.ui.PetEd;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PetEdViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PetEdViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is another fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}