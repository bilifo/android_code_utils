package 一个双向同步的观察者代理类;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

//还不是通用,看后期把<T extends Collection>换成<T>
//而且array.clear();array.addAll();的方式来更新集合,由set转为list时,会打乱原先list的顺序.看来还是得分set和list的版本

/**
 * 一個雙向的觀察者集合,但自身改變時,會同步改變其他觀察者,反之亦然.
 * 使用方式:
 * 1/建立InvocationHandler代理类
 * 		ListInvocationHandler<Set> a1 = new ListInvocationHandler<Set>(tree);
 *		ListInvocationHandler<List> a2 = new ListInvocationHandler<Collection>(array);
 * 2/添加觀察者
 * 		a1.addObserver(a2);
 *		a2.addObserver(a1);
 *3/用Proxy.newProxyInstance创建代理类对象
 *		Set<String> list2 = (Set<String>)Proxy.newProxyInstance(Set.class.getClassLoader(), new Class[] { Set.class }, a1);
 *		List<String> list3 = (List<String>) Proxy.newProxyInstance(List.class.getClassLoader(), new Class[] { List.class }, a2);
 *4/操作list2\list3,将会自动同步
 *
 * @author jlpan
 *
 * @param <T>
 */
public class ListInvocationHandler<T extends Collection> extends Observable
		implements Observer, InvocationHandler {
	T array;

	public ListInvocationHandler(T array) {
		super();
		this.array = array;
	}
	public ListInvocationHandler() {
		super();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = method.invoke(array, args);
		setChange();
		return result;

	}

	@Override
	public void update(Observable observable, Object data) {
		array.clear();
		array.addAll( (Collection) data);
//		array=(T) data;
		if (observable.hasChanged()) {
			observable.notifyObservers(data);
		}
	}
	
	/**
	 * 生成代理对象
	 * @param target 
	 * @param interfaces 
	 * @return
	 */
	public T bind(T target){
		//奇怪现象:tree = (Set<String>) a1.bind(tree);可以通过
		//但array = (List<String>) a2.bind(array);会出现转换异常,待研究
		this.array = target;
		return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(), this);
	}

	private void setChange() {
		if (this.hasChanged() == false) {
			this.setChanged();
			this.notifyObservers(array);
		}
	}

}
