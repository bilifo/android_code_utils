package 通用_listview的adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import 通用_item组合布局对应类.ItemXmlClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;



/**
 * 该类依赖ListViewItem,需和ListViewItem一起使用.该类比起废弃的ComAdapter,不需要关心itemview的各个控件的声明和赋值
 * 
 * 适用于listview(其他的adapterView待验证)的简单adapter封装.使用时自建adapter继承该类,实现getItemViewImpl
 * 		
 * 完成了动态添加/更改/删除子item的功能.新添加到的数据会先放到mDataCache中,在序号到达后更新进mData.
 * mdata会根据序号自动对数据进行升排序.
 * 尽可能的对数据和子布局进行隔离.
 * getview还可以改进
 * 
 * 生命周期:getItemViewImpl()是在getview里面的
 *
 * @param <T> T表示数据类型
 * @author PanJunLong
 */
public abstract class ComAdapter2<T> extends BaseAdapter {
    protected LayoutInflater mLayoutInflater;
    protected Context mContext;// 为了使子类能够使用，把private改成protected
    protected List<DataCacheItem> mData = new ArrayList<DataCacheItem>();// 泛型T，子类实现时，必须指明T的类型
    protected Set<DataCacheItem> mDataCache = new TreeSet<DataCacheItem>();// 针对还没有该数据时的状况
    boolean isNewState = false;// mDataCache是否是最新状态

    public ComAdapter2(Context context, List<T> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;

        if (mData != null) {
            this.mDataCache.clear();
            this.mDataCache.addAll(new DataCacheItemUtils().listToSet(mData));
            updataDataState();
        }
    }


    /**
     * 看数据是否有变化,更新数据状态
     */
    public void updataDataState() {
        if (isNewState == false) {
            mData = new ArrayList<DataCacheItem>(mDataCache);
            isNewState = true;
        } else {

        }
    }

    @Override
    public int getCount() {
        return mDataCache == null ? 0 : mDataCache.size();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 获得单个数据对象
     */
    @Override
    public T getItem(int position) {
        updataDataState();
        return mData.get(position).t;
    }

    /**
     * 获得???
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 更新全部数据
     *
     * @param datas
     * @return
     */
    public ComAdapter2<T> updataAllDatas(List<T> datas) {
        mDataCache.clear();
        mDataCache.addAll(new DataCacheItemUtils().listToSet(datas));
        isNewState = false;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 新增和更新单个item数据
     *
     * @param position
     * @param itemData
     * @return
     */
    public ComAdapter2<T> updataItemData(int position, T itemData) {

        // new IndexOutOfBoundsException().printStackTrace();
        // list.size还没有position大，先把数据放到mDataCache中
        DataCacheItem newItemDataCache = new DataCacheItem(position, itemData);
        mDataCache.add(newItemDataCache);
        isNewState = false;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 删除单个item数据
     *
     * @param position
     * @return
     */
    public ComAdapter2<T> removeItemData(int position) {
        DataCacheItem newItemDataCache = mData.get(position);
        mDataCache.remove(newItemDataCache);
        isNewState = false;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 更新单个item数据并显示到界面上 （常用于监听等能取得convertView和parent的方法里）
     *
     * @param position
     * @param convertView
     * @param parent
     * @param itemData    新数据（传入adapter的数据要秉承一个原则，即它是一个item所需数据的集合，而不是零散的int、string什么的）
     * @return
     */
    public ComAdapter2<T> updataItemView(int position, View convertView, ViewGroup parent, T itemData) {
        updataItemData(position, itemData);
        getView(position, convertView, parent);
        return this;
    }
//
    /**
     * 更新单个item数据并显示到界面上 （常用不容易取得convertView和parent的方法里，如单个button中）
     *
     * @param position
     * @param adapterview 所有能setadapter的控件的父类
     * @param itemData    新数据（传入adapter的数据要秉承一个原则，即它是一个item所需数据的集合，而不是零散的int、string什么的）
     * @return
     */
    public ComAdapter2<T> updataItemView(int position, AdapterView adapterview, T itemData) {
        updataItemData(position, itemData);
        int visibleFirstPosi = adapterview.getFirstVisiblePosition();
        int visibleLastPosi = adapterview.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {

            T t = getItem(position);
            if (t != null) {
                View view = adapterview.getChildAt(position - visibleFirstPosi);
                ItemXmlClass viewItem = (ItemXmlClass) view.getTag();
                viewItem.setData(itemData);
            }
        } else if (position > visibleLastPosi) {// position超出了现在listview的范围。
        }
        return this;
    }


    /**
     * 更新单个item数据并显示到界面上 （常用不容易取得convertView和parent的方法里，如单个button中）
     *
     * @param position
     * @param adapterview 所有能setadapter的控件的父类
     *                    新数据（传入adapter的数据要秉承一个原则，即它是一个item所需数据的集合，而不是零散的int、string什么的）
     * @return
     */
    public ComAdapter2<T> removeItemView(int position, AdapterView adapterview) {
        removeItemData(position);

        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T t = getItem(position);
        ItemXmlClass mlistViewItem = null;
        if (convertView == null) {
            mlistViewItem = getItemViewImpl(mContext);

        } else {
            mlistViewItem = (ItemXmlClass) convertView.getTag();
        }
        mlistViewItem.setData(t);
        convertView = mlistViewItem.getRootView();
        convertView.setTag(mlistViewItem);
        return convertView;
    }

    public abstract ItemXmlClass getItemViewImpl(Context context);

    /**
     * 和DataCache配合使用的类，以position作为位置判断标记,对于新加入的DataCacheItem元素会自动排序.
     * 当然这个是有漏洞的,如我已经删除了position以前的所有元素,由于position没有重排机制,第一个元素将会是position=20,
     * 此时,我想在第5行加入新元素,会发现position=5,会把新元素插到首位.如果中间删除的中间的某位,这样极易导致数据添加混乱.
     *
     * @author PanJunLong
     */
    class DataCacheItem implements Comparable<DataCacheItem> {
        int position;
        T t;

        private DataCacheItem(int position, T t) {
            super();
            this.position = position;
            this.t = t;
        }

        @Override
        public int compareTo(ComAdapter2<T>.DataCacheItem arg0) {
            if (this.position > arg0.position) {
                return 1;// 升序。改为-1为降序
            } else if (this.position == arg0.position) {
                arg0.t = this.t;// 替换成最新的
                return 0;
            } else {
                return -1;
            }
        }

    }

    /**
     * DataCacheItem辅助类
     *
     * @author PanJunLong
     */
    private class DataCacheItemUtils {
        /**
         * 把普通list对象转换成DataCacheItem的set集合,让其具备自动排序能力
         *
         * @param list
         * @return
         */
        public Set<DataCacheItem> listToSet(List<T> list) {
            if (list == null) {
                return null;
            }
            Set<DataCacheItem> set = new TreeSet<DataCacheItem>();
            for (int i = 0; i < list.size(); i++) {
                set.add(new DataCacheItem(i, list.get(i)));
            }
            return set;
        }

    }

}
