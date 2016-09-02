package com.mobeta.android.demodslv;

import com.chalilayang.test.R;
import com.mobeta.android.dslv.DragSortController;

import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mobeta.android.dslv.DragSortController;

/**
 * Sets drag init mode on DSLV controller passed into ctor.
 */
public class DragInitModeDialog extends DialogFragment {

    private static final String ARG_PARAM1 = "DragInitModeDialog";
    private DragSortController mControl;

    private int mDragInitMode;

    private DragOkListener mListener;

    public DragInitModeDialog() {
        super();
        mDragInitMode = DragSortController.ON_DOWN;
    }

    public static DragInitModeDialog newInstance(int param2) {
        DragInitModeDialog fragment = new DragInitModeDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface DragOkListener {
        public void onDragOkClick(int removeMode);
    }

    public void setDragOkListener(DragOkListener l) {
        mListener = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mDragInitMode  = getArguments().getInt(ARG_PARAM1);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_remove_mode)
                .setSingleChoiceItems(R.array.drag_init_mode_labels, mDragInitMode,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDragInitMode = which;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onDragOkClick(mDragInitMode);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
    
        return builder.create();
    }
}
