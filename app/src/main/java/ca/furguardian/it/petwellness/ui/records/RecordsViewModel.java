//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563

package ca.furguardian.it.petwellness.ui.records;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecordsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RecordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(" ");
    }

    public LiveData<String> getText() {
        return mText;
    }
}