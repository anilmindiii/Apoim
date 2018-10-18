package com.apoim.fragment.eventFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.apoim.ImagePickerPackge.ImagePicker;
import com.apoim.R;
import com.apoim.activity.event.CreateEventActivity;
import com.apoim.activity.profile.EditProfileActivity;
import com.apoim.adapter.newEvent.EventImgeUploadAdapter;
import com.apoim.app.Apoim;
import com.apoim.helper.Constant;
import com.apoim.listener.ProfileImageAdapterListener;
import com.apoim.modal.EventDetailsInfo;
import com.apoim.modal.ImageBean;
import com.apoim.modal.SignInInfo;
import com.apoim.server_task.WebService;
import com.apoim.session.Session;
import com.apoim.util.InsLoadingView;
import com.apoim.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.apoim.activity.event.CreateEventActivity.friendsIds;

/**
 * Created by mindiii on 14/9/18.
 */

public class FourthScreenFragment extends Fragment {
    private TextView tv_select_background_four, tv_four;
    private Context mContext;

    private TextView tv_select_background_three, tv_three;
    private ImageView iv_right_three,iv_back;
    private InsLoadingView loadingView;
    private Session session;
    private RecyclerView event_horizontal_recycler;
    private List<ImageBean> imageBeans;
    private Bitmap bitmap;
    private EventImgeUploadAdapter adapter;
    private String eventId = "";
    private String imageId = "";
    private TextView tv_skip;

    public static FourthScreenFragment newInstance(String eventId,String imageId) {

        Bundle args = new Bundle();

        FourthScreenFragment fragment = new FourthScreenFragment();
        fragment.setArguments(args);
        args.putString(Constant.eventId,eventId);
        args.putString("imageId",imageId);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_fourth_screen, container, false);

        session = new Session(mContext);

        if(getArguments() != null){
            eventId = getArguments().getString(Constant.eventId);
            imageId = getArguments().getString("imageId");
        }

        tv_select_background_four = getActivity().findViewById(R.id.tv_select_background_four);
        iv_back = getActivity().findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        tv_four = getActivity().findViewById(R.id.tv_four);
        tv_select_background_three = getActivity().findViewById(R.id.tv_select_background_three);
        tv_three = getActivity().findViewById(R.id.tv_three);
        iv_right_three = getActivity().findViewById(R.id.iv_right_three);
        loadingView = view.findViewById(R.id.loadingView);
        event_horizontal_recycler = view.findViewById(R.id.event_horizontal_recycler);
        tv_skip = view.findViewById(R.id.tv_skip);

        tv_select_background_four.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_four.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        imageBeans = new ArrayList<>();
        imageBeans.add(0, null);


        if((session.getcreateEventInfo().firstImage) != null){
            bitmap = getBitmap(session.getcreateEventInfo().firstImage);
            imageBeans.add(1, new ImageBean(null, bitmap, imageId));
        }


        adapter = new EventImgeUploadAdapter(imageBeans, new ProfileImageAdapterListener() {
            @Override
            public void getPosition(int position) {
                if (position == 0) {
                    getPermissionAndPicImage();
                }
            }
        });
        event_horizontal_recycler.setAdapter(adapter);

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        return view;
    }

    private Bitmap getBitmap(String encoded) {
        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {

                bitmap = ImagePicker.getImageFromResult(mContext, requestCode, resultCode, data);

                if (bitmap != null) {
                    if (imageBeans.size() < 6) {

                        imageUploadTask(eventId, bitmap);
                        imageBeans.add(1, new ImageBean(null, bitmap, "0"));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tv_select_background_four.setBackgroundResource(R.drawable.holo_circle_border);
        tv_four.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        tv_select_background_three.setBackgroundResource(R.drawable.primary_circle_solid);
        tv_three.setVisibility(View.VISIBLE);
        tv_three.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        iv_right_three.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);

        FourthScreenFragment youTubePlayerFragment = (FourthScreenFragment) getChildFragmentManager().findFragmentById(R.id.event_fragment_place);
        if (youTubePlayerFragment != null) {
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();
        }
    }

    private void imageUploadTask(final String userId, final Bitmap bitmap) {
        loadingView.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("eventId", userId);

        Map<String, Bitmap> bitmapMap = new HashMap<>();
        bitmapMap.put("eventImage", bitmap);

        WebService service = new WebService(mContext, Apoim.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                loadingView.setVisibility(View.GONE);
                Log.e("RESPONSE", response);

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    JSONObject object_ = jsonObject.getJSONObject("data");
                    JSONObject imgObj = object_.getJSONObject("event");
                    String imgId  =  imgObj.getString("eventImgId");

                    if (status.equals("success")) {

                        imageBeans.add(1, new ImageBean(null, bitmap, imgId));

                    } else {
                        Utils.openAlertDialog(mContext, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingView.setVisibility(View.GONE);
                }


            }

            @Override
            public void ErrorListener(VolleyError error) {
                loadingView.setVisibility(View.GONE);
            }
        });
        service.callMultiPartApi("event/addEventImage", map, bitmapMap);
    }

}
