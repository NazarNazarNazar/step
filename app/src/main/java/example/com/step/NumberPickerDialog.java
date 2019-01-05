package example.com.step;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerDialog extends AppCompatDialogFragment {
    private NumberDialogListener mListener;

    private static final String TAG = NumberDialogListener.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog : called");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        final NumberPicker numberPicker = view.findViewById(R.id.add_number);
        numberPicker.setMinValue(1000);
        numberPicker.setMaxValue(100000);
        numberPicker.setValue(1000);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // every step of numberPricker +- 1000
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
                int step = 1000;
                picker.setValue(newVal < oldVal ? oldVal - step : oldVal + step);
            }
        });

        // save number of steps
        view.findViewById(R.id.save_target_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "saveTargetButton : clicked");

                mListener.applyNumber(String.valueOf(numberPicker.getValue()));
                dismiss();
            }
        });

        // cancel number of steps
        view.findViewById(R.id.cancel_target_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "cancelTargetButton : clicked");
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            mListener = (NumberDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e + "must implement NumberDialogListener");
        }
    }

    // pass to the main activity new selected number of steps
    public interface NumberDialogListener {
        void applyNumber(String num);
    }
}
