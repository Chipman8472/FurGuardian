package ca.furguardian.it.petwellness.ui.peted;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
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