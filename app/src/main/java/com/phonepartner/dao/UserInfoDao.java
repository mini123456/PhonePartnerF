package com.phonepartner.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.Dbhelper;


/**
 * Created by cwj on 2016/10/18 0018.
 */
public class UserInfoDao {
    private Context mContext;

    public UserInfoDao(Context context) {
        mContext = context;
    }

    public boolean intsert(UserInfo u){

        boolean result =false;
        Dbhelper dbhelper = new Dbhelper(mContext);
        SQLiteDatabase database = dbhelper.getWritableDatabase();
        try {
            String[] args ;
            args =new String[]{u.getName(),u.getPwd(),u.getUid(),u.getMemoName(),u.getEmergenceName1(),u.getEmergencePhone1()};
            String sql = "insert into UserInfo(phone,pwd,uid,name,emName,emPhone) values(?,?,?,?,?,?)";
            database.execSQL(sql,args);
            result =true;
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(database !=null){
                database.close();
            }
        }
        return result;
    }
    public UserInfo login(UserInfo u){

        String uid ="";
     // int count =0;
        Dbhelper dbhelper = new Dbhelper(mContext);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        try {
//        String sql
//                ="SELECT COUNT(ID) AS exist FROM UserInfo WHERE name=? AND pwd=?";
        String[] args ;
        args =new String[]{u.getName(),u.getPwd()};
      // Cursor cursor = database.rawQuery(sql,args);
            Cursor cursor =  database.query("UserInfo", new String[]{"id","phone","pwd","uid","name","emName","emPhone", "email",
                    "address", "emName2","emPhone2",
                    "emName3","emPhone3"}, "phone=? AND pwd=?", new String[]{u.getName(),u.getPwd()}, null, null, null);
//            //判断游标是否为空
//            if(cursor.moveToFirst()) {
////遍历游标
//                for(int i=0;i<cursor.getCount();i++){
//                    cursor.move(i);
////获得ID
//                    int id = cursor.getInt(0);
////获得用户名
//                    String username=cursor.getString(1);
////获得密码
//                    String password=cursor.getString(2);
//                    uid =cursor.getString(3);
//                }
//            }
        while (cursor.moveToNext()){
            //count++;
            String sql2 = "create TABLE UserInfo(" +
                    "id INTEGER PRIMARY KEY, " +
                    "phone VARCHAR(20),pwd VARCHAR(20)," +
                    "uid VARCHAR(20),name VARCHAR(20)," +
                    "emName VARCHAR(20),emPhone VARCHAR(20)," +
                    "photo VARCHAR(20)," +
                    "email VARCHAR(20)," +
                    "address VARCHAR(20)," +
                    "emName2 VARCHAR(20),emPhone2 VARCHAR(20)," +
                    "emName3 VARCHAR(20),emPhone3 VARCHAR(20)) ";
            int id = cursor.getInt(0);
            String userName = cursor.getString(1);
            String userPwd = cursor.getString(2);
            uid = cursor.getString(3);
            String name = cursor.getString(4);
            String emname = cursor.getString(5);
            String emphone = cursor.getString(6);
            String email = cursor.getString(7);
            String address = cursor.getString(8);
            String emname2 = cursor.getString(9);
            String emPhone2 = cursor.getString(10);
            String emname3 = cursor.getString(11);
            String enohne3 = cursor.getColumnName(12);
            u.setUid(uid);
            u.setName(userName);
            u.setPwd(userPwd);
            u.setMemoName(name);
            u.setEmergenceName1(emname);
            u.setEmergencePhone1(emphone);
            u.setMail(email);
            u.setAddress(address);
            u.setEmergenceName2(emname2);
            u.setEmergencePhone2(emPhone2);
            u.setEmergenceName3(emname3);
            u.setEmergencePhone3(enohne3);
        }
            //cursor.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(database !=null){
                database.close();
            }

        }
        return u;
    }

//修改用户@Override

   //更新用户
    // int uid= Integer.parseInt(editID.getText().toString());
    //    UserInfo u3=new UserInfo(uid,editName.getText().toString(),editPwd.getText().toString());
    //  boolean r3=new UserDaoImpl(this).updatePwd(u3);
    // if(r3){
    //  result="修改成功";
    // }else{
    //    result="修改失败";
    //  }
    //  textResult.setText(result);


    public boolean updatePwd(UserInfo user)
{
    Dbhelper dbhelper = new Dbhelper(mContext);
   // SQLiteDatabase database = dbhelper.getReadableDatabase();
    SQLiteDatabase database =dbhelper.getWritableDatabase();
    boolean result=false;
    String sql="update userInfo set pwd=? where name=?";
    Object[] args=new Object[]{user.getName(),user.getPwd()};

    try {
        database.execSQL(sql,args);
        result=true;   }
    catch (Exception e)
    {
        e.printStackTrace();    }
    finally {
        if (database != null) {
            database.close();
        }
    }
    return result;
}

    public boolean updatePersonal(UserInfo user)
    {
        Dbhelper dbhelper = new Dbhelper(mContext);
        // SQLiteDatabase database = dbhelper.getReadableDatabase();
        SQLiteDatabase database =dbhelper.getWritableDatabase();
        boolean result=false;

        String sql="update userInfo set name=?,email=?,address=?,emName=?,emPhone=?," +
                "emName2=?,emPhone2=?,emName3=?,emPhone3=?" +
                " where uid=?";
        Object[] args=new Object[]{user.getMemoName(),user.getMail(),user.getAddress(),user.getEmergenceName1(),
                user.getEmergencePhone1(),user.getEmergenceName2(),user.getEmergencePhone2(),
                user.getEmergenceName3(),user.getEmergencePhone3(),user.getUid()};

        try {
            database.execSQL(sql,args);
            result=true;   }
        catch (Exception e)
        {
            e.printStackTrace();    }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return result;
    }


}


