package com.phonepartner.frgment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phonepartner.BaseFragment;
import com.phonepartner.R;
import com.phonepartner.View.RoundedImageView;
import com.phonepartner.activity.AboutActivity;
import com.phonepartner.activity.EditPwdActivity;
import com.phonepartner.activity.HelpActivity;
import com.phonepartner.activity.LoginActivity;
import com.phonepartner.activity.PersponalActivity;
import com.phonepartner.popupwindow.MyPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * to handle interaction events.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "123";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICTURE_SELECT =1;
    private static final int TAKE_PHOTO=2;
    private String mParam1;
    private String mParam2;
    private RoundedImageView headportrait;
    private RoundedImageView mine_sex;
    private TextView username;
    private TextView userContext;
    private RelativeLayout rvPersonal;
    private RelativeLayout rvDeviceManager;
    private RelativeLayout rvEditPwd;
    private RelativeLayout rvAbout;
    private RelativeLayout rvHelp;
    private Button btn_logout;
    private String[] mListText = { "从相册选择", "拍一张" };
    private Uri imageUri;
    public PersonalFragment() {
        // Required empty public constructor
    }


    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
       // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView(view);
        initEvent();
        return view;
    }


    private void initView(View view) {
        headportrait = (RoundedImageView) view.findViewById(R.id.headportrait);
        mine_sex = (RoundedImageView) view.findViewById(R.id.mine_sex);
        username = (TextView) view.findViewById(R.id.username);
        userContext = (TextView) view.findViewById(R.id.userContext);
        rvPersonal = (RelativeLayout) view.findViewById(R.id.rvPersonal);
        rvDeviceManager = (RelativeLayout) view.findViewById(R.id.rvDeviceManager);
        rvEditPwd = (RelativeLayout) view.findViewById(R.id.rvEditPwd);
        rvAbout = (RelativeLayout) view.findViewById(R.id.rvAbout);
        rvHelp = (RelativeLayout) view.findViewById(R.id.rvHelp);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);

    }

    private void initEvent() {
        rvPersonal.setOnClickListener(this);
        rvDeviceManager.setOnClickListener(this);
        rvEditPwd.setOnClickListener(this);
        rvAbout.setOnClickListener(this);
        rvHelp.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        headportrait.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headportrait:
                showPopMenu(v);
                break;
            case R.id.rvPersonal:
                startActivity(PersponalActivity.class);
                break;
            case R.id.rvDeviceManager:
                break;
            case R.id.rvEditPwd:
                startActivity(EditPwdActivity.class);
                break;
            case R.id.rvAbout:
                startActivity(AboutActivity.class);
                break;
            case R.id.rvHelp:
                startActivity(HelpActivity.class);
                break;
            case R.id.btn_logout://退出登录
               startActivity(LoginActivity.class);
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==RESULT_OK){
            if(requestCode == PICTURE_SELECT){
                imageUri = data.getData();
            }
            //Toast.makeText(IndexActivity.this,data.getData().toString(),Toast.LENGTH_SHORT).show();Log.d("ttttttt", data.getData().toString());
            if(imageUri !=null){
                Log.d("ttttttt", imageUri.toString());
                try {
                    InputStream is=getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    headportrait.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private void showPopMenu(View v) {
        new MyPopupWindow(getActivity(), new MyPopupWindow.ResultListener() {

            @Override
            public void onResultChanged(int index) {
                Intent intent = new Intent();
                switch (index) {
                    case 0:
                        //  Toast.makeText(IndexActivity.this, "111", Toast.LENGTH_SHORT).show();
                        choseHeadImageFromGallery(intent);

                        break;
                    case 1:
                        setTakePhoto();
                        break;
                    default:
                        break;
                }
            }
        }, v, mListText);
    }
    private void choseHeadImageFromGallery(Intent intent) {

        intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICTURE_SELECT);
    }

    private void setTakePhoto() {
        //创建File对象，用于存储拍照后的图片

        File takephoto = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");

        try {
            //  File takephoto = new File(Environment.getExternalStorageDirectory() + "/com.hechamall/takephoto/1.png");
            if (takephoto.exists()) {
                takephoto.delete();
            }
            takephoto.createNewFile();
            imageUri = Uri.fromFile(takephoto);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
