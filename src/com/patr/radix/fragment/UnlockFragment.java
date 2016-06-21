package com.patr.radix.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.felipecsl.gifimageview.library.GifImageView;
import com.patr.radix.MyKeysActivity;
import com.patr.radix.R;
import com.patr.radix.utils.Utils;
import com.patr.radix.view.GifView;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class UnlockFragment extends Fragment implements OnClickListener {
    
    private TitleBarView titleBarView;
    
    private GifImageView gifView;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unlock, container, false);
		titleBarView = (TitleBarView) view.findViewById(R.id.unlock_titlebar);
		titleBarView.hideBackBtn().showSelectKeyBtn().setTitle("AAA钥匙");
		titleBarView.setOnSelectKeyClickListener(this);
		gifView = (GifImageView) view.findViewById(R.id.unlock_giv);
		byte[] bytes;
        try {
            bytes = Utils.input2byte(getResources().openRawResource(
                    R.raw.shake_shake));
            gifView.setBytes(bytes);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return view;
	}

	@Override
	public void setArguments(Bundle args) {
		// TODO Auto-generated method stub
		super.setArguments(args);
	}

    @Override
    public void onStart() {
        super.onStart();
        gifView.startAnimation();
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.titlebar_select_key_btn:
            MyKeysActivity.start(getActivity());
            break;
        }
    }

}
