package com.apoim.groupchatwebrtc.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apoim.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.apoim.groupchatwebrtc.utils.ResourceUtils;

public class PreviewFragment extends Fragment {

    public static final String PREVIEW_IMAGE = "preview_image";


    public static Fragment newInstance(int imageResourceId) {
        PreviewFragment previewFragment = new PreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PREVIEW_IMAGE, imageResourceId);
        previewFragment.setArguments(bundle);
        return previewFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_screen_share, container, false);
      /*  Glide.with(this)
                .load(getArguments().getInt(PREVIEW_IMAGE))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(ResourceUtils.getDimen(R.dimen.pager_image_width),
                        ResourceUtils.getDimen(R.dimen.pager_image_height))
                .into((ImageView) view.findViewById(R.id.image_preview));*/

      Glide.with(this).load(getArguments().getInt(PREVIEW_IMAGE)).into((ImageView) view.findViewById(R.id.image_preview));
        return view;
    }
}
