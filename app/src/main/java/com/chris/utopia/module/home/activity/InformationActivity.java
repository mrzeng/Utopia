package com.chris.utopia.module.home.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chris.utopia.R;
import com.chris.utopia.common.constant.Constant;
import com.chris.utopia.common.util.SharedPrefsUtil;
import com.chris.utopia.common.util.StringUtil;
import com.chris.utopia.common.view.BaseActivity2;
import com.chris.utopia.common.view.CircleTransform;
import com.chris.utopia.entity.User;
import com.chris.utopia.module.home.presenter.ProfilePresenter;
import com.chris.utopia.module.home.presenter.ProfilePresenterImpl;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jianjianhong on 2016/12/7.
 */
public class InformationActivity extends BaseActivity2 implements ProfileActionView, View.OnClickListener {

    private ImageView profileIv;
    private TextInputLayout nameTi;
    private EditText nameEt;
    private TextInputLayout emailTi;
    private EditText emailEt;
    private TextInputLayout introduceTi;
    private EditText introduceEt;
    private AppCompatRadioButton manCb;
    private AppCompatRadioButton womenCb;
    private LinearLayout passwordView;
    private RadioGroup sexGroup;

    private MaterialDialog resetPwdDialog;
    private User user;
    private Bitmap myBitmap;
    private byte[] mContent;
    private String fileName;
    private String filePath;

    private ProfilePresenter profilePresenter = new ProfilePresenterImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutId(R.layout.activity_information);
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        profileIv = (ImageView) findViewById(R.id.profile_image);
        sexGroup = (RadioGroup) findViewById(R.id.rg_info_sex);
        nameTi = (TextInputLayout) findViewById(R.id.ti_info_name);
        emailTi = (TextInputLayout) findViewById(R.id.ti_info_email);
        introduceTi = (TextInputLayout) findViewById(R.id.ti_info_introduce);
        nameEt = (EditText) findViewById(R.id.et_info_name);
        emailEt = (EditText) findViewById(R.id.et_info_email);
        introduceEt = (EditText) findViewById(R.id.et_info_introduce);
        manCb = (AppCompatRadioButton) findViewById(R.id.cb_info_man);
        womenCb = (AppCompatRadioButton) findViewById(R.id.cb_info_women);
        passwordView = (LinearLayout) findViewById(R.id.view_info_password);
    }

    public void initData() {
        fileName = SharedPrefsUtil.getStringValue(getContext(), Constant.SP_KEY_LOGIN_USER_NAME, "")+".jpg";
        filePath = getContext().getFilesDir().getPath()+"/"+fileName;
        if(!new File(filePath).exists()) {
            Picasso.with(this).load(R.drawable.boy).transform(new CircleTransform()).fit().centerCrop().into(profileIv);
        }else {
            Picasso.with(this).load(new File(filePath)).transform(new CircleTransform()).fit().centerCrop().into(profileIv);
        }

        profilePresenter.setActionView(this);
        profilePresenter.loadUserInfo();
    }

    public void initEvent() {
        passwordView.setOnClickListener(this);
        profileIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_info_password:
                resetPwdDialog = new MaterialDialog.Builder(getContext())
                        .title("更改密码")
                        .customView(R.layout.dialog_reset_password, true)
                        .positiveText("确认")
                        .show();
                View dialogView = resetPwdDialog.getCustomView();
                final TextInputLayout pwTi = (TextInputLayout) dialogView.findViewById(R.id.resetPasswordDialog_password_textInput);
                final EditText pwEt = (EditText) dialogView.findViewById(R.id.resetPasswordDialog_password_et);
                final TextInputLayout npwTi = (TextInputLayout) dialogView.findViewById(R.id.resetPasswordDialog_new_password_textInput);
                final EditText npwEt = (EditText) dialogView.findViewById(R.id.resetPasswordDialog_new_password_et);
                final TextInputLayout cpwTi = (TextInputLayout) dialogView.findViewById(R.id.resetPasswordDialog_confirm_password_textInput);
                final EditText cpwEt = (EditText) dialogView.findViewById(R.id.resetPasswordDialog_confirm_password_et);

                resetPwdDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = true;
                        String pwd = pwEt.getText().toString();
                        String newPwd = npwEt.getText().toString();
                        String comfirmPwd = cpwEt.getText().toString();

                        if(StringUtil.isEmpty(pwd)) {
                            pwTi.setErrorEnabled(true);
                            pwTi.setError("旧密码不能为空");
                            flag = false;
                        }else {
                            pwTi.setErrorEnabled(false);
                            pwTi.setError(null);
                        }
                        if(StringUtil.isEmpty(newPwd)) {
                            npwTi.setErrorEnabled(true);
                            npwTi.setError("密码不能为空");
                            flag = false;
                        }else {
                            npwTi.setErrorEnabled(false);
                            npwTi.setError(null);
                        }
                        if(StringUtil.isEmpty(comfirmPwd)) {
                            cpwTi.setErrorEnabled(true);
                            cpwTi.setError("确认密码不能为空");
                            flag = false;
                        }else {
                            cpwTi.setErrorEnabled(false);
                            cpwTi.setError(null);
                        }
                        if(StringUtil.isNotEmpty(newPwd) && StringUtil.isNotEmpty(comfirmPwd)) {
                            if(!newPwd.equals(comfirmPwd)) {
                                cpwTi.setErrorEnabled(true);
                                cpwTi.setError("确认密码不正确");
                                flag = false;
                            }else {
                                cpwTi.setErrorEnabled(false);
                                cpwTi.setError(null);
                            }
                        }

                        if(flag) {
                            profilePresenter.resetPassword(pwd, newPwd);
                        }
                    }
                });
                break;
            case R.id.profile_image:
                final CharSequence[] items = { "相册", "拍照" };

                AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle("选择照片").setItems(items,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                //这里item是根据选择的方式，   在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
                                if(which==1){
                                    Intent getImageByCamera  = new Intent("android.media.action.IMAGE_CAPTURE");
                                    startActivityForResult(getImageByCamera, 1);
                                }else{
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                    getImage.setType("image/jpeg");
                                    startActivityForResult(getImage, 0);
                                }
                                showProgress("正在处理中，请稍后...");
                            }
                        }).create();
                dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hideProgress();
                    }
                });
                dlg.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable(){
            public void run(){
                ContentResolver contentResolver  =getContentResolver();
                if(requestCode==0){

                    //方式一
                    try {
                        //获得图片的uri
                        Uri orginalUri = data.getData();
                        //将图片内容解析成字节数组
                        mContent = readStream(contentResolver.openInputStream(Uri.parse(orginalUri.toString())));
                        //将字节数组转换为ImageView可调用的Bitmap对象
                        myBitmap  =getPicFromBytes(mContent,null);
                        ////把得到的图片绑定在控件上显示
                        //profileIv.setImageBitmap(myBitmap);
                        saveBitmap();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }

                }else if(requestCode==1){
                    try {
                        Bundle extras = data.getExtras();
                        myBitmap = (Bitmap) extras.get("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.JPEG , 100, baos);
                        mContent = baos.toByteArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                    //profileIv.setImageBitmap(myBitmap);
                    saveBitmap();
                }
            }}
        ).start();


    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }



    public static byte[] readStream(InputStream in) throws Exception{
        byte[] buffer  =new byte[1024];
        int len  =-1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while((len=in.read(buffer))!=-1){
            outStream.write(buffer, 0, len);
        }
        byte[] data  =outStream.toByteArray();
        outStream.close();
        in.close();
        return data;
    }

    public void saveBitmap() {
        File f = new File(getContext().getFilesDir().getPath(), fileName);
        if (f.exists()) {
            f.delete();
            Picasso.with(getContext()).invalidate(f);
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    //更新UI
                    Picasso.with(getContext()).load(new File(filePath)).transform(new CircleTransform()).fit().centerCrop().error(R.drawable.boy).into(profileIv);
                }

            });

            Log.i("Chris", "头像已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    hideProgress();
                }

            });

        }

    }

    @Override
    public void loadUserInfo(User user) {
        this.user = user;
        nameEt.setText(user.getName());
        if("男".equals(user.getGender())) {
            manCb.setChecked(true);
        }else if("女".equals(user.getGender())) {
            womenCb.setChecked(true);
        }
        emailEt.setText(user.getEmail());
        introduceEt.setText(user.getIntroduce());
    }

    @Override
    public void setToolBarTitle() {
        toolbar = (Toolbar) findViewById(R.id.activity_toolBar);
        toolbar.setTitle("编辑个人信息");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_role_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rcAct_action_save:
                String name = nameEt.getText().toString();
                if(StringUtil.isEmpty(name)) {
                    nameTi.setErrorEnabled(true);
                    nameTi.setError("Title不能为空");
                    return true;
                }else {
                    nameTi.setErrorEnabled(false);
                    nameTi.setError(null);
                }
                user.setName(name);
                String radiovalue = ((RadioButton) findViewById(sexGroup.getCheckedRadioButtonId())).getText().toString();
                user.setGender(radiovalue);
                user.setEmail(emailEt.getText().toString());
                user.setIntroduce(introduceEt.getText().toString());
                profilePresenter.saveUser(user);
                return true;
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showResetPasswordFail(String message) {
        resetPwdDialog.dismiss();
        new MaterialDialog.Builder(getContext())
                .title(R.string.dialog_title)
                .content(message)
                .show();
    }

    @Override
    public void showResetPasswordSuccess(String message) {
        resetPwdDialog.dismiss();
        showMessage(message);
    }

    @Override
    public void showPasswordError(String message) {
        View view = resetPwdDialog.getCustomView();
        TextInputLayout pwTi = (TextInputLayout) view.findViewById(R.id.resetPasswordDialog_password_textInput);
        pwTi.setErrorEnabled(true);
        pwTi.setError(message);
    }

    @Override
    public void showSaveUserMessage(String message) {
        showMessage(message);
    }
}
