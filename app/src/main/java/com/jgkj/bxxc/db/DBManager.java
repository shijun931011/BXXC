package com.jgkj.bxxc.db;

import android.content.Context;

import com.jgkj.bxxc.bean.entity.Sub4ProjectEntity.Sub4ProjectEntity;
import com.jgkj.bxxc.bean.entity.SubProjectEntity.SubProjectEntity;
import com.jgkj.bxxc.tools.MyApplication;
import org.greenrobot.greendao.query.Query;
import java.util.List;

public class DBManager {
    private static Context mContext;
    private static DBManager instance;
    public SubProjectEntityDao subProjectEntityDao;
    public Sub4ProjectEntityDao sub4ProjectEntityDao;

    public static DBManager getInstance(Context context){
        if(null == instance){
            instance = new DBManager();
            if(mContext == null){
                mContext = context;
            }
            DaoSession session = MyApplication.getDaoSession(mContext);
            instance.subProjectEntityDao = session.getSubProjectEntityDao();
            instance.sub4ProjectEntityDao = session.getSub4ProjectEntityDao();

        }
        return instance;
    }

    public static DBManager getInstance(){
        return instance;
    }

    /**
     * 插入list
     * @param list
     */
    public void insertSubProject(List<SubProjectEntity> list){
        subProjectEntityDao.insertOrReplaceInTx(list);
    }

    /**
     * 删除list
     */
    public void delSubProject(){
        subProjectEntityDao.deleteAll();
    }

    /**
     * 查询
     * @return
     */
    public List<SubProjectEntity> getSubProject(){
        Query<SubProjectEntity> query = subProjectEntityDao.queryBuilder().build();
        return query.list();
    }

    /**
     * 插入list
     * @param list
     */
    public void insertSub4Project(List<Sub4ProjectEntity> list){
        sub4ProjectEntityDao.insertOrReplaceInTx(list);
    }

    /**
     * 删除list
     */
    public void del4SubProject(){
        sub4ProjectEntityDao.deleteAll();
    }

    /**
     * 查询
     * @return
     */
    public List<Sub4ProjectEntity> getSub4Project(){
        Query<Sub4ProjectEntity> query = sub4ProjectEntityDao.queryBuilder().build();
        return query.list();
    }

}
