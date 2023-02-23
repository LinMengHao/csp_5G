package com.xzkj.taskChannel.redis.common;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * 队列的实现类
 * @author admin
 */
public class SerializableQueue<M extends Serializable> {

	// private ConcurrentLinkedQueue<M> queue = new ConcurrentLinkedQueue<M>();
	private LinkedList<M> queue = new LinkedList<M>();

	/**
	 * 添加到队列头部
	 * @param x
	 */
	public synchronized void addFirst(M x) {
		if (x != null) {
			queue.addFirst(x);
		}
	}

	/**
	 * 添加到队列头部
	 * @param x
	 */
	public synchronized void addFirst(List<M> x) {
		if (x != null && x.size() > 0) {
			for (int i = 0; i < x.size(); i++) {
				queue.addFirst(x.get(i));
			}
		}
	}

	/**
	 * 添加到队列尾部
	 * @param x
	 */
	public synchronized void addLast(M x) {
		if (x != null) {
			queue.add(x);
		}
	}

	/**
	 * 添加到队列尾部
	 * @param x
	 */
	public synchronized void addLast(List<M> x) {
		if (x != null && x.size() > 0) {
			queue.addAll(x);
		}
	}

	/**
	 * 从头部弹出
	 * @param x
	 */
	public synchronized M pop() {
		return queue.poll();
	}

	/**
	 * 弹出所有
	 * @param x
	 */
	public synchronized List<M> popAll() {
		if (!queue.isEmpty()) {
			List<M> ret = new Vector<M>();
			while (true) {
				M m = queue.poll();
				if (m != null) {
					ret.add(m);
				} else {
					break;
				}
			}
			queue.clear();
			return ret;
		}
		return null;
	}

	public int size() {
		return queue.size();
	}

	public boolean empty() {
		return queue.isEmpty();
	}

}
