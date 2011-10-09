package de.dennisboldt.okular;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Okular Page
 *
 * @author Dennis Boldt
 *
 */
public class Page {

	private List<Annotation> annotations = null;
	private Integer number = null;

	/**
	 *
	 * @param number The page number which is represented by an instance
	 */
	public Page(Integer number) {
		this.annotations = new LinkedList<Annotation>();
		this.number = number;
	}

	/**
	 * Adds an annotation to this page instance
	 * @param a The annotation
	 */
	public void addAnnotation(Annotation a) {
		this.annotations.add(a);
	}

	public int getNumber() {
		return number.intValue();
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}
}
