package com.me.restaurantsmartsearch.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.me.restaurantsmartsearch.activity.MainActivity;

/**
 * Created by Laptop88T on 2/9/2017.
 */
public abstract class BaseFragment extends Fragment {
    public interface IOnFragmentChangeListener {
        void onFragmentStart(BaseFragment fragment);

        void onFragmentStop(BaseFragment fragment);
    }

    private IOnFragmentChangeListener mOnFragmentChangeListener;
    private Handler myHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getTagName(), "#onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(getTagName(), "#onAttach");
        if (getActivity() != null && getActivity() instanceof IOnFragmentChangeListener) {
            mOnFragmentChangeListener = (IOnFragmentChangeListener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(getTagName(), "#onDetach");
        mOnFragmentChangeListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(getTagName(), "#onStart");
        if (mOnFragmentChangeListener != null) {
            mOnFragmentChangeListener.onFragmentStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(getTagName(), "#onStop");
        myHandler.removeCallbacksAndMessages(null);
        if (mOnFragmentChangeListener != null) {
            mOnFragmentChangeListener.onFragmentStop(this);
        }
    }

    protected MainActivity getMainActivity() {
//        Logger.d(getTagName(), "#getMainActivity");
        try {
            return (MainActivity) getActivity();
        } catch (ClassCastException ex) {
            Log.e(getTagName(), "Can't cast to MainActivity !");
        } catch (NullPointerException ex) {
            Log.e(getTagName(), "Activity null!");
        }
        return null;
    }

    public abstract String getTagName();

    public String getTitle() {
        return getTagName();
    }

    public void showLog(String message) {
        Log.d(getTagName(), message);
    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int redId) {
        Toast.makeText(getContext(), redId, Toast.LENGTH_SHORT).show();
    }

    public void hideSoftKeyboard() {
        if (getMainActivity() != null && getMainActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getMainActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getMainActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText editText) {
        if (getMainActivity() != null) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

}
