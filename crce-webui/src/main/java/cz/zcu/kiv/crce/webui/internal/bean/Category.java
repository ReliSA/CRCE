package cz.zcu.kiv.crce.webui.internal.bean;

import java.io.Serializable;

/**
 * Bean class for category (tag of resources).
 * Category is represented by name and number of occurences in the store.
 * @author Jan Reznicek
 * @version 1.0
 */
public class Category implements Serializable, Comparable<Category>{
	private static final long serialVersionUID = 1L;
	/**
	 * Name of category (jar,zip..)
	 */
	private String name;
	/**
	 * Number of occurences of category in the store.
	 */
	private int count;
	
	public Category(String name) {
		this.name = name;
		count = 1;
	}

	public String getName() {
		return name;
	}
	public int getCount() {
		return count;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public boolean equals(Object obj)	{
		if(this == obj) return true;
		if((obj == null) || (obj.getClass() != this.getClass())) return false;
		
		Category otherCat = (Category)obj;
		if (otherCat.getName().equals(this.getName())) return true;
		else return false;
    }
	
	@Override
	public int hashCode() {
		
		return name.hashCode();		
	}
	
	public int compareTo(Category cat) {
	return this.getName().compareTo(cat.getName());
	}

}
