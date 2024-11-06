package ca.furguardian.it.petwellness.controller;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
public class Format {

    public static String formatEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }
}
